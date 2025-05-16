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
                    # Get barcode event
                    logging.debug("Waiting for barcode event...")
                    event = self.barcode_scanner.get_event_queue().get(timeout=1)
                    if event is None:
                        logging.debug("No event received, continuing...")
                        continue
                    
                    logging.debug(f"Received barcode event: {event}")
                    
                    # Create scan event with weight
                    scan_event = ScanEvent(
                        barcode=event.barcode,
                        timestamp=event.timestamp,
                        weight=self.weight_sensor.get_weight()
                    )
                    logging.debug(f"Created scan event with weight: {scan_event}")
                    
                    # Send to API
                    if self.api_client.send_scan(
                        cart_uuid=self.cart_uuid,
                        trolley_uuid=self.trolley_uuid,
                        barcode=scan_event.barcode,
                        weight=scan_event.weight,
                        timestamp=scan_event.timestamp
                    ):
                        logging.debug("API call successful")
                        self.barcode_scanner.get_event_queue().mark_processed()
                    else:
                        logging.debug("API call failed")
                        self.barcode_scanner.get_event_queue().mark_failed()
                        
                except Exception as e:
                    logging.exception("Error processing event")
                    self.barcode_scanner.get_event_queue().mark_failed()
        except Exception as e:
            logging.exception("Fatal error in event processing thread")
            raise

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