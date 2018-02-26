# See https://github.com/hashicorp/envconsul for config documentation

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
sanitize = true

# Sonar Secret
secret {
    format = "pg_{{ key }}"
    no_prefix = true
    path = "secret/sonar/database"
}

secret {
    format = "pg_{{ key }}"
    no_prefix = true
    path = "secret/sonar/database/backend"
}
