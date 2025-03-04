import RPi.GPIO as GPIO
from hx711 import HX711

GPIO.setmode(GPIO.BCM)

hx = HX711(dout_pin = 5, pd_sck_pin=6)

hx.zero()

input('PLACE KNOWN WEIGHT: ')
reading = hx.get_data_mean(readngs=100)
#weight in grams
known_weight = input('Enter the known weight in grams and press Enter: ' )
value = float(known_weight)

ratio = reading/va;ue
hx.set_scale_ratio(ratio)

while True:
    weight = hx.get_weight_mean()
    
    weight_kg=weight/1000
    print (f"Weight in kg: {weight_kg}")
