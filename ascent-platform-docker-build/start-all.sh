# Starts the entire Ascent platform, including all log aggregation services

source ./start-vault.sh
docker-compose -f docker-compose.yml \
	-f docker-compose.override.yml \
	-f docker-compose.logging.yml \
	-f docker-compose.logging.override.yml \
	-f docker-compose.cache.yml \
	-f docker-compose.cache.override.yml \
	up --build -d
