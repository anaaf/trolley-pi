import os
import time
import logging

# Create the named pipe if it doesn't exist
PIPE_PATH = "/tmp/barcode_pipe"
if not os.path.exists(PIPE_PATH):
    os.mkfifo(PIPE_PATH)

def send_barcode(barcode: str) -> None:
    """Send a barcode through the named pipe."""
    try:
        with open(PIPE_PATH, 'w') as pipe:
            pipe.write(f"{barcode}\n")
            pipe.flush()
    except Exception as e:
        logging.error(f"Error sending barcode: {e}")

if __name__ == "__main__":
    # Example usage
    while True:
        try:
            barcode = input("Enter barcode (or 'q' to quit): ")
            if barcode.lower() == 'q':
                break
            send_barcode(barcode)
        except KeyboardInterrupt:
            break 