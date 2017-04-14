from __future__ import print_function
import boto3
import json
import time
import hashlib
import jwt
import stripe
import requests
import decimal
JWT_SECRET = 'secret'
JWT_ALGORITHM = 'HS256'
dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")
class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, decimal.Decimal):
            if o % 1 > 0:
                return float(o)
            else:
                return int(o)
        return super(DecimalEncoder, self).default(o)
        
def respond(err, res=None):
    return {
        'statusCode': '400' if err else '200',
        'body': err.message if err else res,
        'headers': {
            'Content-Type': 'application/json',
        },
    }

    
    
    
def make_payment(event):
    stripe_keys = {
      'secret_key': 'sk_test_Hrqp4whe1ZsCTyLzol7jth8v',
      'publishable_key': 'pk_test_mLVxfSZ0XoplPi6EppPDVic9'
    }
    
    stripe.api_key = stripe_keys['secret_key']
    card = {
        "number":event['card_number'],
        "cvc":event['cvc'],
        "exp_month": event['exp_month'],
        "exp_year": event['exp_year'],
    }
    token = stripe.Token.create(card=card)
    table = dynamodb.Table('Order')
    date = time.strftime("%d/%m/%Y")

    my_cart = event['my_cart']
    price = int(event['price']*100)
    print(price)
    user_id = jwt.decode(event['jwt'], JWT_SECRET, algorithms=JWT_ALGORITHM )
    customer = stripe.Customer.create(
        email=user_id['id'],
        source=token
    )
    charge = stripe.Charge.create(
        customer=customer.id,
        amount=price,
        currency='usd',
        description='Dynamo Charge'
    )   
    for item in my_cart:
        response = table.put_item(
           Item={
                'id': item['order_id'],
                'item_id ': item['item_id'],
                'item_name': item['item_name'],
                'price':item['price'],
                'date_time':date,
                'user_id':user_id['id']
            }
        )

    table = dynamodb.Table('Item')
    print(my_cart)
    for item in my_cart:
        response = table.update_item(
            Key={
                'id':item['item_id']
            },
            UpdateExpression="set in_stock = in_stock - :val",
            ExpressionAttributeValues={
                    ':val': decimal.Decimal(item['quantity'])
            },
            ReturnValues="UPDATED_NEW"
        )
        print(item['quantity'],item['item_id'])
    print("Payment succeeded:")
    #print(json.dumps(response, indent=4))
    print(user_id['id'])
    
    table3 = dynamodb.Table('Cart')
    for item in my_cart:
        response = table3.delete_item(
        Key={
            'id':item['cart_id']
        },
        ConditionExpression="user_id = :val",
        ExpressionAttributeValues= {
            ":val": user_id['id']
        }
    )
    

def lambda_handler(event, context):
    event = event['Records'][0]['Sns']['Message']
    json_acceptable_string = event.replace("'", "\"")
    event=json.loads(json_acceptable_string)
    print(event)
    dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")
    operation = event['operation']
    #type = event['type']
    #make payment
    make_payment(event)