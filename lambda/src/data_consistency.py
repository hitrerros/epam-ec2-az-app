import os, boto3, psycopg2,json

rds_client = boto3.client('rds')
s3_client  = boto3.client('s3')


def db_connect() :
    print("trying to connect to db")
    try:
        return psycopg2.connect(
            host=os.environ['RDS_ENDPOINT'],
            dbname=os.environ['RDS_DB'],
            user='postgres',
            password=os.environ['RDS_PASSWORD'],
            port='5432'
        )
    except Exception as e:
        print(f"Connection failed {str(e)} ")
        None

rds_connector = db_connect()


def validate_db_and_s3():
    # S3 fetch
    print("S3 fetching...")
    s3_objects = s3_client.list_objects_v2(Bucket=os.environ['S3_BUCKET'])
    s3_tab = set((obj["Key"],obj["Size"]) for obj in s3_objects.get("Contents", []))

    # Database connection
    db_tab = []
    if (rds_connector!=None):
        print("DB fetching....")
        with rds_connector.cursor() as cursor:
            cursor.execute("SELECT f.\"fileName\", f.size FROM public.file_metadata AS f")
            db_tab = set((row[0],row[1]) for row in cursor.fetchall())
        rds_connector.close()

    difference_1 =  [ x for x in db_tab if x not in s3_tab ]
    difference_2 =  [ x for x in s3_tab if x not in db_tab ]

    return list(difference_1 + difference_2)

def get_invocation_source(event):
    source = ""
    if  (event.get("source") == "aws.events"):
        source = f"Source: Triggered by  {event.get("detail-type")}"
    elif "requestContext" in event and "httpMethod" in event:
        source = "Source: Invoked by API Gateway"
    else:
        source  = "Source: Invoked by EC2 App"

    print(source)

def lambda_handler(event, context):
    get_invocation_source(event)
    result = validate_db_and_s3()

    return {
        'statusCode': 200,
        'body': json.dumps(result),
        'headers': {
            'Content-Type': 'application/json'
        }
    }
