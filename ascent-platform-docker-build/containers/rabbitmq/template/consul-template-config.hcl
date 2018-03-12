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
  source = "/etc/rabbitmq/template/rabbitmq.conf.tpl"
  destination = "/etc/rabbitmq/conf/rabbitmq.conf"
  perms = 0644
}

