import logging
import threading
import time
import requests
from typing import Optional

from src.sensors.barcode_scanner import BarcodeScanner
from src.sensors.weight_sensor import WeightSensor
from src.config import config
from src.queue.event_queue import EventQueue
from src.events import BarcodeScanEvent, ScanEvent

class Trolley:
    def __init__(
        self,
        cart_uuid: str,
        trolley_uuid: str,
        api_url: str,
        serial_port: str = config.SERIAL_PORT,
        baudrate: int = config.BAUDRATE,
    ):
        self.cart_uuid = cart_uuid
        self.trolley_uuid = trolley_uuid
        self.api_url = api_url
        self._stop_event = threading.Event()
        
        # Initialize sensors
        self.barcode_scanner = BarcodeScanner(
            cart_uuid=cart_uuid,
            trolley_uuid=trolley_uuid,
            api_url=api_url,
            serial_port=serial_port,
            baudrate=baudrate,
        )
        self.weight_sensor = WeightSensor()

    def start(self):
        """Start all services and processing threads."""
        logging.debug("Starting trolley services...")
        
        # Start sensors
        logging.debug("Starting barcode scanner...")
        self.barcode_scanner.start()
        logging.debug("Starting weight sensor...")
        self.weight_sensor.start()
        
        # Start event processing thread
        try:
            logging.debug("Creating event processing thread...")
            self.event_thread = threading.Thread(
                target=self._process_events,
                daemon=True,
                name="event_processor"
            )
            self.event_thread.start()
            logging.debug(f"Event processing thread started: {self.event_thread.name} (daemon={self.event_thread.daemon})")
            
            # Verify thread is alive
            if not self.event_thread.is_alive():
                logging.error("Event processing thread failed to start")
                raise RuntimeError("Event processing thread failed to start")
                
        except Exception as e:
            logging.exception("Failed to start event processing thread")
            raise
        
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
                    if self._send_to_api(scan_event):
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

    def _send_to_api(self, event: ScanEvent) -> bool:
        """Send event data to API. Returns True if successful."""
        payload = {
            "cartUuid": self.cart_uuid,
            "trolleyUuid": self.trolley_uuid,
            "barcodeNumber": event.barcode,
            "weight": float(event.weight) if event.weight is not None else 0.0,
            "timestamp": event.timestamp
        }
        try:
            logging.info(f"Sending payload: {payload}")
            resp = requests.post(self.api_url, json=payload, timeout=5)
            resp.raise_for_status()
            logging.info(f"API success ({resp.status_code}).")
            return True
        except requests.RequestException:
            logging.exception("API request failed.")
            return False

    def stop(self):
        """Stop all services and clean up."""
        logging.debug("Stopping trolley...")
        self._stop_event.set()
        if hasattr(self, 'event_thread'):
            logging.debug(f"Waiting for event thread {self.event_thread.name} to stop...")
            self.event_thread.join(timeout=5)
        self.barcode_scanner.stop()
        self.weight_sensor.stop()
        logging.info("Trolley stopped") 