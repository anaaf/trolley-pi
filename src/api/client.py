import logging
import requests
from typing import Optional
from decimal import Decimal

class APIClient:
    def __init__(self, api_url: str):
        self.api_url = api_url

    def send_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Send scan data to API. Returns True if successful."""
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