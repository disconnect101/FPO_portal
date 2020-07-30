# Download the helper library from https://www.twilio.com/docs/python/install
from twilio.rest import Client

# Your Account Sid and Auth Token from twilio.com/console
# DANGER! This is insecure. See http://twil.io/secure
account_sid = 'AC25dc9403e0195434ea90f683850af7b3'
auth_token = '132c5437a64a32ad3ef03d8c0cb1c906'
client = Client(account_sid, auth_token)

TWILIO_NUMBER = '+12058585009'


def send_message(message_to, message_from=TWILIO_NUMBER, message_body='Enter Text To Send'):
    message = client.messages \
                    .create(
                        body=message_body,
                        from_=message_from,
                        to=message_to
                    )
    return message.sid


if __name__ == '__main__':

    recipient = '+919892629763'
    message = 'When you eliminate the impossible whatever remains, however improbable, must be the truth'

    send_message(recipient, TWILIO_NUMBER, message)