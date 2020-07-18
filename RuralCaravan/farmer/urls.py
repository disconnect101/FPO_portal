from django.contrib import admin
from django.urls import path, include
from farmer import views

urlpatterns = [
    path('', views.home, name='home'),
]