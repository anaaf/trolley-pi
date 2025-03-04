from picamera import PiCamera
from time import sleep
import RPi.GPIO as GPIO
from hx711 import HX711
import serial

GPIO.setmode(GPIO.BCM)

hx = HX711(dout_pin = 5, pd_sck_pin=6)

hx.zero()

input('PLACE KNOWN WEIGHT AND ENTER: ')
reading = hx.get_data_mean(readings=100)
#weight in grams
#known_weight = input('Enter the known weight in grams and press Enter: ' )
#value = float(known_weight)

hx.set_scale_ratio(98.8)

while True:
    weight = hx.get_weight_mean()
    
    weight_kg=weight/1000
    print (f"Weight in kg: {weight_kg}")


camera = PiCamera()
camera.start_preview ()
sleep(5)
camera.capture('/home/pi/Desktop/image1.jpg')
camera.sop_preview
