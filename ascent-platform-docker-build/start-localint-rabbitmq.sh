# Starts the entire Ascent platform, including all log aggregation services

docker-compose -f docker-compose.localint.rabbitmq.yml \
	up --build -d
