import os
import time
import logging
from decimal import Decimal

# Create the named pipe if it doesn't exist
PIPE_PATH = "/tmp/weight_pipe"
if not os.path.exists(PIPE_PATH):
    os.mkfifo(PIPE_PATH)

def send_weight(weight: Decimal) -> None:
    """Send a weight value through the named pipe."""
    try:
        with open(PIPE_PATH, 'w') as pipe:
            pipe.write(f"{weight}\n")
            pipe.flush()
    except Exception as e:
        logging.error(f"Error sending weight: {e}")

if __name__ == "__main__":
    # Example usage
    while True:
        try:
            weight = input("Enter weight in kg (or 'q' to quit): ")
            if weight.lower() == 'q':
                break
            weight_decimal = Decimal(weight)
            send_weight(weight_decimal)
        except ValueError:
            print("Invalid weight value. Please enter a number.")
        except KeyboardInterrupt:
            break 