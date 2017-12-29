{{ with secret "secret/elasticsearch" }}{{ .Data.privatekey }}{{ end }}
