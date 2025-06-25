
#  EPAM "AWS Developer" Course project. Anton Khitrov, 2025

### Stack: Scala 2.13, cats, http4s, Slick, Python, AWS SDK

Deployment: CI/CD CodePipeline
`buildspec.yml`

 The app contains following endpoints groups:
```
  /        - greeting by default
  /az      - show region and availability zone
  /files   - file upload/download
  /info    - file metadata information
  /queues  - SQS operations management
  /lambda  - lambda invocation
```
 Build with sbt:
 `sbt clean assembly`