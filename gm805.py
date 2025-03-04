import serial
import time

def read_barcode():
    try:
        ser = serial.Serial(
            port='/dev/ttyAMA0',  # Use PL011 UART
            baudrate=9600,  # Default baud rate for GM805
            timeout=1  # Timeout for reading
        )
        
        print("Waiting for barcode...")
        while True:
            if ser.in_waiting > 0:
                    data = ser.readline().decode('utf-8', errors='ignore').strip()
                    print(data)
            time.sleep(0.1)
    
    except serial.SerialException as e:
        print("Serial Error:", e)
    except KeyboardInterrupt:
        print("Program exited.")
    finally:
        if 'ser' in locals() and ser.is_open:
            print("Closing serial port.")
            ser.close()

if __name__ == "__main__":
    read_barcode()
