import logging
import time

from src.core.trolley import Trolley
from src.config import config
from src.core.sensors import BarcodeScanner, WeightSensor
from src.api.client import APIClient
from src.api.mock_client import MockAPIClient

def setup_logging() -> None:
    """Configure logging settings."""
    logging.basicConfig(
        level=logging.DEBUG,
        format='%(asctime)s %(levelname)s: %(message)s',
    )

def main() -> None:
    """Main entry point for the application."""
    setup_logging()
    logging.info("Starting Trolley... Welcome PI")

    # Initialize components
    api_client = MockAPIClient(config.API_URL) if config.USE_MOCK else APIClient(config.API_URL)
    
    barcode_scanner = BarcodeScanner(
        cart_uuid=config.CART_UUID,
        trolley_uuid=config.TROLLEY_UUID,
        api_url=config.API_URL,
        serial_port=config.SERIAL_PORT,
        baudrate=config.BAUDRATE,
    )
    
    weight_sensor = WeightSensor()

    # Initialize trolley with dependencies
    trolley = Trolley(
        cart_uuid=config.CART_UUID,
        trolley_uuid=config.TROLLEY_UUID,
        api_client=api_client,
        barcode_scanner=barcode_scanner,
        weight_sensor=weight_sensor,
    )

    try:
        trolley.start()
        logging.debug("Trolley started, entering main loop")
        
        # Keep the main thread alive
        while True:
            time.sleep(1)

    except KeyboardInterrupt:
        logging.info("Shutdown requested; stopping trolley.")
        trolley.stop()
    except Exception as e:
        logging.exception("Unexpected error in main loop")
        trolley.stop()

if __name__ == "__main__":
    main()
