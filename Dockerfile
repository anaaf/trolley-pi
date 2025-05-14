FROM python:3.11-slim

WORKDIR /app

# Copy your requirements
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the whole project
COPY . .

# Enable mock mode in container
ENV USE_MOCK=1

CMD ["python", "all.py"]
