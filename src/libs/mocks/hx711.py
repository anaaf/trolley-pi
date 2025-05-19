from decimal import Decimal
import os
import logging

PIPE_PATH = "/tmp/weight_pipe"

class HX711:
    def __init__(self, dout_pin, pd_sck_pin):
        print(f"[MOCK HX711] init pins {dout_pin}, {pd_sck_pin}")
        self._last_weight = Decimal('0')
        # Create the named pipe if it doesn't exist
        if not os.path.exists(PIPE_PATH):
            os.mkfifo(PIPE_PATH)

    def zero(self):
        print("[MOCK HX711] zero()")

    def set_scale_ratio(self, ratio):
        print(f"[MOCK HX711] set_scale_ratio({ratio})")

    def get_weight_mean(self):
        """Read weight from the named pipe."""
        try:
            with open(PIPE_PATH, 'r') as pipe:
                weight_str = pipe.readline().strip()
                if weight_str:
                    self._last_weight = Decimal(weight_str)
                return float(self._last_weight * 1000)  # Convert kg to grams
        except Exception as e:
            logging.error(f"Error reading weight from pipe: {e}")
            return float(self._last_weight * 1000)  # Return last known weight
