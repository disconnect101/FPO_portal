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
        ewallet.amount -= amount
    except:
        raise Exception('16')

    if ewallet.amount<0:
        raise Exception("17")

    refno = generateRefno()
    ew_t = ew_transaction(refno=refno,
                          user=user,
                          amount=-1*amount,
                          description=description,
                          currrent_amount=ewallet.amount)
    try:
        ewallet.save()
        ew_t.save()
    except:
        raise Exception('16')

    return refno




def makeOrder(type, buyer, item, rate, quantity, price):
    order = Orders(type=type,
                   buyer=buyer,
                   item=item,
                   rate=item.rate,
                   quantity=quantity,
                   price=item.rate * quantity)

    if type == "COD":
        pass
    elif type == "CAS":
        order.is_paid = True
    elif type == "PEW":
        description = "Paid to FPO for productID: " + str(item.id)
        try:
            refno = makeTransaction(user=buyer,
                                    amount=item.rate * quantity,
                                    description=description)
        except Exception as e:
            raise Exception(str(e))
        order.is_paid = True
    else:
        pass    ##UPI payment

    try:
        order.save()
    except:
        raise Exception('15')

    return order.id





