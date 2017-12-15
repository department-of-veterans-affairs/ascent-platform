vault {
  renew_token   = false
  unwrap_token = false

  retry {
    attempts = 10
  }

  ssl {
    enabled = true
    verify  = false
  }
}

upcase = true

# Sentinell Password Secret
secret {
    format = "REDIS_SENTINEL_{{ key }}"
    no_prefix = true
    path = "secret/redis-sentinel"
}
