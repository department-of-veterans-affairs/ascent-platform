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
    format = "mysql_{{ key }}"
    no_prefix = true
    path = "secret/sonar/database"
}

secret {
    format = "mysql_root_{{ key }}"
    no_prefix = true
    path = "secret/sonar/database/root"
}
