from dataclasses import dataclass

@dataclass
class BarcodeScanEvent:
    """Event created when a barcode is scanned."""
    barcode: str
    timestamp: float 