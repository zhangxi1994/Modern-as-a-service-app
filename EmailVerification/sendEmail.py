import boto3
import hashlib
import uuid
import decimal

def lambda_handler(event, context):
    err = False
    resp_success ={
        "success" : True
    }
    resp_failure ={
        "success" : False
    }
    
    dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")
    table = dynamodb.Table('Customer')
    
    entry = table.get_item(
        Key={
            'id': event['customer']['id'],
        }
    )
    print entry
    
    if not 'Item' in entry:
        return resp_failure
        
    if entry['Item']['verified'] == "True":
        resp_failure['AlreadyVerified'] = True
        return resp_failure

    response = table.update_item(
                Key={
                    'id': event['customer']['id'],
                },
                UpdateExpression="set hash_val = :r",
                ExpressionAttributeValues={
                    ':r': uuid.uuid4().hex
                },
                ReturnValues="UPDATED_NEW"
            )

    ses = boto3.client('ses')
    fromEmail = "thejaswi01@gmail.com"
    toEmail = event['customer']['id']
    subject = "Verify Email"
    message = "Test " + response['Attributes']['hash_val']
    h = response['Attributes']['hash_val']
    user  = event['customer']['id']
    message =  "https://sp1as0yiy2.execute-api.us-west-2.amazonaws.com/Test/verify" + "?hash_val=" + h + "&user=" + user
    
    response = ses.send_email(
			Source=fromEmail,
			Destination={
				'ToAddresses': [
					toEmail,
				],
			},
			Message={
				'Subject': {
					'Data': subject,
					'Charset': 'utf8'
				},
				'Body': {
					'Text': {
						'Data': message,
						'Charset': 'utf8'
					}
				}
			}
		)


    if err:
        return resp_failure
    else:
        return resp_success