from django.contrib import admin
from django.urls import path, include
from farmer import views


app_name = "farmer"

urlpatterns = [
    path('home/', views.home, name='home'),
]




