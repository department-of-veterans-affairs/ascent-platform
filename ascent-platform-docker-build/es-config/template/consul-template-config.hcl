# See https://github.com/hashicorp/consul-template for config documentation

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

template {
  source = "/docker/curator/config.tpl.yml"
  destination = "/docker/curator/config.yml"
  perms = 0644
}

