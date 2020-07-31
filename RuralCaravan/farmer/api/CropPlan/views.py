from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes
from farmer.api.utils import statuscode
from farmer.models import Crops, FarmerCropMap, Farmer, UserProfile
from farmer.api.serializers import CropSerializer
import json
from django.core import serializers
from django.forms.models import model_to_dict
from farmer.SMSservice import sms


@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def cropplan(request, cropID=0):

    if cropID==0:
        user = request.user

        crop_subscriptions = FarmerCropMap.objects.filter(farmer=user).order_by('-date')
        crop_subscriptions_ids = []

        subscriptions = []
        for subscription in crop_subscriptions:
            crop_subscriptions_ids.append(subscription.crop.id)
            subscriptions += [ CropSerializer(subscription.crop).data ]

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

        data = {
            'subscriptions': subscriptions,
            'not_subscribed': unsubscribedCropList,
        }
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
            subscription.delete()
        except:
            return Response(statuscode('12'))

        return Response(statuscode('0'))