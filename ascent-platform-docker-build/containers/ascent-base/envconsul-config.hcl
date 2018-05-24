# See https://github.com/hashicorp/envconsul for config documentation

vault {
  renew_token   = true
  unwrap_token = false

  retry {
    attempts = 10
  }

  ssl {
    enabled = true
    verify  = false
  }
}

upcase = false

# Secrets to load into the environment
secret {
    format = "discovery.{{ key }}"
    no_prefix = true
    path = "secret/ascent-discovery"
}
secret {
    format = "config.{{ key }}"
    no_prefix = true
    path = "secret/ascent-config"
}
