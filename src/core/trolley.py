import logging
import threading
import time
from typing import Optional
from decimal import Decimal

from src.core.events import BarcodeScanEvent, ScanEvent
from src.core.sensors import BarcodeScanner, WeightSensor
from src.api.client import APIClient

class Trolley:
    def __init__(
        self,
        cart_uuid: str,
        trolley_uuid: str,
        api_client: APIClient,
        barcode_scanner: BarcodeScanner,
        weight_sensor: WeightSensor,
    ):
        """Initialize the trolley with its dependencies."""
        self.cart_uuid = cart_uuid
        self.trolley_uuid = trolley_uuid
        self.api_client = api_client
        self.barcode_scanner = barcode_scanner
        self.weight_sensor = weight_sensor
        self._stop_event = threading.Event()
        self.event_thread: Optional[threading.Thread] = None
        self.last_scanned_barcode: Optional[str] = None
        self.last_scanned_timestamp: Optional[float] = None
        self._scan_lock = threading.Lock()

    def start(self):
        """Start the trolley and all its components."""
        logging.info("Starting trolley...")
        self.barcode_scanner.start()
        self.weight_sensor.start()
        
        # Start event processing thread
        self.event_thread = threading.Thread(target=self._process_events)
        self.event_thread.start()
        logging.info("Trolley started")

    def _process_events(self):
        """Process events from barcode scanner and send to API."""
        try:
            logging.debug(f"Event processing thread {threading.current_thread().name} started")
            while not self._stop_event.is_set():
                try:
                    self._handle_barcode_event()
                    self._handle_weight_change()
                except Exception as e:
                    logging.exception("Error in event processing loop")
        except Exception as e:
            logging.exception("Fatal error in event processing thread")
            raise

    def _handle_barcode_event(self):
        """Handle a barcode scan event."""
        event = self.barcode_scanner.get_event_queue().get(timeout=1)
        if event is None:
            logging.debug("No event received, continuing...")
            return

        logging.debug(f"Received barcode event: {event}")
        
        with self._scan_lock:
            if self.last_scanned_barcode is not None:
                logging.warning("Cannot scan new item until previous item is added to cart")
                self.barcode_scanner.get_event_queue().mark_failed()
                return
            
            if self._register_scan(event):
                self.last_scanned_barcode = event.barcode
                self.last_scanned_timestamp = event.timestamp
                logging.info(f"Registered scan for barcode: {event.barcode}")
                self.barcode_scanner.get_event_queue().mark_processed()
            else:
                logging.error("Failed to register scan")
                self.barcode_scanner.get_event_queue().mark_failed()

    def _register_scan(self, event: BarcodeScanEvent) -> bool:
        """Register a barcode scan with the API."""
        return self.api_client.register_scan(
            cart_uuid=self.cart_uuid,
            trolley_uuid=self.trolley_uuid,
            barcode=event.barcode,
            timestamp=event.timestamp
        )

    def _handle_weight_change(self):
        """Handle significant weight changes."""
        if not self.weight_sensor.has_significant_weight_change():
            return

        with self._scan_lock:
            if self.last_scanned_barcode is None:
                return

            scan_event = self._create_scan_event()
            if self._add_item(scan_event):
                self.last_scanned_barcode = None
                self.last_scanned_timestamp = None

    def _create_scan_event(self) -> ScanEvent:
        """Create a scan event with the current weight."""
        return ScanEvent(
            barcode=self.last_scanned_barcode,
            timestamp=self.last_scanned_timestamp or time.time(),
            weight=self.weight_sensor.get_weight()
        )

    def _add_item(self, scan_event: ScanEvent) -> bool:
        """Add an item with its weight."""
        success = self.api_client.add_item(
            cart_uuid=self.cart_uuid,
            trolley_uuid=self.trolley_uuid,
            barcode=scan_event.barcode,
            weight=scan_event.weight,
            timestamp=scan_event.timestamp
        )
        
        if success:
            logging.debug("Item added successfully")
        else:
            logging.debug("Failed to add item")
        
        return success

    def stop(self):
        """Stop all services and clean up."""
        logging.debug("Stopping trolley...")
        self._stop_event.set()
        if self.event_thread:
            logging.debug(f"Waiting for event thread {self.event_thread.name} to stop...")
            self.event_thread.join(timeout=5)
        self.barcode_scanner.stop()
        self.weight_sensor.stop()
        logging.info("Trolley stopped") 