from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes
from farmer.api.utils import statuscode
from farmer.models import Crops, FarmerCropMap, Farmer, UserProfile, Orders, Land, Produce
from farmer.api.serializers import CropSerializer
import json
from django.core import serializers
from django.forms.models import model_to_dict
from farmer.SMSservice import sms
from farmer.Recommender.profit_estimate import Recommender
from django.db.models import Sum, Avg
from django.core import serializers


@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def cropplan(request, cropID=0):

    if cropID==0:
        user = request.user

        crop_subscriptions = FarmerCropMap.objects.filter(farmer=user).order_by('-date')
        crop_subscriptions_ids = []

        subscriptions = []
        for subscription in crop_subscriptions:
            crop_subscriptions_ids.append(subscription.crop.id)
            #subscriptions += [ CropSerializer(subscription.crop).data ]
            #subscriptions += [subscription]

        subscriptions = Crops.objects.filter(id__in=crop_subscriptions_ids).values()
        
        crops = Crops.objects.filter(live=True).exclude(id__in=list(crop_subscriptions_ids)).values('id',
                                                                                                    'code',
                                                                                                    'name',
                                                                                                    'type',
                                                                                                    'max_cap',
                                                                                                    'current_amount',
                                                                                                    'weigth_per_land',
                                                                                                    'guidance',
                                                                                                    'live',
                                                                                                    'image',
                                                                                                    'subscribers')
        unsubscribedCropList = list(crops)
        recommendationFailed = False
        recommendation = request.data.get('recommendation')


        if recommendation=='1':
            try:
                #### Recommendation System starts......

                avgInvestment = 0
                if request.data.get('investment'):
                    avgInvestment = request.data.get('investment')
                else:
                    investments = Orders.objects.values('date__year').filter(buyer=user).annotate(investment=Sum('price'))
                    investmentSum = 0

                    for investment in investments:
                        print(investment)
                        investmentSum += investment.get('investment')
                    if investments.count()==0:
                        print('invest')
                        raise Exception("not enough investments to predict")

                    avgInvestment = investmentSum/investments.count()

                land = Land.objects.filter(owner=user)
                if land.count()==0:
                    print("land")
                    raise Exception("No Land to predict profit for")

                landarea = 0
                if request.data.get('landarea'):
                    landarea = request.data.get('landarea')

                else:
                    landarea = land.aggregate(avglandarea=Avg('area'))
                    landarea = landarea.get('avglandarea')
                soil = land.first().soil

                print('investment : ',avgInvestment)
                print('landarea : ', landarea)
                farmerData = {
                    'investment': int(avgInvestment),
                    'landarea': int(landarea),
                    'soil': soil
                }

                recommender = Recommender()
                recommender.trainModel(recommender.gatherData())
                estimatedProfits = recommender.predict(farmerData, unsubscribedCropList)

                cropEstProfit = []
                i = 0
                for estprofit in estimatedProfits:
                    unsubscribedCropList[i].update({'estimatedProfit': estprofit})
                    i+=1

                #### Recommendation System ends......
            except Exception as e:
                print(str(e))
                recommendationFailed = True
                for temp in unsubscribedCropList:
                    temp.update({'estimatedProfit': 0})

        else:
            for temp in unsubscribedCropList:
                temp.update({'estimatedProfit': 0})

        data = {
            'subscriptions': subscriptions,
            'not_subscribed': unsubscribedCropList,
            #'not_subscribed': cropEstProfit,
        }
        if recommendationFailed:
            return Response(statuscode('25', data))
        return Response(statuscode('0', data))

    else:
        user = request.user
        try:
            crop = Crops.objects.filter(id=int(cropID)).values().first()
        except:
            return Response(statuscode('19'))

        return Response(statuscode('0', { 'data': crop }))



@api_view(['GET'])
@permission_classes((IsAuthenticated, ))
def cropproducts(request, cropID):

    try:
        cropproducts = Crops.objects.get(id=cropID).products.filter(available=True).values()
    except:
        return Response(statuscode('12'))

    data = {
        'data': list(cropproducts)
    }
    return Response(statuscode('0', data))


@api_view(['POST', 'DELETE'])
@permission_classes((IsAuthenticated, ))
def confcrop(request, cropID):

    if request.method=='POST':
        user = request.user
        landarea = int(request.data.get('landarea'))

        try:
            crop = Crops.objects.get(id=cropID, live=True)
        except:
            return Response(statuscode('19'))
        try:
            FarmerCropMap(farmer=user, crop=crop, landarea=landarea).save()
            crop.current_amount += crop.weigth_per_land*landarea
            crop.subscribers += 1
            crop.save()
        except:
            return Response(statuscode('12'))

        message = "You have successfully subcribed to " + crop.name
        try:
            if user.category != 'N':
                send_to = str(user.contact_set.first().number)
                sms.send_message('+91' + send_to, sms.TWILIO_NUMBER, message)
        except:
            print("SMS could not be sent")
        return Response(statuscode('0'))

    if request.method=='DELETE':
        user = request.user
        try:
            crop = Crops.objects.get(id=cropID, live=True)
        except:
            return Response(statuscode('19'))
        try:
            subscription = FarmerCropMap.objects.get(farmer=user, crop=crop)
            crop.current_amount -= subscription.landarea*crop.weigth_per_land
            crop.subscribers -= 1
            crop.save()
            subscription.delete()
        except:
            return Response(statuscode('12'))

        return Response(statuscode('0'))


@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def produce(request):
    user = request.user

    try:
        produce = Produce.objects.filter(owner=user).order_by('-date').values()
    except Exception as e:
        print(str(e))
        return Response(statuscode('12'))

    data = {
        'data': produce,
    }

    return Response(statuscode('0', data))