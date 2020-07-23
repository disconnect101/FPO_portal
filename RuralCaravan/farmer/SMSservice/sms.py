from twilio.rest import Client


account_sid = 'AC27fb68edbb99b7236c7008ce666adf78'
auth_token = 'cc5d9bbe9f0a53c673242cc2546e60eb'

client = Client(account_sid, auth_token)


TWILIO_NUMBER = "+19382010140"


def send_message(message_to, message_from, message_body):
    message = client.messages.create(
                        body=message_body,
                        from_=message_from,
                        to=message_to,
                    )
    return message.sid


