import logging
import threading
import time
import sys
from decimal import Decimal, ROUND_HALF_UP
import os
import requests

USE_MOCK = os.getenv("USE_MOCK") == "1"

if USE_MOCK:
    # Point at our local stub modules
    import mocks.GPIO as GPIO
    from mocks.hx711 import HX711
    import mocks.serial as serial
else:
    import RPi.GPIO as GPIO
    from hx711 import HX711
    import serial
# Configuration constants (can move to env/config)
API_URL = "http://56.228.26.246:8080/v1/cart/barcodeScan"
CART_UUID = "da4c6590-b0bf-4ee3-b436-fa868188f520"
TROLLEY_UUID = "0771c388-a12f-4499-afef-cf3401887722"
SERIAL_PORT = "/dev/ttyAMA0"
BAUDRATE = 9600
SCALE_RATIO = 99.6
READ_INTERVAL = 0.1
WEIGHT_INTERVAL = 1.0
USE_CONSOLE_INPUT = True  # listen on stdin for keyboard-wedge scanners
WEIGHT_THRESHOLD_KG = Decimal('0.010')  # 10 grams


class BarcodeScanner:
    def __init__(
        self,
        cart_uuid: str,
        trolley_uuid: str,
        api_url: str,
        serial_port: str = SERIAL_PORT,
        baudrate: int = BAUDRATE,
        scale_ratio: float = SCALE_RATIO,
    ):
        self.cart_uuid = cart_uuid
        self.trolley_uuid = trolley_uuid
        self.api_url = api_url
        self.serial_port = serial_port
        self.baudrate = baudrate
        self.scale_ratio = scale_ratio
        self._stop_event = threading.Event()

        # Initialize GPIO and scale
        GPIO.setmode(GPIO.BCM)
        self.hx = HX711(dout_pin=6, pd_sck_pin=5)
        self.hx.zero()
        self.hx.set_scale_ratio(self.scale_ratio)

        # Track last weight to enforce threshold
        self.last_weight = self._get_weight()
        logging.info(f"Initial weight: {self.last_weight} kg")

        self.serial_conn = None

    def start(self) -> None:
        """Start threads for barcode scanning."""
        try:
            self._open_serial()
            threading.Thread(target=self._scan_loop, daemon=True).start()
        except Exception:
            logging.warning("Serial port unavailable; serial scanning disabled.")

        if USE_CONSOLE_INPUT:
            threading.Thread(target=self._console_loop, daemon=True).start()
            logging.info("Console scanning enabled.")

        logging.info("BarcodeScanner service running.")

    def _open_serial(self) -> None:
        """Open serial port to barcode device."""
        self.serial_conn = serial.Serial(
            port=self.serial_port,
            baudrate=self.baudrate,
            timeout=1,
        )
        logging.info(f"Serial port {self.serial_port}@{self.baudrate} opened.")

    def _scan_loop(self) -> None:
        """Continuously read from serial port."""
        while not self._stop_event.is_set():
            try:
                if self.serial_conn.in_waiting:
                    raw = self.serial_conn.readline()
                    barcode = raw.decode('utf-8', errors='ignore').strip()
                    if barcode:
                        self._handle_scan(barcode)
                time.sleep(READ_INTERVAL)
            except serial.SerialException:
                logging.exception("Serial error; stopping serial scan.")
                break

    def _console_loop(self) -> None:
        """Read barcodes from stdin."""
        logging.info("Listening for console input...")
        while not self._stop_event.is_set():
            try:
                line = sys.stdin.readline()
                if not line:
                    time.sleep(READ_INTERVAL)
                    continue
                barcode = line.strip()
                if barcode:
                    self._handle_scan(barcode)
            except Exception:
                logging.exception("Console input error; stopping console scan.")
                break

    def _handle_scan(self, barcode: str) -> None:
        """Process a barcode scan if weight change exceeds threshold."""
        logging.info(f"Scanned barcode: {barcode}")
        weight = self._get_weight()
        delta = (weight - self.last_weight).copy_abs()
        if delta >= WEIGHT_THRESHOLD_KG:
            logging.info(
                f"Weight change {delta} from {self.last_weight} kg to {weight} kg >= threshold {WEIGHT_THRESHOLD_KG} kg. Posting scan."
            )
            self._post_scan(barcode, weight)
            self.last_weight = weight
        else:
            logging.info(
                f"Weight change {delta} from {self.last_weight} kg to {weight} kg < threshold {WEIGHT_THRESHOLD_KG} kg; skipping API call."
            )

    def _get_weight(self) -> Decimal:
        """Return current weight in kg (3 decimal places)."""
        grams = self.hx.get_weight_mean()
        kg = grams / 1000
        return Decimal(str(kg)).quantize(Decimal('0.001'), ROUND_HALF_UP)

    def _post_scan(self, barcode: str, weight: Decimal) -> None:
        """Send scan data to REST API."""
        payload = {
            "cartUuid": self.cart_uuid,
            "trolleyUuid": self.trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight),
        }
        try:
            logging.info(f"Sending payload: {payload}")
            resp = requests.post(self.api_url, json=payload, timeout=5)
            resp.raise_for_status()
            logging.info(f"API success ({resp.status_code}).")
        except requests.RequestException:
            logging.exception("API request failed.")

    def stop(self) -> None:
        """Stop scanning and clean up resources."""
        self._stop_event.set()
        if self.serial_conn and self.serial_conn.is_open:
            self.serial_conn.close()
            logging.info("Serial port closed.")
        GPIO.cleanup()
        logging.info("GPIO cleaned up.")


def main() -> None:
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s %(levelname)s: %(message)s',
    )

    scanner = BarcodeScanner(
        cart_uuid=CART_UUID,
        trolley_uuid=TROLLEY_UUID,
        api_url=API_URL,
    )

    try:
        scanner.start()
        while True:
            # Periodic weight logging
            weight = scanner._get_weight()
            logging.info(f"Current weight: {weight} kg")
            time.sleep(WEIGHT_INTERVAL)
    except KeyboardInterrupt:
        logging.info("Shutdown requested; stopping service.")
        scanner.stop()


if __name__ == "__main__":
    main()
