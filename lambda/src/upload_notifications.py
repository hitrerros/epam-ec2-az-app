import boto3
import json
import os

SNS_TOPIC_ARN = os.environ['SNS_TOPIC_ARN']
DNS_URL       = os.environ['DNS_URL']
sns_client    = boto3.client('sns')

def process_messages(event):

    for record in event['Records']:
        message_body = record['body']

        print(f"Received message body - CI/CD: {message_body}")

        try:
            data = json.loads(message_body)
        except json.JSONDecodeError:
            print(f"Message: {message_body} is not in JSON format")
            continue

        file_name = data.get('fileName')
        extension = data.get('extension')
        size = data.get('size')

        download_url = f"{DNS_URL}/files/{file_name}"
        message_text = f"""
        Dear user!

        File {file_name} has been uploaded.
        Metadata: name {file_name}, extension {extension}, size {size}
        Download link: ${download_url}
        """

        response = sns_client.publish(
            TopicArn=SNS_TOPIC_ARN,
            Message=message_text,
            Subject='File upload notification'
        )


def lambda_handler(event, context):
    process_messages(event)

    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
