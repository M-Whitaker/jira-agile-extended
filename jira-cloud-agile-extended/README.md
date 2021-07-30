# jira-cloud-agile-extended

## Build
``` shell
Add ngrok url to forge/manifest.yml
mvn spring-boot:run -Dspring-boot.run.arguments="--addon.base-url=https://{id-here}.ngrok.io"
forge deploy -e development
forge install
```
