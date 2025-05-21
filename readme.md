# Trolley-Pi

A Raspberry Pi-based smart shopping trolley system that combines barcode scanning and weight measurement capabilities. The system automatically detects when items are added to the trolley and sends the data to a central server.

## Features

- Barcode scanning via serial port or keyboard input
- Weight measurement using HX711 load cell
- Automatic API integration for sending scan data
- Docker support for easy deployment
- Mock mode for development and testing

## Prerequisites

- Raspberry Pi (tested with Raspberry Pi OS)
- HX711 load cell
- Barcode scanner (serial or keyboard-wedge type)
- Python 3.x
- Docker and Docker Compose (optional)

## Hardware Setup

1. Connect the HX711 load cell to the Raspberry Pi:
   - DOUT pin to GPIO 6
   - PD_SCK pin to GPIO 5
   - VCC to 3.3V
   - GND to GND

2. Connect the barcode scanner:
   - For serial scanner: Connect to `/dev/ttyAMA0`
   - For keyboard-wedge scanner: Connect via USB

## Installation

### Using Docker (Recommended)

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd trolley-pi
   ```

2. Build and run using Docker Compose:
   ```bash
   # First time setup
   docker-compose build
   
   # Run the application
   make run-docker
   ```

### Manual Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd trolley-pi
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Run the application:
   ```bash
   python3 src/main.py
   ```

### Testing with Dummy Barcodes

You can test the system by passing dummy barcodes through the console input. Here are two ways to do it:

1. Using Docker:
   ```bash
   echo "1234567890" | make run-docker
   ```

2. Using direct Python execution:
   ```bash
   echo "1234567890" | python3 src/main.py
   ```

The system will process the barcode and attempt to send it to the API endpoint. Make sure you have configured the correct API_URL and other environment variables before testing.

### Simulation Mode

The system supports a simulation mode that allows you to test both barcode scanning and weight measurements without physical hardware. To enable simulation mode:

1) Run the application:
   ```bash
   make run-with-mock
   ```

Note: In simulation mode, all hardware dependencies are mocked, making it perfect for development and testing without physical hardware.

### Using Provider Scripts for Simulation

The system includes provider scripts that allow you to simulate barcode scanning and weight measurements through named pipes. These scripts are located in `src/libs/mocks/`.

#### Simulating Barcode Scans

1. Run the barcode provider script:
   ```bash
   python3 src/libs/mocks/barcode_provider.py
   ```

2. Enter barcodes when prompted. The script will:
   - Accept barcode input from the console
   - Send the barcode through a named pipe (`/tmp/barcode_pipe`)
   - Continue accepting input until you press 'q' or Ctrl+C

Example:
```bash
Enter barcode (or 'q' to quit): 1234567890
Enter barcode (or 'q' to quit): 9876543210
Enter barcode (or 'q' to quit): q
```

#### Simulating Weight Measurements

1. Run the weight provider script:
   ```bash
   python3 src/libs/mocks/weight_provider.py
   ```

2. Enter weight values when prompted. The script will:
   - Accept weight input in kilograms
   - Send the weight through a named pipe (`/tmp/weight_pipe`)
   - Continue accepting input until you press 'q' or Ctrl+C

Example:
```bash
Enter weight in kg (or 'q' to quit): 1.5
Enter weight in kg (or 'q' to quit): 2.3
Enter weight in kg (or 'q' to quit): q
```

Note: Make sure the main application is running in mock mode (`USE_MOCK=1`) when using these provider scripts.

## Configuration

The following environment variables can be configured:

- `USE_MOCK`: Set to "1" to enable mock mode (useful for development)
- `API_URL`: The endpoint for sending scan data
- `CART_UUID`: Unique identifier for the shopping cart
- `TROLLEY_UUID`: Unique identifier for the trolley
- `SERIAL_PORT`: Serial port for barcode scanner (default: "/dev/ttyAMA0")
- `BAUDRATE`: Serial port baud rate (default: 9600)
- `SCALE_RATIO`: Calibration ratio for the load cell (default: 99.6)

## Development

For development and testing, you can use mock mode by setting `USE_MOCK=1`. This mode:
- Simulates GPIO operations
- Provides mock HX711 readings
- Simulates serial port communication

## Project Structure

- `src/main.py`: Main application file
- `src/mocks/`: Mock implementations for development
- `Dockerfile`: Container configuration
- `docker-compose.yml`: Docker Compose configuration
- `requirements.txt`: Python dependencies

## License

[Add your license information here]

## Contributing

[Add contribution guidelines here]
