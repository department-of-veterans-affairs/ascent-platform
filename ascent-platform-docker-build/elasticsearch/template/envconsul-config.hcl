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

# TODO: needs to be generated in vault
# ElasticSerch SSL Secrets
#secret {
#    format = "es_{{ key }}"
#    no_prefix = true
#    path = "secret/elasticsearch"
#}

# ElasticSearch User Secrets
secret {
    format = "es_{{ key }}"
    no_prefix = true
    path = "secret/elasticsearch/admin"   
}


# Kibana User Secrets
secret {
    format = "kibana_{{ key }}"
    no_prefix = true
    path = "secret/elasticsearch/kibana"
}


