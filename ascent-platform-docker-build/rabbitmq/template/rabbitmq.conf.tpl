[{ rabbit, [
   { default_user, <<"{{ with secret "secret/application" }}{{ index .Data "spring.rabbitmq.username" }}{{ end }}">> },
   { default_pass, <<"{{ with secret "secret/application" }}{{ index .Data "spring.rabbitmq.password" }}{{ end }}">> },
   { loopback_users, [] }
 ]}
].
