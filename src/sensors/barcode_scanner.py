import logging
import sys
import threading
import time
from typing import Optional

import requests

from src.config import config

if config.USE_MOCK:
    from src.mocks import serial
else:
    import serial

class BarcodeScanner:
    def __init__(
        self,
        cart_uuid: str,
        trolley_uuid: str,
        api_url: str,
        serial_port: str = config.SERIAL_PORT,
        baudrate: int = config.BAUDRATE,
    ):
        """Initialize the barcode scanner with specified configuration."""
        self.cart_uuid = cart_uuid
        self.trolley_uuid = trolley_uuid
        self.api_url = api_url
        self.serial_port = serial_port
        self.baudrate = baudrate
        self._stop_event = threading.Event()
        self.serial_conn: Optional[serial.Serial] = None

    def start(self) -> None:
        """Start threads for barcode scanning."""
        try:
            self._open_serial()
            threading.Thread(target=self._scan_loop, daemon=True).start()
        except Exception:
            logging.warning("Serial port unavailable; serial scanning disabled.")

        if config.USE_CONSOLE_INPUT:
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
                if self.serial_conn and self.serial_conn.in_waiting:
                    raw = self.serial_conn.readline()
                    barcode = raw.decode('utf-8', errors='ignore').strip()
                    if barcode:
                        self._handle_scan(barcode)
                time.sleep(config.READ_INTERVAL)
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
                    time.sleep(config.READ_INTERVAL)
                    continue
                barcode = line.strip()
                if barcode:
                    self._handle_scan(barcode)
            except Exception:
                logging.exception("Console input error; stopping console scan.")
                break

    def _handle_scan(self, barcode: str) -> None:
        """Process a barcode scan."""
        logging.info(f"Scanned barcode: {barcode}")
        self._post_scan(barcode)

    def _post_scan(self, barcode: str) -> None:
        """Send scan data to REST API."""
        payload = {
            "cartUuid": self.cart_uuid,
            "trolleyUuid": self.trolley_uuid,
            "barcodeNumber": barcode,
            "weight": 0.350
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