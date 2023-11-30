{{- define "serviceType" -}}
{{- if .Values.ingress.enabled }}
{{- "ClusterIP" }}
{{- else }}
{{- .Values.ingress.serviceType }}
{{- end }}
{{- end }}