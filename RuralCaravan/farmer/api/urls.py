from django.urls import path
from farmer.api import views

from rest_framework.authtoken.views import obtain_auth_token

app_name = "farmer"

urlpatterns = [
    path('register/', views.register, name='register'),
    path('login/', views.CustomAuthToken.as_view(), name='login'),
    path('userdata/', views.userData, name='getuserdata'),
    path('register/OTP/', views.verifyOTP, name='OTP'),
    path('villages/', views.getVillageInfo, name='OTP'),
]