.PHONY: all update
all:
	git pull
	python3 all.py

run-docker:
	docker-compose run --rm -T scanner python -m src.main

# run directly on PI
run:
	export USE_MOCK=0 && PYTHONPATH=. python3 src/main.py

# run directly on laptop using Mocks
run-with-mock:
	export USE_MOCK=1 && PYTHONPATH=. python3 src/main.py

run-barcode-simulator:
	python3 src/libs/mocks/barcode_provider.py

run-weight-simulator:
	python3 src/libs/mocks/weight_provider.py
