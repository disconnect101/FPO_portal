from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes
from farmer.api.utils import statuscode
from farmer.models import Crops, FarmerCropMap, Farmer

@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def cropPlan(request):

    user = request.user
    farmer = Farmer.objects.get(user=user)
    crop_subscriptions = FarmerCropMap.objects.filter(farmer=farmer)
    crop_subscriptions_ids = []

    for crop in crop_subscriptions:
        crop_subscriptions_ids.append(crop.id)

    crops = Crops.objects.filter(live=True).exclude(id__in=list(crop_subscriptions_ids)).values()
    unsubscribedCropList = list(crops)
    crops = Crops.objects.filter(live=True, id__in=crop_subscriptions_ids).values()
    crop_subscriptions = list(crops)

    data = {
        'subscribed': crop_subscriptions,
        'not_subscribed': unsubscribedCropList,
    }
    return Response(statuscode('0', data))


