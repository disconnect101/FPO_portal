from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager
from django.conf import settings
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token

from ckeditor.fields import RichTextField


class MyUserProfileManager(BaseUserManager):   ## not a Model
    def create_user(self, username, category='F', password=None):
        if not username:
            raise ValueError("Username required")
        if not category:
            raise ValueError("Category required")

        user = self.model(username=username, category=category)

        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, username, category='A', password=None ):
        user = self.create_user(username=username, category=category, password=password)

        user.is_admin = True
        user.is_staff = True
        user.is_superuser = True
        user.is_superviser = True
        user.save(using=self._db)

        return user


class UserProfile(AbstractBaseUser):
    username = models.CharField(max_length=50, unique=True)
    date_joined = models.DateTimeField(verbose_name='date joined', auto_now_add=True)
    last_login = models.DateTimeField(verbose_name='last login', auto_now=True)

    is_admin = models.BooleanField(default=False)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    is_superviser = models.BooleanField(default=False)

    CATEGORIES = (
        ('A', 'FPO Admin'),
        ('L', 'Leader'),
        ('F', 'Farmer'),
    )

    category = models.CharField(max_length=10, choices=CATEGORIES)

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = ['category', ]

    objects = MyUserProfileManager()

    def __str__(self):
        return self.username

    def has_perm(self, perm, obj=None):
        return self.is_admin

    def has_module_perms(self, app_label):
        return True


@receiver(post_save, sender=settings.AUTH_USER_MODEL)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)





class Crops(models.Model):
    code = models.CharField(max_length=6)
    name = models.CharField(max_length=100)
    type = models.CharField(max_length=100)
    max_cap = models.FloatField()
    current_amount = models.FloatField(default=0)
    weigth_per_land = models.FloatField()
    guidance = RichTextField(blank=True, null=True)
    live = models.BooleanField(default=True)
    def __str__(self):
        return self.name

    




class Farmer(models.Model):
    user = models.OneToOneField(UserProfile, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    aadhar = models.CharField(max_length=12, null=True, blank=True)
    contact = models.CharField(max_length=10)
    village = models.CharField(max_length=100)
    district = models.CharField(max_length=100)
    pin = models.IntegerField()
    photo = models.ImageField(upload_to='media/profile_photos/', null=True, blank=True)
        ####### Changes By Aniket ###########
    govt_schemes = models.ManyToManyField('Govt', blank=True)
    def __str__(self):
        return self.user.username + '-' +  self.first_name


class FarmerCropMap(models.Model):
    farmer = models.ForeignKey(Farmer, on_delete=models.CASCADE)
    crop = models.ForeignKey(Crops, on_delete=models.CASCADE)
    date = models.DateTimeField(auto_now_add=True)


class Leader(models.Model):
    user = models.OneToOneField(UserProfile, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    aadhar = models.CharField(max_length=12, null=True, blank=True)
    contact = models.CharField(max_length=10)
    village = models.CharField(max_length=100)
    district = models.CharField(max_length=100)
    pin = models.IntegerField()
    photo = models.ImageField(upload_to='media/profile_photos/', null=True, blank=True)
    profession = models.CharField(max_length=100, null=True, blank=True)
    farmers = models.ManyToManyField(Farmer)

    def __str__(self):
        return self.user.username + '-' + self.first_name



class BankDetails(models.Model):
    user = models.OneToOneField(UserProfile, on_delete=models.CASCADE)
    acc_no = models.CharField(max_length=15)
    ifsc = models.CharField(max_length=11)



class Land(models.Model):
    owner = models.ForeignKey(UserProfile, on_delete=models.CASCADE)
    area = models.CharField(max_length=10)

    SOIL_CATEGORY = (
        ('A', 'Type A'),
        ('B', 'Type B'),
        ('C', 'Type C'),
    )

    soil = models.CharField(max_length=3, choices=SOIL_CATEGORY)
    address = models.CharField(max_length=150)



class Meetings(models.Model):
    organiser = models.CharField(max_length=50)
    agenda = models.CharField(max_length=100)
    venue = models.CharField(max_length=150)
    date = models.DateField()
    time = models.TimeField()
    description = RichTextField(blank=True, null=True)
    photo = models.ImageField(upload_to='media/meeting_photos/', null=True, blank=True)







class Products(models.Model):
    name = models.CharField(max_length=100)
    associated_crop = models.ForeignKey(Crops, on_delete=models.SET_DEFAULT, default=-1)

    CATEGORY = (
        ('SED', 'Seeds'),
        ('FER', 'Fertilizer'),
        ('PES', 'Pesticide'),
        ('EQP', 'Equipment'),
        ('OTH', 'Others'),
    )
    category = models.CharField(max_length=3, choices=CATEGORY)
    rate = models.FloatField()
    description = RichTextField(null=True, blank=True)
    image = models.ImageField(upload_to='images/products_images/', null=True, blank=True)



class Orders(models.Model):
    TYPE = (
        ('COD', 'Cash on Delivery'),
        ('UPI', 'UPI Payment'),
        ('CAS', 'Pay at FPO office'),
        ('PEW', 'Pay with E-Wallet'),
    )
    type = models.CharField(max_length=3, choices=TYPE)
    item = models.ForeignKey(Products, on_delete=models.SET_DEFAULT, default=-1)
    buyer = models.ForeignKey(UserProfile, on_delete=models.PROTECT)
    price = models.FloatField()
    rate = models.FloatField()
    quantity = models.IntegerField()
    is_paid = models.BooleanField(default=False)
    is_delivered = models.BooleanField()



class Produce(models.Model):
    crop = models.ForeignKey(Crops, on_delete=models.PROTECT)
    amount = models.FloatField()
    date = models.DateTimeField(auto_now_add=True)
    land = models.ForeignKey(Land, models.SET_NULL, null=True, blank=True)
    quality = models.BooleanField()
    owner = models.ForeignKey(UserProfile, on_delete=models.PROTECT)



class Kart(models.Model):
    owner = models.ForeignKey(UserProfile, on_delete=models.PROTECT)
    item = models.ForeignKey(Products, on_delete=models.SET_DEFAULT, default=-1)
    quantity = models.IntegerField()
    rate = models.FloatField()
    price = models.FloatField()



class ew_transaction(models.Model):
    refno = models.CharField(max_length=20)
    user = models.ForeignKey(UserProfile, on_delete=models.PROTECT)
    amount = models.FloatField()
    date = models.DateTimeField(auto_now_add=True)
    currrent_amount = models.FloatField()
    description = models.CharField(max_length=200)


class FPOLedger(models.Model):
    crop = models.ForeignKey(Crops, on_delete=models.PROTECT)
    amount_sold = models.FloatField()
    rate = models.FloatField()
    price = models.FloatField()
    date = models.DateTimeField(auto_now_add=True)
    sold_to = models.CharField(max_length=150)
    description = models.CharField(max_length=200)



class Produce_FPOLedger_Map(models.Model):
    produce = models.ForeignKey(Produce, on_delete=models.PROTECT)
    fpoledger = models.ForeignKey(FPOLedger, on_delete=models.PROTECT)
    money_received = models.FloatField()      #Rupees
    crop_sold = models.FloatField()           #Weight of crop sold
    refno = models.CharField(max_length=15)



class Govt(models.Model):
    name = models.CharField(max_length = 200)
    description = models.TextField()    


    ###### Changes By Aniket #########

class MeetingToken(models.Model):
    # This is a special token that we need to create in order to track the attendance.
    token_number = models.CharField(max_length=250)
    # Has RSVPed. This tracks if the information about the event has been received by the farmer and he has agreed to come to the event. 
    has_rsvped = models.BooleanField(default=False)
    # Did attend maintains the attendance
    did_attend = models.BooleanField(default=False)
    # Is redeemed will check if the token has been redeemed for some incentive 
    # (the incentive is not decided yet)
    is_redeemed = models.BooleanField(default=False)
    # The creation of the token
    created_at = models.DateTimeField(auto_now_add=True)
    # The event for which the token was created
    meeting = models.ForeignKey(Meetings, on_delete=models.CASCADE)
    # The farmer for which the token was created
    farmer = models.ForeignKey(Farmer, on_delete=models.CASCADE)