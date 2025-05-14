echo "1234567890" | docker exec -i $(docker-compose ps -q scanner) tee /dev/stdin
