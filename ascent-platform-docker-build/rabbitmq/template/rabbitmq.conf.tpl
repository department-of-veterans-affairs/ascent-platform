default_pass = {{ with secret "secret/application" }}{{ index .Data "spring.rabbitmq.password" }}{{ end }}
default_user = {{ with secret "secret/application" }}{{ index .Data "spring.rabbitmq.username" }}{{ end }}
loopback_users = none
