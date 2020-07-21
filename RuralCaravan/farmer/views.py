from django.shortcuts import render, HttpResponse
from rest_framework.decorators import permission_classes, api_view
from rest_framework.permissions import IsAuthenticated
from farmer.models import UserProfile
# Create your views here.


@api_view(['GET', ])
@permission_classes((IsAuthenticated,))
def home(request):
    user = request.user.username
    return HttpResponse('<center><h1>Home' + user + '</h1></center>')
