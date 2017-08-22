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
  source = "/usr/share/filebeat/template/ca.pem.tpl"
  destination = "/usr/share/filebeat/ca.pem"
  perms = 0644
}

template {
  source = "/usr/share/filebeat/template/client.pem.tpl"
  destination = "/usr/share/filebeat/client.pem"
  perms = 0644
}

template {
  source = "/usr/share/filebeat/template/client.key.tpl"
  destination = "/usr/share/filebeat/client.key"
  perms = 0600
}