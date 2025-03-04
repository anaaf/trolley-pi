import serial
ser = serial.Serial(port='/dev/serial0', baudrate=9600, timeout=1)

try:
        if ser.in_waiting > 0:
            barcode_data = ser.read(ser.in_waiting).decode('utf-8').strip()
            return barcode_data
        else:
            return None
    except Exception as e:
        print(f" Error reading from barcode sensor: {e}")
        return None
    
    print("Waiting for barcode...")
        print (f"Sccanned barcode :{barcode_data}")
        
try:
    while True:
        read_barcode
        main()
        
except KeyboardInterrupt:
        print("\nExiting...")
    
finally:
        ser.close
        

            