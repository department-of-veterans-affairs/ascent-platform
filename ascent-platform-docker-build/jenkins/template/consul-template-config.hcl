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
  source = "/usr/share/jenkins/template/create-user.groovy.tpl"
  destination = "/usr/share/jenkins/ref/init.groovy.d/create-user.groovy"
  perms = 0644
}
 
