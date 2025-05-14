from decimal import Decimal
import random

class HX711:
    def __init__(self, dout_pin, pd_sck_pin):
        print(f"[MOCK HX711] init pins {dout_pin}, {pd_sck_pin}")

    def zero(self):
        print("[MOCK HX711] zero()")

    def set_scale_ratio(self, ratio):
        print(f"[MOCK HX711] set_scale_ratio({ratio})")

    def get_weight_mean(self):
        # return a random gram value between 0 and 2000g
        return random.uniform(0, 2000)
