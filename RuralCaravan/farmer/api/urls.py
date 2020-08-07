from django.urls import path
from farmer.api.LoginSignup import views as auth
from farmer.api.CatalogueOrders import views as catalogue
from farmer.api.MeetingsNewsSchemes import views as notice
from farmer.api.CropPlan import views as crops
from farmer.api.Balancesheet import views as balancesheet
from farmer.api.LeaderAccess import views as leaderaccess

app_name = "farmer"

urlpatterns = [
    path('register/', auth.register, name='register'),
    path('login/', auth.CustomAuthToken.as_view(), name='login'),
    path('userdata/', auth.userData, name='getuserdata'),
    path('updateuserdata/', auth.updateUserData, name='updateuserdata'),
    path('register/OTP/', auth.verifyOTP, name='OTP'),
    path('villages/', auth.getVillageInfo, name='OTP'),
    path('catalogue/', catalogue.catalogue, name='catalogue'),
    path('product/', catalogue.product, name='product'),
    path('kart/', catalogue.kart, name='kart'),
    path('kart/delete/', catalogue.kartdelete, name='kartdelete'),
    path('order/', catalogue.order, name='order'),
    path('meetings/', notice.meetings, name='meetings'),
    path('govt/', notice.govtSchemes, name='addfarmerdownline'),
    path('meetings/rsvp/', notice.rsvpMeeting, name='meetings'),
    path('crops/<int:cropID>/', crops.cropplan, name='cropplan'),
    path('cropproducts/<int:cropID>/', crops.cropproducts, name='cropproducts'),
    path('confcrop/<int:cropID>/', crops.confcrop, name='confcrop'),
    path('produce/', crops.produce, name='produce'),
    path('balancesheet/', balancesheet.balancesheet, name='balancesheet'),
    path('leaderaccess/', leaderaccess.downlinefarmers, name='downlinefarmers'),
    path('leaderaccess/add/', leaderaccess.addfarmer, name='addfarmerdownline'),
]