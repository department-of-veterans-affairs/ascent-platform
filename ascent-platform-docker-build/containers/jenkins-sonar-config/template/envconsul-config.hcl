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
sanitize = true

# Sonar Secret
secret {
    format = "sonar_{{ key }}"
    no_prefix = true
    path = "secret/sonar/admin"
}

# Jenkins Secret
secret {
    format = "jenkins_{{ key }}"
    no_prefix = true
    path = "secret/jenkins"
}

# Nexus Secret
secret {
    format = "nexus_{{ key }}"
    no_prefix = true
    path = "secret/nexus"
}

# Github token Secret
secret {
    format = "github_{{ key }}"
    no_prefix = true
    path = "secret/github"
}





