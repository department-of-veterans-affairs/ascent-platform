{{ with secret "pki/issue/vets-api-dot-gov" "common_name=elastic.internal.vets-api.gov" }}{{ .Data.issuing_ca }}{{ end }}
