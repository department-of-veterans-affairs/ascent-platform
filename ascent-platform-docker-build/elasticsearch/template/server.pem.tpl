{{ with secret "pki/issue/vets-api-dot-gov" "common_name=elastic.internal.vets-api.gov" }}{{ .Data.certificate }}{{ end }}
