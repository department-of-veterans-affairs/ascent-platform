FROM redis:4.0.2-alpine
 
ENV SENTINEL_QUORUM 2
ENV SENTINEL_DOWN_AFTER 1000
ENV SENTINEL_FAILOVER 1000
ENV REDIS-SENTINEL_PASSWORD changeme
ENV REDIS_START_COMMAND "redis-server /redis/sentinel.conf --sentinel"

RUN apk add --update openssl

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN wget -q -O envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz
 
RUN mkdir -p /redis
 
WORKDIR /redis
 
COPY sentinel.conf .
COPY redis-sentinel-entrypoint.sh /usr/local/bin/
COPY configure-sentinel-conf.sh configure-sentinel-conf.sh

# Add our envconsul config
COPY template/ template/
 
RUN chown redis:redis /redis/* && \
    chmod +x /usr/local/bin/redis-sentinel-entrypoint.sh; \
    chmod -R 755 template; \
    chmod 777 configure-sentinel-conf.sh
 
EXPOSE 26379

ENTRYPOINT ["/usr/local/bin/redis-sentinel-entrypoint.sh"]
