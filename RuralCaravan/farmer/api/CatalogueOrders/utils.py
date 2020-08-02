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
    ew_t = ew_transaction(refno=refno,
                          user=user,
                          amount=-1*amount,
                          description=description)
    try:
        #ewallet.save()
        ew_t.save()
    except Exception as e:
        raise Exception(e)

    return refno






