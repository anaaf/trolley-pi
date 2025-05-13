import logging
import threading
import time
from decimal import Decimal, ROUND_HALF_UP

import RPi.GPIO as GPIO
import serial
import requests
from hx711 import HX711

# Configuration constants (consider moving to a config file or environment variables)
API_URL = "http://56.228.26.246:8080/v1/cart/barcodeScan"
CART_UUID = "da4c6590-b0bf-4ee3-b436-fa868188f520"
TROLLEY_UUID = "0771c388-a12f-4499-afef-cf3401887722"
SERIAL_PORT = "/dev/ttyAMA0"
BAUDRATE = 9600
SCALE_RATIO = 99.6
READ_INTERVAL = 0.1
WEIGHT_INTERVAL = 1.0


class BarcodeScanner:
    def __init__(self,
                 cart_uuid: str,
                 trolley_uuid: str,
                 api_url: str,
                 serial_port: str = SERIAL_PORT,
                 baudrate: int = BAUDRATE,
                 scale_ratio: float = SCALE_RATIO):
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

        # Serial port will be opened in start()
        self.serial = None

    def start(self) -> None:
        """Start the barcode reading thread."""
        self._open_serial()
        threading.Thread(target=self._scan_loop, daemon=True).start()
        logging.info("Barcode scanner started.")

    def _open_serial(self) -> None:
        try:
            self.serial = serial.Serial(port=self.serial_port,
                                        baudrate=self.baudrate,
                                        timeout=1)
            logging.info(f"Opened serial port {self.serial_port} at {self.baudrate} baud.")
        except serial.SerialException as e:
            logging.exception("Unable to open serial port.")
            raise

    def _scan_loop(self) -> None:
        """Continuously read barcodes and send API requests."""
        while not self._stop_event.is_set():
            try:
                if self.serial.in_waiting:
                    raw = self.serial.readline()
                    barcode = raw.decode('utf-8', errors='ignore').strip()

                    if barcode:
                        logging.info(f"Scanned barcode: {barcode}")
                        weight = self._get_weight()
                        logging.info(f"Weight at scan: {weight} kg")
                        self._post_scan(barcode, weight)
                time.sleep(READ_INTERVAL)

            except serial.SerialException:
                logging.exception("Serial communication error.")
                break

    def _get_weight(self) -> Decimal:
        """Return the current weight in kg, rounded to 3 decimal places."""
        raw_grams = self.hx.get_weight_mean()
        kg = raw_grams / 1000
        return Decimal(str(kg)).quantize(Decimal('0.001'), ROUND_HALF_UP)

    def _post_scan(self, barcode: str, weight: Decimal) -> None:
        """Send the scan event to the REST API."""
        payload = {
            "cartUuid": self.cart_uuid,
            "trolleyUuid": self.trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight)
        }
        try:
            response = requests.post(self.api_url, json=payload, timeout=5)
            response.raise_for_status()
            logging.info(f"API call succeeded (status {response.status_code}).")
        except requests.RequestException:
            logging.exception("Failed to send scan to API.")

    def stop(self) -> None:
        """Cleanly shut down serial connection and GPIO."""
        self._stop_event.set()
        if self.serial and self.serial.is_open:
            self.serial.close()
            logging.info("Serial port closed.")
        GPIO.cleanup()
        logging.info("GPIO cleaned up.")


def main() -> None:
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s %(levelname)s: %(message)s'
    )

    scanner = BarcodeScanner(
        cart_uuid=CART_UUID,
        trolley_uuid=TROLLEY_UUID,
        api_url=API_URL
    )

    try:
        scanner.start()
        while True:
            weight = scanner._get_weight()
            logging.info(f"Current weight: {weight} kg")
            time.sleep(WEIGHT_INTERVAL)

    except KeyboardInterrupt:
        logging.info("Keyboard interrupt received; stopping scanner.")
        scanner.stop()


if __name__ == "__main__":
    main()
