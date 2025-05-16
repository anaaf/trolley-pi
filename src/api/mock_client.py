import logging
from typing import Optional
from decimal import Decimal

class MockAPIClient:
    def __init__(self, api_url: str):
        self.api_url = api_url

    def send_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Mock sending scan data to API. Always returns True."""
        payload = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "weight": float(weight) if weight is not None else 0.0,
            "timestamp": timestamp
        }
        logging.info(f"[MOCK] Would send payload: {payload}")
        return True 