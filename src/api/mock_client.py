import logging
from typing import Optional
from decimal import Decimal

class MockAPIClient:
    def __init__(self, api_url: str):
        """Initialize mock API client.
        
        Args:
            api_url: The API URL (not used in mock implementation but kept for interface consistency)
        """
        self.api_url = api_url
        self.last_payload = None
        self.should_fail = False

    def register_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, timestamp: float) -> bool:
        """Mock implementation of register_scan."""
        if self.should_fail:
            logging.error("Mock API client configured to fail")
            return False
            
        self.last_payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "timestamp": timestamp
        }
        logging.info(f"Mock register_scan called with payload: {self.last_payload}")
        return True

    def add_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Mock implementation of add_item."""
        if self.should_fail:
            logging.error("Mock API client configured to fail")
            return False
            
        self.last_payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight) if weight is not None else 0.0,
            "timestamp": timestamp
        }
        logging.info(f"Mock add_item called with payload: {self.last_payload}")
        return True 