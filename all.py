import RPi.GPIO as GPIO
from hx711 import HX711
import serial
import time
import threading
from decimal import Decimal, ROUND_HALF_UP

GPIO.setmode(GPIO.BCM)

hx = HX711(dout_pin=6, pd_sck_pin=5)
hx.zero()
hx.set_scale_ratio(99.6)

def read_barcode():
    try:
        ser = serial.Serial(port='/dev/ttyAMA0', baudrate=9600, timeout=1)

        while True:
            if ser.in_waiting > 0:
                barcode = ser.readline().decode('utf-8', errors='ignore').strip()
                print(f"Scanned Barcode: {barcode}")
            time.sleep(0.1)

    except serial.SerialException as e:
        print("Serial Error:", e)
    finally:
        if 'ser' in locals() and ser.is_open:
            ser.close()

barcode_thread = threading.Thread(target=read_barcode, daemon=True)
barcode_thread.start()

try:
    while True:
        weight_kg = hx.get_weight_mean() / 1000
        weight_kg_r = Decimal(str(weight_kg))
        rounded = weight_kg_r.quantize(Decimal("0.001"), rounding=ROUND_HALF_UP)
        print(f"Weight in kg updated: {rounded}")
        time.sleep(1)

except KeyboardInterrupt:
    GPIO.cleanup()
    print("\nProgram Exited")
