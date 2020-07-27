from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.models import Products, Kart, Orders, Crops
from farmer.api.serializers import ProductSerializer, KartSerializer
from django.core import serializers
from .utils import *
from farmer.api.utils import statuscode
import json


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

        kartList = {
            'data': list(kart)
        }
        return Response(kartList)


    if request.method=='POST':
        user = request.user
        kartItemID = request.data.get('id')
        quantity = request.data.get('quantity')

        try:
            kartItem = Kart.objects.get(id=kartItemID, user=user)
        except:
            return Response({
                'statuscode': '13'
            })
        kartItem.quantity = quantity
        try:
            kartItem.save()
        except:
            return Response({
                'statuscode': '18'
            })
        return Response({
            'statuscode': '0'
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
                orderID = makeOrder(type=paymentType,
                            buyer=user,
                            item=product,
                            rate=product.rate,
                            quantity=quantity,
                            price=product.rate*quantity)
            except Exception as e:
                return Response({
                    'statuscode': str(e)
                })
            return Response({
                'statuscode': '0',
                'orderID': orderID,
            })

        else:
            kart = Kart.objects.filter(user=user)
            productIDs = []
            for kartItem in kart:
                productIDs += str(kartItem.item.id)

            products = Products.objects.filter(id__in=productIDs)
            totalCost = 0

            if paymentType=='PEW':
                for kartItem in kart:
                    totalCost += kartItem.item.rate * kartItem.quantity
                ewalletBalance = Ewallet.objects.get(user=user).amount
                if ewalletBalance < totalCost:
                    return Response(statuscode('17'))

            orderIDs = []
            for product in products:
                quantity = kart.get(item=product).quantity
                try:
                    orderID = makeOrder(type=paymentType,
                                buyer=user,
                                item=product,
                                rate=product.rate,
                                quantity=quantity,
                                price=product.rate*quantity)
                except Exception as e:
                    return Response(statuscode(str(e)))

                orderIDs.append(orderID)

            kart.delete()
            return Response(statuscode('0', {'orderIDs': orderIDs}))

    if request.method=='GET':
        user = request.user
        try:
            completed_orders = Orders.objects.filter(buyer=user, is_delivered=True).values()
            pending_orders = Orders.objects.filter(buyer=user, is_delivered=False).values()
        except:
            return Response(statuscode('12'))

        completed_orders_list = list(completed_orders)
        pending_orders_list = list(pending_orders)

        data = {
            'completed_orders': completed_orders,
            'pending_orders': pending_orders,
        }

        return Response(statuscode('0', data))

