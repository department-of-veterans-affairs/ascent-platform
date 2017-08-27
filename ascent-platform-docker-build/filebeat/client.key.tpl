{{ with secret "secret/filebeat" }}
{{ .Data.privatekey }}{{ end }}