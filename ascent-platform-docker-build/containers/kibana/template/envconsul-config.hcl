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

# Kibana SSL Secrets
#secret {
#    format = "kb_{{ key }}"
#    no_prefix = true
#    path = "secret/kibana"
#}


# ElasticSearch User Secrets
secret {
    format = "es_{{ key }}"
    no_prefix = true
    path = "secret/elasticsearch/admin"   
}


# Kibana User Secrets
secret {
    format = "elasticsearch_{{ key }}"
    no_prefix = true
    path = "secret/elasticsearch/kibana"
}
