from django.urls import path
from farmer.api.LoginSignup import views as auth
from farmer.api.CatalogueOrders import views as catalogue

app_name = "farmer"

urlpatterns = [
    path('register/', auth.register, name='register'),
    path('login/', auth.CustomAuthToken.as_view(), name='login'),
    path('userdata/', auth.userData, name='getuserdata'),
    path('register/OTP/', auth.verifyOTP, name='OTP'),
    path('villages/', auth.getVillageInfo, name='OTP'),
    path('catalogue/', catalogue.catalogue, name='catalogue'),
    path('product/', catalogue.product, name='product'),
    path('kart/', catalogue.kart, name='kart'),
    path('order/', catalogue.order, name='order'),
]