from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager
from django.conf import settings
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token


# class MyUserProfileManager(BaseUserManager):
#     def create_user(self, username, category='F', password=None):
#         if not username:
#             raise ValueError("Username required")
#         if not category:
#             raise ValueError("Category required")

#         user = self.model(username=username, category=category)

#         user.set_password(password)
#         user.save(using=self._db)
#         return user

#     def create_superuser(self, username, category='A', password=None ):
#         user = self.create_user(username=username, category=category, password=password)

#         user.is_admin = True
#         user.is_staff = True
#         user.is_superuser = True
#         user.is_superviser = True
#         user.save(using=self._db)

#         return user


# class UserProfile(AbstractBaseUser):
#     username = models.CharField(max_length=50, unique=True)
#     date_joined = models.DateTimeField(verbose_name='date joined', auto_now_add=True)
#     last_login = models.DateTimeField(verbose_name='last login', auto_now=True)

#     is_admin = models.BooleanField(default=False)
#     is_active = models.BooleanField(default=True)
#     is_staff = models.BooleanField(default=False)
#     is_superviser = models.BooleanField(default=False)

#     CATEGORIES = (
#         ('A', 'FPO Admin'),
#         ('L', 'Leader'),
#         ('F', 'Farmer'),
#     )

#     category = models.CharField(max_length=10, choices=CATEGORIES)

#     USERNAME_FIELD = 'username'
#     REQUIRED_FIELDS = ['category', ]

#     objects = MyUserProfileManager()

#     def __str__(self):
#         return self.username

#     def has_perm(self, perm, obj=None):
#         return self.is_admin

#     def has_module_perms(self, app_label):
#         return True


# @receiver(post_save, sender=settings.AUTH_USER_MODEL)
# def create_auth_token(sender, instance=None, created=False, **kwargs):
#     if created:
#         Token.objects.create(user=instance)




# class Leader(models.Model):
#     user = models.OneToOneField(UserProfile, on_delete=models.CASCADE, null=True)
#     first_name = models.CharField(max_length=50)
#     last_name = models.CharField(max_length=50)
#     aadhar = models.CharField(max_length=12, null=True, blank=True)
#     contact = models.CharField(max_length=10, null=True)
#     village = models.CharField(max_length=100, null=True)
#     district = models.CharField(max_length=100, null=True)
#     pin = models.IntegerField(null=True)
#     photo = models.ImageField(upload_to='images/profile_photos/', null=True, blank=True)
#     profession = models.CharField(max_length=100, null=True, blank=True)

#     def __str__(self):
#         return self.user.username + '-' + self.first_name


# class Farmer(models.Model):
#     user = models.OneToOneField(UserProfile, on_delete=models.CASCADE, null=True)
#     first_name = models.CharField(max_length=50, null=True)
#     last_name = models.CharField(max_length=50, null=True)
#     aadhar = models.CharField(max_length=12, null=True, blank=True)
#     contact = models.CharField(max_length=10, null=True)
#     village = models.CharField(max_length=100, null=True)
#     district = models.CharField(max_length=100, null=True)
#     pin = models.IntegerField(null=True)
#     photo = models.ImageField(upload_to='images/profile_photos/', null=True, blank=True)

#     def __str__(self):
#         return self.user.username + '-' +  self.first_name

# class LeaderFarmerMap(models.Model):
#     leader = models.OneToOneField(Leader, on_delete=models.CASCADE)
#     farmer = models.OneToOneField(Farmer, on_delete=models.CASCADE)

#     def __str__(self):
#         return self.leader.user.username + '-' + self.farmer.user.username
