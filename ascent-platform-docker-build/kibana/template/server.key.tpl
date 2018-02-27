{{ with secret "secret/kibana" }}{{ .Data.privatekey }}{{ end }}
