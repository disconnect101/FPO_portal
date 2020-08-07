from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from farmer.models import Leader, UserProfile, Farmer
from farmer.api.utils import statuscode
from django.db.models import F
from rest_framework.authtoken.models import Token



@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def downlinefarmers(request):
    user = request.user
    if user.category!='L':
        return Response(statuscode('20'))

    try:
        downlinefarmers = Leader.objects.get(user=user).farmers.all()
    except:
        return Response(statuscode(12))

    farmer_details = Farmer.objects.select_related('user').filter(user__in=downlinefarmers)
    tokens = Token.objects.select_related('user').filter(user__in=downlinefarmers)

    farmer_list = []
    for farmer in farmer_details:
        farmer_name = farmer.first_name + " " + farmer.last_name
        token = tokens.get(user=farmer.user).key
        userID = farmer.user.username
        farmer_list.append({'farmer_name': farmer_name, 'token': token, 'userid': userID})

    return Response(statuscode('0', { 'farmerlist': farmer_list }))



@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def addfarmer(request):

    user = request.user
    if user.category!='L':
        return Response(statuscode('20'))

    try:
        farmertoken = request.data.get('farmertoken')
    except:
        return Response(statuscode('"farmerusername" argument missing'))
    try:
        token = Token.objects.get(key=farmertoken)
        Leader.objects.get(user=user).farmers.add(token.user)
    except:
       return Response(statuscode('DB error or username invalid'))

    return Response(statuscode('0'))
