from django.shortcuts import render, HttpResponse

# Create your views here.

def home(request):
    return HttpResponse('<center><h1>Hi</h1></center>')