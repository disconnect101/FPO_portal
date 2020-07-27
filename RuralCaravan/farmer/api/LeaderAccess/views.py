from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from farmer.models import Leader, UserProfile
from farmer.api.utils import statuscode
from django.db.models import F


@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def downlinefarmers(request):
    user = request.user
    if user.category!='L':
        return Response(statuscode('20'))

    try:
        downlinefarmers = Leader.objects.get(user=user)\
                                        .farmers\
                                        .all()\
                                        .annotate(farmerid=F('id'), username=F('user__username'))\
                                        .values('farmerid',
                                                'username',
                                                'first_name',
                                                'last_name')
    except:
        return Response(statuscode(12))

    return Response(statuscode('0', { 'farmerlist': list(downlinefarmers) }))



@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def addfarmer(request):

    user = request.user
    if user.category!='L':
        return Response(statuscode('20'))

    try:
        farmerusername = request.data.get('farmerusername')
    except:
        return Response(statuscode('"farmerusername" argument missing'))
    try:
        farmer = UserProfile.objects.get(username=farmerusername)
        Leader.objects.get(user=user).farmers.add(farmer)
    except:
       return Response(statuscode('DB error or username invalid'))

    return Response(statuscode('0'))
