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
        self.pending_removal: bool = False  # Flag to indicate weight drop detected

    def start(self):
        """Start the trolley and all its components."""
        logging.info("Starting trolley...")
        self.barcode_scanner.on_scan(self._handle_barcode_event)
        self.weight_sensor.on_weight_change(self._handle_weight_change)
        self.barcode_scanner.start()
        self.weight_sensor.start()
        logging.info("Trolley started")

    def _handle_barcode_event(self, event: BarcodeScanEvent):
        """Handle a barcode scan event."""
        logging.debug(f"Received barcode event: {event}")
        
        # First check if this is a removal scan
        if self.pending_removal:
            # For removal, directly call remove_item
            if self.api_client.remove_item(
                cart_uuid=self.cart_uuid,
                trolley_uuid=self.trolley_uuid,
                barcode=event.barcode,
                timestamp=event.timestamp
            ):
                self.pending_removal = False
            return
        
        # Normal scan handling
        if self._register_scan(event):
            self.last_scanned_barcode = event.barcode
            self.last_scanned_timestamp = event.timestamp
            logging.info(f"Registered scan for barcode: {event.barcode}")
        else:
            logging.error("Failed to register scan")

    def _register_scan(self, event: BarcodeScanEvent) -> bool:
        """Register a barcode scan with the API."""
        return self.api_client.register_scan(
            cart_uuid=self.cart_uuid,
            trolley_uuid=self.trolley_uuid,
            barcode=event.barcode,
            timestamp=event.timestamp
        )

    def _handle_weight_change(self, old_weight: Decimal, new_weight: Decimal):
        """Handle significant weight changes.
        
        Args:
            old_weight: The previous weight reading
            new_weight: The current weight reading
        """
        # Check for significant weight drop
        if old_weight > new_weight:
            weight_difference = float(old_weight - new_weight)
            logging.info(f"Detected weight drop of {weight_difference:.3f}kg")
            self.pending_removal = True
            logging.info("Please scan the item you want to remove from the cart")
            return

        if self.last_scanned_barcode is None:
            return

        scan_event = ScanEvent(
            barcode=self.last_scanned_barcode,
            timestamp=self.last_scanned_timestamp or time.time(),
            weight=new_weight
        )
        
        if self._add_item(scan_event):
            self.last_scanned_barcode = None
            self.last_scanned_timestamp = None

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
        self.barcode_scanner.stop()
        self.weight_sensor.stop()
        logging.info("Trolley stopped") 