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

# Filebeat SSL Secrets
secret {
    format = "ls_{{ key }}"
    no_prefix = true
    path = "secret/logstash"
}