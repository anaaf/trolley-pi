import logging
import time

from src.sensors import BarcodeScanner, WeightSensor
from src.config import config

def setup_logging() -> None:
    """Configure logging settings."""
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s %(levelname)s: %(message)s',
    )

def main() -> None:
    """Main entry point for the application."""
    setup_logging()

    # Initialize components
    weight_sensor = WeightSensor()
    scanner = BarcodeScanner(
        cart_uuid=config.CART_UUID,
        trolley_uuid=config.TROLLEY_UUID,
        api_url=config.API_URL,
    )

    try:
        # Start the barcode scanner
        scanner.start()

        # Main loop for weight monitoring
        while True:
            weight = weight_sensor.get_weight()
            logging.info(f"Current weight: {weight} kg")
            time.sleep(config.WEIGHT_INTERVAL)

    except KeyboardInterrupt:
        logging.info("Shutdown requested; stopping service.")
        scanner.stop()
        weight_sensor.cleanup()

if __name__ == "__main__":
    main()
