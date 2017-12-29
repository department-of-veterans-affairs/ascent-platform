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
  source = "/usr/share/kibana/template/ca.pem.tpl"
  destination = "/usr/share/kibana/config/ca.pem"
  perms = 0644
}

# - Kibana server certs
template {
  source = "/usr/share/kibana/template/server.pem.tpl"
  destination = "/usr/share/kibana/config/server.pem"
  perms = 0644
}
template {
  source = "/usr/share/kibana/template/server.key.tpl"
  destination = "/usr/share/kibana/config/server.key"
  perms = 0600
}

# - Elasticsearch server certs
template {
  source = "/usr/share/kibana/template/es-server.pem.tpl"
  destination = "/usr/share/kibana/config/es-server.pem"
  perms = 0644
}
template {
  source = "/usr/share/kibana/template/es-server.key.tpl"
  destination = "/usr/share/kibana/config/es-server.key"
  perms = 0600
}


template {
  source = "/usr/share/kibana/template/kibana.ssl.yml.tpl"
  destination = "/usr/share/kibana/config/kibana.yml"
  perms = 0600
}

