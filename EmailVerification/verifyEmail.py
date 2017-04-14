import boto3
import hashlib
import time

def verify_email(event, response):
    dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")
    table = dynamodb.Table('Customer')
    entry = table.get_item(
        Key={
            'id': event['id'],
        }
    )
    # print entry
    
    if entry['Item']['verified'] == "True":
        response["success"] = "True"
        response["AlreadyVerified"] = "True"
        return response


    if entry['Item']['hash_val'] and (entry['Item']['hash_val'] == event['hash_val']):
        table.update_item(
            Key={
                'id': event['id'],
            },
            UpdateExpression="set verified = :r",
            ExpressionAttributeValues={
                ':r': bool(1)
            },
            ReturnValues="UPDATED_NEW"
        )
        response["success"] = "True"
    else:
        response["success"] = "False"
    return response
    

def lambda_handler(event, context):
    response = dict()
    response['id'] = event['id']
    return verify_email(event, response)