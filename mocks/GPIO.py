# Simple stub for RPi.GPIO
BCM = “BCM”
OUT = “OUT”
IN = “IN”

def setmode(mode):
    print(f"[MOCK GPIO] setmode({mode})")

def cleanup():
    print("[MOCK GPIO] cleanup()")
