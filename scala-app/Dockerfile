FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# Update packages and install Amazon Corretto 17 (Java 17)
RUN yum update -y && \
    yum install -y java-17-amazon-corretto && \
    yum clean all

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64
ENV PATH="$JAVA_HOME/bin:$PATH"

# Set working directory
WORKDIR /app

# Copy fat jar
COPY ../target/scala-2.13/aws-scala-app-0.1.0.jar app.jar

# Expose port if needed
EXPOSE 8080

CMD java  -DAPP_ENV=cloud -DRDS_ENDPOINT=$RDS_URL \
         				  -DDB_PASSWORD=$DB_PASSWORD \
         				  -DSQS_URL=$SQS_URL \
         				  -DSNS_ARN=$SNS_ARN \
         				  -DRDS_DB=metaws    \
         				  -jar app.jar