import logging
from decimal import Decimal, ROUND_HALF_UP
import time
from typing import Optional, Callable
import threading

from src.config import config

if config.USE_MOCK:
    from src.libs.mocks import HX711, GPIO
else:
    from hx711 import HX711
    import RPi.GPIO as GPIO

class WeightSensor:
    def __init__(self, dout_pin: int = 6, pd_sck_pin: int = 5):
        """Initialize the weight sensor with specified GPIO pins."""
        self._setup_gpio()
        self._setup_scale(dout_pin, pd_sck_pin)
        self.last_weight: Optional[Decimal] = None
        self._stop_event = threading.Event()
        self._weight_change_callback: Optional[Callable[[Decimal, Decimal], None]] = None
        self._initialize_weight()
    
    def start(self):
        """Start weight monitoring in a background thread."""
        threading.Thread(target=self._monitor_weight, daemon=True).start()
        logging.info("Weight sensor started")

    def _monitor_weight(self):
        """Monitor weight in background thread."""
        while not self._stop_event.is_set():
            weight = self.get_weight()
            logging.info(f"Current weight: {weight} kg")
            if self.has_significant_weight_change() and self._weight_change_callback:
                self._weight_change_callback(self.last_weight, weight)
            time.sleep(config.WEIGHT_INTERVAL)

    def stop(self):
        """Stop weight monitoring."""
        self._stop_event.set()
        self.cleanup()

    def _setup_gpio(self) -> None:
        """Configure GPIO mode."""
        GPIO.setmode(GPIO.BCM)

    def _setup_scale(self, dout_pin: int, pd_sck_pin: int) -> None:
        """Initialize the HX711 scale with specified pins."""
        self.hx = HX711(dout_pin=dout_pin, pd_sck_pin=pd_sck_pin)
        self.hx.zero()
        self.hx.set_scale_ratio(config.SCALE_RATIO)

    def _initialize_weight(self) -> None:
        """Initialize the last weight reading."""
        self.last_weight = self.get_weight()
        logging.info(f"Initial weight: {self.last_weight} kg")

    def get_weight(self) -> Decimal:
        """Get current weight in kg with 3 decimal places."""
        grams = self.hx.get_weight_mean()
        kg = grams / 1000
        return Decimal(str(kg)).quantize(Decimal('0.001'), ROUND_HALF_UP)

    def has_significant_weight_change(self) -> bool:
        """Check if weight change exceeds threshold."""
        current_weight = self.get_weight()
        if self.last_weight is None:
            self.last_weight = current_weight
            return False

        delta = (current_weight - self.last_weight).copy_abs()
        has_change = delta >= config.WEIGHT_THRESHOLD_KG
        
        if has_change:
            logging.info(
                f"Weight change {delta} from {self.last_weight} kg to {current_weight} kg >= threshold {config.WEIGHT_THRESHOLD_KG} kg"
            )
            self.last_weight = current_weight
        else:
            logging.info(
                f"Weight change {delta} from {self.last_weight} kg to {current_weight} kg < threshold {config.WEIGHT_THRESHOLD_KG} kg"
            )
        
        return has_change

    def on_weight_change(self, callback: Callable[[Decimal, Decimal], None]) -> None:
        """Register a callback function to be called when significant weight changes occur.
        
        Args:
            callback: Function that takes (old_weight, new_weight) as arguments
        """
        self._weight_change_callback = callback

    def cleanup(self) -> None:
        """Clean up GPIO resources."""
        GPIO.cleanup()
        logging.info("GPIO cleaned up.") 