from __future__ import print_function
import boto3
import json
import time
import hashlib
import jwt


JWT_SECRET = 'secret'
JWT_ALGORITHM = 'HS256'
dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")

def respond(err, res=None):
    return {
        'statusCode': '400' if err else '200',
        'body': err.message if err else res,
        'headers': {
            'Content-Type': 'application/json',
        },
    }
    
def make_order(event):
    # table = dynamodb.Table('Order')
    # #TODO hash ids for order
    date = time.strftime("%d/%m/%Y")
    # event['order_id']=  order_id
    table = dynamodb.Table('Cart')
    table2 = dynamodb.Table('Item')
    temp = table2.scan()
    temp = temp['Items']
    item_id_name_map ={}
    for entry in temp:
        item_id_name_map[entry['id']] = entry['item_name']
    #user_id = jwt.decode(event['jwt'], JWT_SECRET, algorithms=JWT_ALGORITHM )
    user_id = event['stripeEmail']
    items = table.scan()
    items = items['Items']
    my_cart = []
    price = 0
    i=0
    order_id_list=[]
    for item in items:
        #print(item['price'])
        print(item['user_id'],user_id)
        if item['user_id']==user_id:
            price +=float(item['price'])
            h = hashlib.new('ripemd160')
            date = int(time.time())
            print(date)
            h.update(str(date+i))
            order_id = h.hexdigest()
            print(item_id_name_map[item['item_id']],item['item_id'],item['price'],order_id)
            user_dict={}
            user_dict['item_name'] = item_id_name_map[item['item_id']]
            user_dict['item_id'] = item['item_id']
            user_dict['price'] = float(item['price'])
            user_dict['order_id'] = order_id
            user_dict['quantity'] = int(item['quantity'])
            user_dict['cart_id'] = item['id']
            print(user_dict)
            my_cart.append(user_dict)
            order_id_list.append(order_id)
            i+=1
            
    event['my_cart'] = my_cart
    print(my_cart)
    event['price'] = price
    event['operation']='payment'
    message = event
    print(message)
    client = boto3.client('sns')
    response = client.publish(
        TopicArn="arn:aws:sns:us-west-2:364727426203:orderpayment",
        #TargetArn="arn:aws:lambda:us-west-2:364727426203:function:stripeTest",
        #TargetArn="arn:aws:sns:us-west-2:364727426203:orderpayment:2af1d566-8976-44dc-9ea5-ad7b419e6f0",
        Message=json.dumps({'default':json.dumps(message)}),
        MessageStructure='json'
    )
    #print("PutItem Order succeeded:")
    #print(json.dumps(response, indent=4))
    return order_id_list
    
    

def lambda_handler(event, context):
    print(type(event))
    dynamodb = boto3.resource('dynamodb', region_name = "us-west-2")
    operation = event['operation']
    #type = event['type']
    #make an order
    if (operation == 'create'):
        order_id=make_order(event)
        return {"success":"true","order_id":order_id}
    else:
        return respond(ValueError('Unsupported method "{}"'.format(operation)))

