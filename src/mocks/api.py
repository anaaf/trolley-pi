"""
Mock API module for testing.
Simulates API responses when running in mock mode.
"""
import logging
from typing import Dict, Any

class MockAPI:
    def __init__(self):
        self.scan_history: list[Dict[str, Any]] = []
        logging.info("Initialized MockAPI")

    def post_scan(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        """Simulate API response for barcode scan."""
        self.scan_history.append(payload)
        logging.info(f"Mock API received scan: {payload}")
        
        # Simulate successful response
        return {
            "status": "success",
            "message": "Scan recorded successfully",
            "data": {
                "scanId": f"mock-scan-{len(self.scan_history)}",
                "timestamp": "2024-03-21T12:00:00Z",
                **payload
            }
        }

# Create a singleton instance
mock_api = MockAPI() 