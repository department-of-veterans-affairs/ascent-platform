vault {
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
  source      = "/tmp/app.crt.tpl"
  destination = "/tmp/app.crt"
}

template {
  source      = "/tmp/app.key.tpl"
  destination = "/tmp/app.key"
}