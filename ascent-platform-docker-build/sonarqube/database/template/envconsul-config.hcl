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

# Sonar Secret
secret {
    format = "postgres_{{ key }}"
    no_prefix = true
    path = "secret/sonar/database"
}

