import logging
from typing import Optional, Dict, List
from decimal import Decimal
from dataclasses import dataclass
from datetime import datetime

@dataclass
class StoreItem:
    barcode: str
    name: str
    price: float
    weight: float  # in kg

@dataclass
class CartItem:
    barcode: str
    name: str
    price: float
    weight: float
    timestamp: float

class MockAPIClient:
    def __init__(self, api_url: str):
        """Initialize mock API client with simulated database."""
        self.api_url = api_url
        self.should_fail = False
        
        # Initialize store items database
        self.store_items: Dict[str, StoreItem] = {
            "8964003250232": StoreItem(
                barcode="8964003250232",
                name="Normega 100",
                price=29.99,
                weight=0.5
            ),
            "8964003250263": StoreItem(
                barcode="8964003250263",
                name="Vitamax",
                price=19.99,
                weight=0.3
            ),
            "8964003250287": StoreItem(
                barcode="8964003250287",
                name="Protein Bar",
                price=4.99,
                weight=0.1
            ),
            "8964003250294": StoreItem(
                barcode="8964003250294",
                name="Energy Drink",
                price=2.99,
                weight=0.25
            )
        }
        
        # Initialize cart
        self.cart_items: List[CartItem] = []
        self.scan_history: List[Dict] = []

    def register_scan(self, cart_uuid: str, trolley_uuid: str, barcode: str, timestamp: float) -> bool:
        """Mock implementation of register_scan."""
        if self.should_fail:
            logging.error("Mock API client configured to fail")
            return False
            
        # Store scan history
        scan_data = {
            "cartUuid": cart_uuid,
            "trolleyUuid": trolley_uuid,
            "barcodeNumber": barcode,
            "timestamp": timestamp
        }
        self.scan_history.append(scan_data)
        
        # Check if item exists in store
        if barcode in self.store_items:
            item = self.store_items[barcode]
            logging.info(f"Found item in store: {item.name} (Barcode: {barcode})")
            return True
        else:
            logging.warning(f"Item not found in store database: {barcode}")
            return False

    def add_item(self, cart_uuid: str, trolley_uuid: str, barcode: str, weight: Optional[Decimal], timestamp: float) -> bool:
        """Mock implementation of add_item."""
        if self.should_fail:
            logging.error("Mock API client configured to fail")
            return False
            
        # Check if item exists in store
        if barcode not in self.store_items:
            logging.error(f"Cannot add item: Barcode {barcode} not found in store")
            return False
            
        store_item = self.store_items[barcode]
        
        # Create cart item
        cart_item = CartItem(
            barcode=barcode,
            name=store_item.name,
            price=store_item.price,
            weight=float(weight) if weight is not None else store_item.weight,
            timestamp=timestamp
        )
        
        # Add to cart
        self.cart_items.append(cart_item)
        
        # Print cart status
        self._print_cart_status()
        
        return True

    def _print_cart_status(self) -> None:
        """Print current cart status."""
        if not self.cart_items:
            logging.info("Cart is empty")
            return
            
        logging.info("\n=== Current Cart Status ===")
        total_price = 0.0
        total_weight = 0.0
        
        for item in self.cart_items:
            logging.info(f"Item: {item.name}")
            logging.info(f"  Barcode: {item.barcode}")
            logging.info(f"  Price: ${item.price:.2f}")
            logging.info(f"  Weight: {item.weight:.3f} kg")
            logging.info(f"  Added: {datetime.fromtimestamp(item.timestamp).strftime('%Y-%m-%d %H:%M:%S')}")
            logging.info("---")
            
            total_price += item.price
            total_weight += item.weight
        
        logging.info(f"Total Items: {len(self.cart_items)}")
        logging.info(f"Total Price: ${total_price:.2f}")
        logging.info(f"Total Weight: {total_weight:.3f} kg")
        logging.info("========================\n") 