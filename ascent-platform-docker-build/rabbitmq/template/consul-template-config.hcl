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
  destination = "/usr/lib/rabbitmq/etc/rabbitmq/rabbitmq.config"
  perms = 0644
}

