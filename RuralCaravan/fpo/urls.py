from django.urls import path
from fpo import views

urlpatterns = [
		path('', views.test, name='home'),

]