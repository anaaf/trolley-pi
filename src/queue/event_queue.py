import logging
from queue import Queue, Empty
from typing import Generic, TypeVar, Optional
from dataclasses import dataclass
import time

T = TypeVar('T')

@dataclass
class QueueStats:
    total_events: int = 0
    processed_events: int = 0
    failed_events: int = 0
    last_event_time: Optional[float] = None

class EventQueue(Generic[T]):
    def __init__(self, name: str = "default"):
        self.queue = Queue()
        self.name = name
        self.stats = QueueStats()
        self._stop_event = False

    def put(self, event: T) -> None:
        """Put an event into the queue."""
        logging.debug(f"[{self.name}] Attempting to put event: {event}")
        self.queue.put(event)
        self.stats.total_events += 1
        self.stats.last_event_time = time.time()
        logging.debug(f"[{self.name}] Event queued successfully. Queue size: {self.queue.qsize()}")

    def get(self, timeout: float = 1.0) -> Optional[T]:
        """Get an event from the queue with timeout."""
        try:
            logging.debug(f"[{self.name}] Attempting to get event with timeout {timeout}s")
            event = self.queue.get(timeout=timeout)
            logging.debug(f"[{self.name}] Successfully got event: {event}")
            return event
        except Empty:
            logging.debug(f"[{self.name}] No event available after {timeout}s timeout")
            return None

    def mark_processed(self) -> None:
        """Mark the last retrieved event as processed."""
        self.stats.processed_events += 1
        self.queue.task_done()
        logging.debug(f"[{self.name}] Event marked as processed. Stats: {self.stats}")

    def mark_failed(self) -> None:
        """Mark the last retrieved event as failed."""
        self.stats.failed_events += 1
        self.queue.task_done()
        logging.debug(f"[{self.name}] Event marked as failed. Stats: {self.stats}")

    def get_stats(self) -> QueueStats:
        """Get current queue statistics."""
        return self.stats

    def is_empty(self) -> bool:
        """Check if queue is empty."""
        return self.queue.empty()

    def size(self) -> int:
        """Get current queue size."""
        return self.queue.qsize()

    def stop(self) -> None:
        """Stop the queue processing."""
        self._stop_event = True

    def is_stopped(self) -> bool:
        """Check if queue is stopped."""
        return self._stop_event 