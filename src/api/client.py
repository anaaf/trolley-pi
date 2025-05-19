import logging
import requests
from typing import Optional
from decimal import Decimal

class APIClient:
    def __init__(self, api_url: str):
        """Initialize API client with server URL."""
        self.api_url = api_url.rstrip('/')  # Remove trailing slash if present

    def register_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, timestamp: float) -> bool:
        """Register a barcode scan with the API."""
        try:
            response = requests.post(
                f"{self.api_url}/api/register-scan",
                json={
                    "cartUuid": cart_uuid,
                    "trolleyUuid": trolley_uuid,
                    "barcodeNumber": barcode,
                    "timestamp": timestamp
                }
            )
            response.raise_for_status()
            data = response.json()
            if data.get('success'):
                logging.info(data.get('message'))
                return True
            else:
                logging.error(data.get('message'))
                return False
        except requests.exceptions.RequestException as e:
            logging.error(f"Failed to register scan: {e}")
            return False

    def add_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Add an item with its weight."""
        try:
            response = requests.post(
                f"{self.api_url}/api/add-item",
                json={
                    "cartUuid": cart_uuid,
                    "trolleyUuid": trolley_uuid,
                    "barcode": barcode,
                    "weight": float(weight) if weight is not None else None,
                    "timestamp": timestamp
                }
            )
            response.raise_for_status()
            data = response.json()
            if data.get('success'):
                logging.info(data.get('message'))
                return True
            else:
                logging.error(data.get('message'))
                return False
        except requests.exceptions.RequestException as e:
            logging.error(f"Failed to add item: {e}")
            return False

    def remove_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, timestamp: float) -> bool:
        """Remove an item from the cart."""
        try:
            response = requests.post(
                f"{self.api_url}/api/remove-item",
                json={
                    "cartUuid": cart_uuid,
                    "trolleyUuid": trolley_uuid,
                    "barcode": barcode,
                    "timestamp": timestamp
                }
            )
            response.raise_for_status()
            data = response.json()
            if data.get('success'):
                logging.info(data.get('message'))
                return True
            else:
                logging.error(data.get('message'))
                return False
        except requests.exceptions.RequestException as e:
            logging.error(f"Failed to remove item: {e}")
            return False

    def get_cart(self) -> dict:
        """Get the current cart status."""
        try:
            response = requests.get(f"{self.api_url}/api/cart")
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            logging.error(f"Failed to get cart: {e}")
            return {}

    def get_scan_history(self) -> list:
        """Get the scan history."""
        try:
            response = requests.get(f"{self.api_url}/api/scan-history")
            response.raise_for_status()
            return response.json()
        except requests.exceptions.RequestException as e:
            logging.error(f"Failed to get scan history: {e}")
            return [] 