from django.shortcuts import render, HttpResponse
from rest_framework.decorators import permission_classes, api_view
from rest_framework.permissions import IsAuthenticated
from farmer.models import UserProfile
from farmer.Recommender import profit_estimate
from farmer.models import Crops


def home(request):
    user = request.user.username
    #data = dataRetriever.getData()



    return HttpResponse('<center><h1>Home' + user + '</h1></center>')
