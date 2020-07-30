from django import forms 
from farmer.models import *
from django.contrib.auth.forms import UserCreationForm
# from django.contrib.auth.models import 

def if_phone(x):
    if x.isnumeric and x.len()==10:
        return True 
    else:
        return False



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
            # "profession",
        ]
    def test_value(self):
        data = self.cleaned_data.get('contact')
        # if not is_phone(data):
        raise form.ValidationError('Not a phone no')
        # return is_phone()     


class LeaderForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = Leader

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
            "profession",
            "farmers",
        ]


class UserProfileForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = UserProfile

        # specify fields to be used
        fields = [
            "username",
            # "date_joined",
            # "last_login",

            "is_admin",
            "is_active",
            "is_staff",
            "is_superviser",
            "category",
            "password",
        ]


class leader_add_farmerForm(forms.Form):
    # create meta class
    username = forms.CharField(max_length=100)


class FPOLedgerForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = FPOLedger

        # specify fields to be used
        fields = '__all__'


class ProduceForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = Produce

        # specify fields to be used
        fields = '__all__'


class TransactionForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = ew_transaction

        # specify fields to be used
        fields = [
            "refno",
            "user",
            "amount",
            "description"
        ]


class OrdersForm(forms.ModelForm):
    # create meta class
    class Meta:
        # specify model to be used
        model = Orders

        # specify fields to be used
        fields = '__all__'