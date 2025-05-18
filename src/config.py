import os
from dataclasses import dataclass
from decimal import Decimal

@dataclass
class Config:
    # API Configuration
    API_URL: str = "http://56.228.26.246:8080/v1/cart/barcodeScan"
    CART_UUID: str = "da4c6590-b0bf-4ee3-b436-fa868188f520"
    TROLLEY_UUID: str = "0771c388-a12f-4499-afef-cf3401887722"
    
    # Serial Configuration
    SERIAL_PORT: str = "/dev/ttyAMA0"
    BAUDRATE: int = 9600
    
    # Scale Configuration
    SCALE_RATIO: float = 99.6
    WEIGHT_THRESHOLD_KG: Decimal = Decimal('0.010')  # 10 grams
    
    # Timing Configuration
    READ_INTERVAL: float = 0.1
    WEIGHT_INTERVAL: float = 1.0
    
    # Feature Flags
    USE_MOCK: bool = os.getenv("USE_MOCK") == "1"
    USE_CONSOLE_INPUT: bool = True  # listen on stdin for keyboard-wedge scanners
    USE_MOCK_CLIENT: bool = os.getenv("USE_MOCK_CLIENT") == "1"  # use mock client regardless of USE_MOCK

config = Config() 