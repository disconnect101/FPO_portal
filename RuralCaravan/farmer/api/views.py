from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.api.serializers import RegistrationSerializer, FarmerSerializer
from rest_framework.authtoken.models import Token

from farmer.models import Farmer

@api_view(['POST'],)
def register(request):
    if request.method=='POST':
        serializer = RegistrationSerializer(data=request.data)
        data = {}
        if serializer.is_valid():
            User = serializer.save()
            data = {
                'response': 'Registration Successfull',
                'username': User.username,
                'category': User.category,
                'token': Token.objects.get(user=User).key,
            }
        else:
            data = serializer.errors

        return Response(data)



@api_view(['GET'],)
@permission_classes((IsAuthenticated, ))
def getUserData(request):
    user = request.user
    try:
        farmer = Farmer.objects.get(user=user)
        print(farmer.first_name)
    except Farmer.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    serializer = FarmerSerializer(farmer)
    return Response(serializer.data)