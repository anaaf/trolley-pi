#!/bin/bash

# This script reloads systemd, enables the trolley service, and starts it.
set -e

sudo systemctl daemon-reload
sudo systemctl enable trolley.service
sudo systemctl start trolley.service

echo "trolley.service reloaded, enabled, and started successfully."
