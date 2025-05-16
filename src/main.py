import logging
import time

from src.services.trolley import Trolley
from src.config import config

def setup_logging() -> None:
    """Configure logging settings."""
    logging.basicConfig(
        level=logging.DEBUG,
        format='%(asctime)s %(levelname)s: %(message)s',
    )

def main() -> None:
    """Main entry point for the application."""
    setup_logging()

    logging.info("Starting Trolley...")

    # Initialize trolley
    trolley = Trolley(
        cart_uuid=config.CART_UUID,
        trolley_uuid=config.TROLLEY_UUID,
        api_url=config.API_URL,
    )

    try:
        trolley.start()
        logging.debug("Trolley started, entering main loop")
        
        # Keep the main thread alive
        while True:
            time.sleep(1)
            logging.debug("Main loop tick")

    except KeyboardInterrupt:
        logging.info("Shutdown requested; stopping trolley.")
        trolley.stop()
    except Exception as e:
        logging.exception("Unexpected error in main loop")
        trolley.stop()


if __name__ == "__main__":
    main()
