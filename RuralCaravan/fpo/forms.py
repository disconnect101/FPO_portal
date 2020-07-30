from django import forms 
from farmer.models import *
from django.contrib.auth.forms import UserCreationForm
# from django.contrib.auth.models import 


class FarmerForm(forms.ModelForm):
    # create meta class 
    class Meta: 
        # specify model to be used 
        model = Farmer
  
        # specify fields to be used 
        fields = [ 
            "user",             
            "first_name",           
            "last_name",            
            "aadhar",               
            "contact",               
            "village",               
            "district",               
            "pin",               
            "photo",              
        ] 

class DateInput(forms.DateInput):
    input_type = 'date'
class TimeInput(forms.TimeInput):
    input_type = 'time'

class MeetingsForm(forms.ModelForm): 
  
    # create meta class 
    class Meta: 
        # specify model to be used 
        model = Meetings
  
        # specify fields to be used 
        fields = [ 
            "organiser",        
            "agenda",        
            "venue",        
            "date", 
            "time", 
            "description", 
            "photo", 
        ]
        widgets = {
            'date': forms.DateInput(attrs={'type': 'date'}),
            'time': forms.TimeInput(attrs={'type': 'time'})
        }

class GovtForm(forms.ModelForm):
    # create meta class 
    class Meta:
        # specify model to be used 
        model = Govt
  
        # specify fields to be used 
        fields = [ 
            "name",
            "description",
        ]

class CropsForm(forms.ModelForm):
    # create meta class 
    class Meta: 
        # specify model to be used 
        model = Crops
  
        # specify fields to be used 
        fields = [ 
            "code",
            "name",   
            "type", 
            "max_cap", 
            "current_amount", 
            "weigth_per_land", 
            "guidance", 
            "live", 
        ]


class ProductsForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = Products

        # specify fields to be used
        fields = '__all__'


class OrdersForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = Orders

        # specify fields to be used
        fields = '__all__'

class plan_add_productForm(forms.Form):
    # create meta class
    ID = forms.IntegerField()              