import logging
import requests
from typing import Optional
from decimal import Decimal

class APIClient:
    def __init__(self, api_url: str):
        self.api_url = api_url

    def register_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, timestamp: float) -> bool:
        """Register a barcode scan. Returns True if successful."""
        payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "timestamp": timestamp
        }
        try:
            logging.info(f"Registering scan: {payload}")
            # resp = requests.post(f"{self.api_url}/register", json=payload, timeout=5)
            # resp.raise_for_status()
            resp = {
                "status_code": 200
            }
            
            logging.info(f"Register scan success ({resp.get('status_code')})")
            return True
        except requests.RequestException:
            logging.exception("Register scan request failed.")
            return False

    def add_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Add an item to the cart. Returns True if successful."""
        payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight) if weight is not None else 0.0,
            "timestamp": timestamp
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
        
    def remove_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Remove an item to the cart. Returns True if successful."""
        payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight) if weight is not None else 0.0,
            "timestamp": timestamp
        }
    
        try:
            logging.info(f"Removing item - Sending payload: {payload}")
            resp = requests.post(self.api_url, json=payload, timeout=5)
            resp.raise_for_status()
            logging.info(f"API success ({resp.status_code}).")
            return True
        except requests.RequestException:
            logging.exception("API request failed.")
            return False 