# Starts the entire Ascent platform, including all log aggregation services

docker-compose -f docker-compose.cache.yml \
	-f docker-compose.cache.override.yml \
	up --build -d
