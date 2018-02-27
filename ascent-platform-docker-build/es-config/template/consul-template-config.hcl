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
  source = "/usr/share/elasticsearch/template/ca.pem.tpl"
  destination = "/usr/share/elasticsearch/config/ca.pem"
  perms = 0644
}

