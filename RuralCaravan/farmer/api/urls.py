from django.urls import path
from farmer.api import views

from rest_framework.authtoken.views import obtain_auth_token

app_name = "farmer"

urlpatterns = [
    path('register/', views.register, name='register'),
    path('login/', obtain_auth_token, name='login'),
    path('getuserdata/', views.getUserData, name='getuserdata')
]