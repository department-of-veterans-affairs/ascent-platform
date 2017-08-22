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
  source = "/usr/share/logstash/template/ca.pem.tpl"
  destination = "/usr/share/logstash/ca.pem"
  perms = 0644
}

template {
  source = "/usr/share/logstash/template/server.pem.tpl"
  destination = "/usr/share/logstash/server.pem"
  perms = 0644
}

template {
  source = "/usr/share/logstash/template/server.key.tpl"
  destination = "/usr/share/logstash/server.key"
  perms = 0600
}