FROM redis:4.0.2-alpine
 
ENV REDIS_PORT 6379
ENV SENTINEL_DOWN_AFTER 1000
ENV SENTINEL_FAILOVER 1000
 
RUN mkdir -p /redis
 
WORKDIR /redis
 
COPY redis.conf .
COPY redis-entrypoint.sh /usr/local/bin/
 
RUN chown redis:redis /redis/* && \
    chmod +x /usr/local/bin/redis-entrypoint.sh
 
ENTRYPOINT ["redis-entrypoint.sh"]