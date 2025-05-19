# Minimal stub for pyserial
class Serial:
    def __init__(self, port, baudrate, timeout):
        print(f"[MOCK SERIAL] opening {port}@{baudrate}")
        self.in_waiting = False

    def readline(self):
        return b""

    def close(self):
        print("[MOCK SERIAL] close()")

    @property
    def is_open(self):
        return False
