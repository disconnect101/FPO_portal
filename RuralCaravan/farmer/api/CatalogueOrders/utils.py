from farmer.models import Ewallet, ew_transaction, Orders
from _datetime import datetime
from rest_framework.response import Response
import random



def generateRefno():
    num = ew_transaction.objects.all().count() + 1
    year = datetime.now().year
    refno = "REF" + str(year) + str(random.randint(100, 999)) + str(num)
    return refno




def makeTransaction(user, amount, description):
    try:
        ewallet = Ewallet.objects.get(user=user)
        #ewallet.amount -= amount
    except:
        raise Exception('16')

    if ewallet.amount-amount<0:
        raise Exception("17")

    refno = generateRefno()
    current_amount = ew_transaction.objects.filter(user=user).order_by('-date').first().currrent_amount - amount
    ew_t = ew_transaction(refno=refno,
                          user=user,
                          amount=-1*amount,
                          description=description,
                          currrent_amount=current_amount)
    try:
        #ewallet.save()
        ew_t.save()
    except Exception as e:
        raise Exception(e)

    return refno






