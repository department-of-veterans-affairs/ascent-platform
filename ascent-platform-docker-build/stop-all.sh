# Stops the entire Ascent platform, including all log aggregation services

docker-compose -f docker-compose.yml \
	-f docker-compose.override.yml \
	-f docker-compose.logging.yml \
	-f docker-compose.logging.override.yml \
	-f docker-compose.cache.yml \
	down -v
