FROM python:3.11-slim

WORKDIR /app

# Copy your requirements
COPY Makefile .
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the src directory and mocks
COPY src/ src/

# Enable mock mode in container
ENV USE_MOCK=1
ENV PYTHONPATH=/app

CMD ["python", "-m", "src.main"]
