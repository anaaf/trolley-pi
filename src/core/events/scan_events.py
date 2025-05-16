from dataclasses import dataclass
from typing import Optional

@dataclass
class ScanEvent:
    """Event containing barcode scan and weight data."""
    barcode: str
    timestamp: float
    weight: Optional[float] = None 