from flask import Flask, jsonify, request
from typing import Dict, List, Optional
from decimal import Decimal
from dataclasses import dataclass, asdict
from datetime import datetime
import json
import os

app = Flask(__name__)

# Define data directory
DATA_DIR = os.path.join(os.path.dirname(__file__), 'data')
os.makedirs(DATA_DIR, exist_ok=True)

# File paths
STORE_ITEMS_FILE = os.path.join(DATA_DIR, 'store_items.json')
CART_FILE = os.path.join(DATA_DIR, 'cart.json')
SCAN_HISTORY_FILE = os.path.join(DATA_DIR, 'scan_history.json')

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

def load_store_items() -> Dict[str, StoreItem]:
    """Load store items from file or create default if not exists."""
    if os.path.exists(STORE_ITEMS_FILE):
        with open(STORE_ITEMS_FILE, 'r') as f:
            data = json.load(f)
            return {barcode: StoreItem(**item) for barcode, item in data.items()}
    else:
        # Default store items
        items = {
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
        # Save default items
        save_store_items(items)
        return items

def save_store_items(items: Dict[str, StoreItem]):
    """Save store items to file."""
    with open(STORE_ITEMS_FILE, 'w') as f:
        json.dump({barcode: asdict(item) for barcode, item in items.items()}, f, indent=2)

def load_cart() -> List[CartItem]:
    """Load cart items from file."""
    if os.path.exists(CART_FILE):
        with open(CART_FILE, 'r') as f:
            data = json.load(f)
            return [CartItem(**item) for item in data]
    return []

def save_cart(items: List[CartItem]):
    """Save cart items to file."""
    with open(CART_FILE, 'w') as f:
        json.dump([asdict(item) for item in items], f, indent=2)

def load_scan_history() -> List[Dict]:
    """Load scan history from file."""
    if os.path.exists(SCAN_HISTORY_FILE):
        with open(SCAN_HISTORY_FILE, 'r') as f:
            return json.load(f)
    return []

def save_scan_history(history: List[Dict]):
    """Save scan history to file."""
    with open(SCAN_HISTORY_FILE, 'w') as f:
        json.dump(history, f, indent=2)

# Initialize data
STORE_ITEMS = load_store_items()
cart_items = load_cart()
scan_history = load_scan_history()

@app.route('/api/register-scan', methods=['POST'])
def register_scan():
    """Register a barcode scan."""
    data = request.json
    cart_uuid = data.get('cartUuid')
    trolley_uuid = data.get('trolleyUuid')
    barcode = data.get('barcodeNumber')
    timestamp = data.get('timestamp')

    if not all([cart_uuid, trolley_uuid, barcode, timestamp]):
        return jsonify({'error': 'Missing required fields'}), 400

    # Store scan history
    scan_data = {
        "cartUuid": cart_uuid,
        "trolleyUuid": trolley_uuid,
        "barcodeNumber": barcode,
        "timestamp": timestamp
    }
    scan_history.append(scan_data)
    save_scan_history(scan_history)

    # Check if item exists in store
    if barcode in STORE_ITEMS:
        item = STORE_ITEMS[barcode]
        return jsonify({
            'success': True,
            'message': f'Found item in store: {item.name}',
            'item': asdict(item)
        })
    else:
        return jsonify({
            'success': False,
            'message': f'Item not found in store database: {barcode}'
        }), 404

@app.route('/api/add-item', methods=['POST'])
def add_item():
    """Add an item to the cart."""
    data = request.json
    cart_uuid = data.get('cartUuid')
    trolley_uuid = data.get('trolleyUuid')
    barcode = data.get('barcode')
    weight = data.get('weight')
    timestamp = data.get('timestamp')

    if not all([cart_uuid, trolley_uuid, barcode, timestamp]):
        return jsonify({'error': 'Missing required fields'}), 400

    # Check if item exists in store
    if barcode not in STORE_ITEMS:
        return jsonify({
            'success': False,
            'message': f'Cannot add item: Barcode {barcode} not found in store'
        }), 404

    store_item = STORE_ITEMS[barcode]
    current_weight = float(weight) if weight is not None else store_item.weight

    # Create cart item
    cart_item = CartItem(
        barcode=barcode,
        name=store_item.name,
        price=store_item.price,
        weight=current_weight,
        timestamp=timestamp
    )

    # Add to cart
    cart_items.append(cart_item)
    save_cart(cart_items)

    return jsonify({
        'success': True,
        'message': f'Added {store_item.name} to cart',
        'cart': [asdict(item) for item in cart_items]
    })

@app.route('/api/remove-item', methods=['POST'])
def remove_item():
    """Remove an item from the cart."""
    data = request.json
    cart_uuid = data.get('cartUuid')
    trolley_uuid = data.get('trolleyUuid')
    barcode = data.get('barcode')
    timestamp = data.get('timestamp')

    if not all([cart_uuid, trolley_uuid, barcode, timestamp]):
        return jsonify({'error': 'Missing required fields'}), 400

    # Check if item exists in store
    if barcode not in STORE_ITEMS:
        return jsonify({
            'success': False,
            'message': f'Cannot remove item: Barcode {barcode} not found in store'
        }), 404

    # Find and remove the item from cart
    for i, cart_item in enumerate(cart_items):
        if cart_item.barcode == barcode:
            removed_item = cart_items.pop(i)
            save_cart(cart_items)
            return jsonify({
                'success': True,
                'message': f'Removed {removed_item.name} from cart',
                'cart': [asdict(item) for item in cart_items]
            })

    return jsonify({
        'success': False,
        'message': f'Item {STORE_ITEMS[barcode].name} not found in cart for removal'
    }), 404

@app.route('/api/cart', methods=['GET'])
def get_cart():
    """Get the current cart status."""
    total_price = sum(item.price for item in cart_items)
    total_weight = sum(item.weight for item in cart_items)

    return jsonify({
        'items': [asdict(item) for item in cart_items],
        'total_items': len(cart_items),
        'total_price': total_price,
        'total_weight': total_weight
    })

@app.route('/api/scan-history', methods=['GET'])
def get_scan_history():
    """Get the scan history."""
    return jsonify(scan_history)

@app.route('/api/store-items', methods=['GET'])
def get_store_items():
    """Get all store items."""
    return jsonify({barcode: asdict(item) for barcode, item in STORE_ITEMS.items()})

@app.route('/api/store-items', methods=['POST'])
def add_store_item():
    """Add a new store item."""
    data = request.json
    barcode = data.get('barcode')
    name = data.get('name')
    price = data.get('price')
    weight = data.get('weight')

    if not all([barcode, name, price, weight]):
        return jsonify({'error': 'Missing required fields'}), 400

    item = StoreItem(
        barcode=barcode,
        name=name,
        price=float(price),
        weight=float(weight)
    )
    STORE_ITEMS[barcode] = item
    save_store_items(STORE_ITEMS)

    return jsonify({
        'success': True,
        'message': f'Added {name} to store',
        'item': asdict(item)
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True) 