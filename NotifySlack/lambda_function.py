import json
import urllib

def lambda_handler(event, context):
    print(event['Records'][0]['Sns']['Message'])
    message = event['Records'][0]['Sns']['Message']
    payload = urllib.parse.urlencode({ "payload" : {"text": json.dumps(json.loads(message), indent = 4, sort_keys = True), "icon_emoji": ":ghost:", "username": "djangobot"}}).encode("utf-8")
    
    try:
        r = urllib.request.urlopen("https://hooks.slack.com/services/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", payload)
        return {"success": True}
    except:
        return {"success": False}