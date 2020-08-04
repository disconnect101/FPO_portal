from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.models import Products, Kart, Orders, Crops, ew_transaction
from farmer.api.serializers import ProductSerializer, KartSerializer
from django.core import serializers
from .utils import *
from farmer.api.utils import statuscode
import json
from farmer.SMSservice import sms


@api_view(['POST',])
@permission_classes((IsAuthenticated, ))
def catalogue(request):

    user = request.user
    category = request.data.get('category')
    products = Products.objects.filter(category=category, available=True).values('id',
                                                                                'name',
                                                                                'category',
                                                                                'rate',
                                                                                'image')

    data = {
        'data': list(products)
    }
    return Response(statuscode('0', data))

    # categorizedProducts = {}
    # for product in allProducts:
    #      if product.get('category') in categorizedProducts:
    #          categorizedProducts[product.get('category')] += [product]
    #      else:
    #          categorizedProducts[product.get('category')] = [product]



@api_view(['POST',])
@permission_classes((IsAuthenticated, ))
def product(request):

    productID = request.data.get('id')
    productID = int(productID)

    try:
        product = Products.objects.get(id=productID)
    except Exception as e:
        return Response({
            'statuscode': '11'
        })

    serializedProduct = ProductSerializer(product).data
    return Response(serializedProduct)


@api_view(['POST', 'GET', 'PUT'])
@permission_classes((IsAuthenticated, ))
def kart(request):

    if request.method=='PUT':
        user = request.user
        productID = int(request.data.get('id'))
        quantity = int(request.data.get('quantity'))

        try:
            product = Products.objects.get(id=productID)
        except Exception as e:
            return Response({
                'statuscode': '11'
            })

        kart = Kart(item=product, user=user, quantity=quantity)
        print(kart)
        try:
            kart.save()
            return Response({
                'statuscode': '0'
            })
        except:
            return Response({
                'statuscode': '3'
            })

    if request.method=='GET':
        user = request.user
        try:
            kart = Kart.objects.filter(user=user).values()
        except:
            return Response({
                'statuscode': '12'
            })

        totalamount = 0
        for kartItem in kart:
            productID = kartItem.get('item_id')
            rate = Products.objects.get(id=productID).rate
            totalamount += rate*kartItem.get('quantity')

        kartList = {
            'data': list(kart),
            'total': totalamount
        }
        return Response(kartList)


    if request.method=='POST':
        user = request.user
        kartItemID = request.data.get('id')
        quantity = request.data.get('quantity')

        try:
            kartItems = Kart.objects.filter(user=user)
        except:
            return Response({
                'statuscode': '13'
            })
        kartItem = kartItems.get(id=kartItemID)
        kartItem.quantity = quantity
        try:
            kartItem.save()
        except:
            return Response({
                'statuscode': '18'
            })

        total = 0
        for kartItem in kartItems:
            total += kartItem.quantity*kartItem.item.rate

        return Response({
            'statuscode': '0',
            'total': total,
        })


@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def kartdelete(request):
    user = request.user
    kartItemID = request.data.get('id')

    try:
        kartItem = Kart.objects.get(id=kartItemID, user=user)
    except:
        return Response({
            'statuscode': '13'
        })

    try:
        kartItem.delete()
    except:
        return Response({
            'statuscode': '14'
        })

    return Response({
        'statuscode': '0'
    })


def send_sms_conf(sender, instance=None, created=False, **kwargs):
    if created:
        message = "Your order has been placed successfully, OrderID : " + str(instance.id)
        if instance.buyer.category!='N':
            send_to = str(instance.buyer.contact_set.first().number)
            sms.send_message( '+91'+send_to, sms.TWILIO_NUMBER, message)


@api_view(['POST', 'GET', ])
@permission_classes((IsAuthenticated,))
def order(request):

    if request.method=='POST':
        user = request.user
        productID = int(request.data.get('id'))
        paymentType = request.data.get('type')

        if int(productID)!=-1:
            quantity = int(request.data.get('quantity'))
            try:
                product = Products.objects.get(id=productID)
            except Exception as e:
                return Response({
                    'statuscode': '11'
                })

            try:
                order = Orders(type=paymentType,
                               buyer=user,
                               item=product,
                               rate=product.rate,
                               quantity=quantity,
                               price=product.rate * quantity)
            except Exception as e:
                return Response({
                    'statuscode': str(e)
                })

            if paymentType=="CAS" or paymentType=="PEW":
                order.is_paid = True

            if paymentType == "PEW":
                description = "Paid to FPO for OrderID: " + str(order.id)
                try:
                    refno = makeTransaction(user, order.price, description)
                except Exception as e:
                    return Response(statuscode(str(e)))
            try:
                order.save()
                message = "Your order has been placed successfully, Products : " + order.item.name + ", OrderID : " + str(order.id)
                if order.buyer.category != 'N':
                    send_to = str(order.buyer.contact_set.first().number)
                    sms.send_message('+91' + send_to, sms.TWILIO_NUMBER, message)
            except Exception as e:
                return Response(statuscode('0'))

            return Response({
                'statuscode': '0',
                'orderID': order.id,
            })

        else:
            kart = Kart.objects.filter(user=user)
            if kart.count()==0:
                return Response(statuscode('0'))
            productIDs = []
            for kartItem in kart:
                productIDs += str(kartItem.item.id)

            products = Products.objects.filter(id__in=productIDs)
            totalCost = 0

            for kartItem in kart:
                totalCost += kartItem.item.rate * kartItem.quantity
            if paymentType=='PEW':
                ewalletBalance = Ewallet.objects.get(user=user).amount
                if ewalletBalance < totalCost:
                    return Response(statuscode('17'))

            orderIDs = []
            orders = []
            amount = 0
            for kartentry in kart:
                quantity = kartentry.quantity
                try:
                    orders.append(Orders(type=type,
                                           buyer=user,
                                           item=kartentry.item,
                                           rate=kartentry.item.rate,
                                           quantity=quantity,
                                           price=kartentry.item.rate * quantity))
                except Exception as e:
                    return Response(statuscode(str(e)))

            if paymentType == "PEW":
                description = ""
                try:
                    refno = makeTransaction(user, totalCost, description)
                except Exception as e:
                    return Response(statuscode(str(e)))

            productsstring = " "
            for order in orders:
                if paymentType=="CAS" or paymentType=="PEW":
                    order.is_paid = True
                order.save()
                orderIDs.append(order.id)
                productsstring += " " + (order.item.name) + ","

            try:
                message = "Your order has been placed successfully, Products : " + productsstring
                if order.buyer.category != 'N':
                    send_to = str(order.buyer.contact_set.first().number)
                    sms.send_message('+91' + send_to, sms.TWILIO_NUMBER, message)
            except:
                print("sms couldn't be sent")

            if paymentType=="PEW":
                description = "Paid to FPO for OrderIDs: " + str(orderIDs)
                transaction = ew_transaction.objects.get(refno=refno)
                transaction.description = description
                transaction.save()

            kart.delete()
            return Response(statuscode('0', {'orderIDs': orderIDs, 'total_cost': totalCost}))

    if request.method=='GET':
        user = request.user
        try:
            completed_orders = Orders.objects.filter(buyer=user, is_delivered=True).order_by('-date').values()
            pending_orders = Orders.objects.filter(buyer=user, is_delivered=False).order_by('-date').values()
        except:
            return Response(statuscode('12'))

        completed_orders_list = list(completed_orders)
        pending_orders_list = list(pending_orders)

        for completed_order in completed_orders_list:
            product = Products.objects.get(id=completed_order.get('item_id'))
            completed_order.update({
                                        'product_name': product.name,
                                        'rate': product.rate,
                                        'image': product.image.url
                                    })

        for pending_order in pending_orders_list:
            product = Products.objects.get(id=pending_order.get('item_id'))
            pending_order.update({
                                        'product_name': product.name,
                                        'rate': product.rate,
                                        'image': product.image.url
                                    })

        data = {
            'completed_orders': completed_orders,
            'pending_orders': pending_orders,
        }

        return Response(statuscode('0', data))

