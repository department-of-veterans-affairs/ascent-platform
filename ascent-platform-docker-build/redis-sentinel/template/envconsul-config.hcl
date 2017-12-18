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
    format = "spring_redis_{{ key }}"
    no_prefix = true
    path = "secret/application"
}
