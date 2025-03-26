import RPi.GPIO as GPIO
from hx711 import HX711
import serial
import time
import threading
import cv2

# GPIO setup
GPIO.setmode(GPIO.BCM)

# Load cell setup
hx = HX711(dout_pin=6, pd_sck_pin=5)
hx.zero()
hx.set_scale_ratio(98.8)

# Function to read barcode scanner
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

# Function to display camera feed
def camera_feed():
    cap = cv2.VideoCapture(0)  # Open default camera
    if not cap.isOpened():
        print("Error: Could not open camera")
        return

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame")
            break
        
        cv2.imshow("Camera Feed", frame)  # Display the camera feed

        # Exit when 'q' is pressed
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

# Start barcode scanner thread
barcode_thread = threading.Thread(target=read_barcode, daemon=True)
barcode_thread.start()

# Start camera feed thread
camera_thread = threading.Thread(target=camera_feed, daemon=True)
camera_thread.start()

try:
    while True:
        weight_kg = hx.get_weight_mean() / 1000
        print(f"Weight in kg: {weight_kg}")
        time.sleep(1)

except KeyboardInterrupt:
    GPIO.cleanup()
    print("\nProgram Exited")
