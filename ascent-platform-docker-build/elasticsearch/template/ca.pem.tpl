{{ with secret "pki/cert/ca" }}{{ with .Data.certificate }}{{ trimSpace . }}{{ end }}{{ end }}
