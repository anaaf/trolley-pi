import os
import select
import time
import logging

class SerialException(Exception):
    pass

class Serial:
    """
    Mock Serial class that reads from a named pipe (FIFO) to simulate a barcode scanner.
    Falls back to BARCODE_PIPE or /tmp/barcode_pipe if the provided port isn't available.
    """
    DEFAULT_PIPE = os.getenv('BARCODE_PIPE', '/tmp/barcode_pipe')

    def __init__(self, port: str, baudrate: int = 9600, timeout: float = 1.0):
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.buffer = b''
        self.fd = None

        # Try primary port
        if not self._open_fifo(self.port):
            # Fallback to default pipe
            if self.port != self.DEFAULT_PIPE:
                logging.warning(f"Mock serial: Could not open '{self.port}', falling back to '{self.DEFAULT_PIPE}'")
                # Ensure default pipe exists
                try:
                    if not os.path.exists(self.DEFAULT_PIPE):
                        os.mkfifo(self.DEFAULT_PIPE)
                except OSError as e:
                    raise SerialException(f"Could not create fallback pipe {self.DEFAULT_PIPE}: {e}")
                # Try opening fallback
                if not self._open_fifo(self.DEFAULT_PIPE):
                    raise SerialException(f"Could not open fallback pipe {self.DEFAULT_PIPE}")
                # Update port to fallback
                self.port = self.DEFAULT_PIPE
            else:
                raise SerialException(f"Could not open serial port {self.port}")
        logging.info(f"Mock serial FIFO opened on {self.port}")

    def _open_fifo(self, path: str) -> bool:
        """Attempt to open a FIFO path in non-blocking read mode."""
        try:
            self.fd = os.open(path, os.O_RDONLY | os.O_NONBLOCK)
            return True
        except OSError:
            return False

    @property
    def in_waiting(self) -> bool:
        """Return True if there is data waiting to be read."""
        if self.fd is None:
            return False
        r, _, _ = select.select([self.fd], [], [], 0)
        return bool(r)

    def readline(self) -> bytes:
        """
        Read a line (up to and including a newline) from the FIFO, with timeout.
        Returns an empty bytes object on timeout.
        """
        if self.fd is None:
            return b''
        end_time = time.time() + (self.timeout or 0)
        while True:
            timeout = max(0, end_time - time.time())
            r, _, _ = select.select([self.fd], [], [], timeout)

            if r:
                chunk = os.read(self.fd, 1024)
                if not chunk:
                    time.sleep(0.01)
                    continue
                self.buffer += chunk
                if b'\n' in self.buffer:
                    line, sep, rest = self.buffer.partition(b'\n')
                    self.buffer = rest
                    return line + sep
            else:
                return b''

    def close(self) -> None:
        """Close the FIFO file descriptor."""
        if self.fd is not None:
            try:
                os.close(self.fd)
            except OSError:
                pass

# Ensure aliasing so "serial.Serial" works
Serial.Serial = Serial