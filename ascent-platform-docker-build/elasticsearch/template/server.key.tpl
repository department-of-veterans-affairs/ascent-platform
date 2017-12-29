{{ with secret "secret/elasticsearch" }}{{ with .Data.privatekey }}{{ trimSpace . }}{{ end }}{{ end }}
