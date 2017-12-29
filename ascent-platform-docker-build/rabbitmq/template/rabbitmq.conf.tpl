default_pass = {{ with secret "secret/application" }}{{ index .Data "spring.rabbitmq.password" }}{{ end }}
loopback_users = none
