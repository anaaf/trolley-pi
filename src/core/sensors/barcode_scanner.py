import logging
import sys
import threading
import time
import re
from typing import Optional, Callable

from src.config import config

if config.USE_MOCK:
    from src.libs.mocks import Serial as serial
else:
    import serial

from src.core.events import BarcodeScanEvent

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
        self._scan_callback: Optional[Callable[[BarcodeScanEvent], None]] = None

    def on_scan(self, callback: Callable[[BarcodeScanEvent], None]) -> None:
        """Register a callback for barcode scan events."""
        self._scan_callback = callback

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

    def _is_valid_barcode(self, code: str) -> bool:
        """Check if the scanned code is a valid barcode.
        
        Args:
            code: The scanned code to validate
            
        Returns:
            bool: True if the code is a valid barcode, False otherwise
        """
        # Check if it's a URL (QR code)
        if code.startswith(('http://', 'https://')):
            logging.debug(f"Ignoring QR code URL: {code}")
            return False
            
        # Check if it's a valid barcode format (numeric or alphanumeric)
        # Most common barcode formats are numeric or alphanumeric
        if not re.match(r'^[A-Za-z0-9]+$', code):
            logging.debug(f"Ignoring invalid barcode format: {code}")
            return False
            
        # Check length (most barcodes are between 8 and 13 characters)
        if not (8 <= len(code) <= 13):
            logging.debug(f"Ignoring barcode with invalid length: {code}")
            return False
            
        return True

    def _handle_scan(self, barcode: str) -> None:
        """Process a barcode scan."""
        logging.debug(f"Handling barcode scan: {barcode}")
        
        if not self._is_valid_barcode(barcode):
            return
            
        try:
            event = BarcodeScanEvent(
                barcode=barcode,
                timestamp=time.time()
            )
            logging.debug(f"Created barcode event: {event}")
            if self._scan_callback:
                self._scan_callback(event)
            else:
                logging.warning("No scan callback registered")
        except Exception as e:
            logging.exception("Failed to handle barcode scan")
            raise

    def stop(self) -> None:
        """Stop the scanner and clean up resources."""
        self._stop_event.set()
        if self.serial_conn:
            self.serial_conn.close()
        logging.info("BarcodeScanner stopped") 