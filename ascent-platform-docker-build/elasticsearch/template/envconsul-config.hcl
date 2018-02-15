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

# ElasticSerch SSL Secrets
secret {
    format = "es_{{ key }}"
    no_prefix = true
    path = "secret/elasticsearch"
}

secret {
    format = "aws_{{ key }}"
    no_prefix = true
    path = "secret/aws"
}


