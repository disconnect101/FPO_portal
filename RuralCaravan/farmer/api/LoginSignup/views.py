from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.authtoken.serializers import AuthTokenSerializer
from farmer.api.serializers import RegistrationSerializer, FarmerSerializer, LeaderSerializer
from rest_framework.authtoken.models import Token
from farmer.SMSservice import sms
import random
from farmer.models import Contact
from django.utils import timezone
from datetime import datetime
from django.contrib.auth import login
from farmer.models import Farmer, UserProfile, Leader
from rest_framework.authtoken.views import ObtainAuthToken
import pytz
from farmer.api.utils import *

@api_view(['POST'],)
def register(request):
    if request.method=='POST':
        serializer = RegistrationSerializer(data=request.data)
        category = request.data.get('category')

        data = {}
        User = None
        ##User Registration code
        username = request.data.get('username')
        check_user = UserProfile.objects.filter(username=username)
        if check_user.count()>0 and check_user.first().category!='N':
            old_user = check_user.first()
            contact = Contact.objects.filter(user=old_user)
            if contact.count()>0 and contact.first().verification_status==False:
                try:
                    old_user.delete()
                except:
                    return Response(statuscode('26'))
            elif contact.count()==0:
                try:
                    old_user.delete()
                except:
                    return Response(statuscode('26'))

        if serializer.is_valid():
            User = serializer.save()
            data = {
                'response': 'Registration Successfull',
                'username': User.username,
                'category': User.category,
                'token': Token.objects.get(user=User).key,
            }
        else:
            data = serializer.errors
            data['statuscode'] = '7'
            return Response(data)

        ##OTP code
        phone_number = ''
        if category!='N':
            try:
                phone_number = request.data.get('contact')
            except:
                return Response({'statuscode' : '1'})
            if len(phone_number)!=10:
                return Response({'statuscode': '1'})
        else:
            return Response({
                'statuscode': '0',
                'token': Token.objects.get(user=User).key
            })

        otp = random.randint(100000, 999999)
        if Contact.objects.filter(number=phone_number).count()==0:
            Contact(user=User, number=phone_number, otp=otp).save()
        else:
            try:
                OTPinst = Contact.objects.get(number=phone_number)
                if OTPinst.verification_status:
                    return Response({
                        'statuscode': '8',
                    })
                OTPinst.otp = otp
                OTPinst.datetime = timezone.now()
                OTPinst.user = User
                OTPinst.save()
            except:
                #print("OTP could not be saved in DB")
                return Response({
                    'statuscode': '3',  #otp could not be saved in DB
                })

        message = "Your OTP is " + str(otp) + "."
        print(phone_number, sms.TWILIO_NUMBER, message)
        try:
            sms.send_message("+91" + phone_number, sms.TWILIO_NUMBER, message)
        except Exception as e:
            return Response({
                'statuscode': '2',  # otp couldn't be sent.
            })

        return Response({
            'statuscode': '0',
        })



@api_view(['GET', 'POST'],)
@permission_classes((IsAuthenticated, ))
def userData(request):
    user = request.user

    if request.method=='GET':
        if user.category=='L':
            try:
                leader = Leader.objects.get(user=user)
                print(leader.first_name)
            except Leader.DoesNotExist:
                return Response("Leader Does not exist")
            serializer = LeaderSerializer(leader)
        else:
            try:
                farmer = Farmer.objects.get(user=user)
                print(farmer.first_name)
            except Farmer.DoesNotExist:
                return Response(statuscode('farmer details not available'))
            serializer = FarmerSerializer(farmer)
            
        return Response(serializer.data)

    if request.method=='POST':
        serializer = FarmerSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save(user)
            return Response({
                'statuscode': '0'
            })
        else:
            return Response({
                'statuscode': '5'
            })


@api_view(['POST'],)
def verifyOTP(request):

    if request.method=='POST':
        phone_number = request.data.get('contact')
        otp = request.data.get('otp')
        if len(phone_number) != 10:
            return Response({
                'statuscode': '1',          #Invalid Phone Number.
            })
        try:
            OTPinst = Contact.objects.get(number=phone_number)
        except Exception as e:
            return Response({
                'statuscode': '5',          #Invalid Request
            })

        statuscode = ''
        if OTPinst.verification_status:
            return Response({
                'statuscode': '8'
            })

        if OTPinst.otp == otp:
            timedelta = datetime.now() - OTPinst.datetime
            print(OTPinst.datetime)
            hours = timedelta.seconds/3600
            print(hours)
            if hours <= 1:
                statuscode = '0'
                OTPinst.verification_status = True
                OTPinst.save()
            else:
                statuscode = '6'            #OTP expired
        else:
            statuscode = '4'                #OTP varification failed

        if statuscode=='0':
            return Response({
                'statuscode': statuscode,
                'token': Token.objects.get(user=OTPinst.user).key,
            })
        else:
            return Response({
                'statuscode': statuscode,
            })


class CustomAuthToken(ObtainAuthToken):

    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data, context={'request': request})

        user = None
        if serializer.is_valid():
            user = serializer.validated_data['user']
        else:
            return Response({'statuscode': '9'})

        try:
            verification_status = Contact.objects.get(user=user).verification_status
        except:
            verification_status = False
        if verification_status or user.category == 'N':
            token = Token.objects.get(user=user).key
        else:
            return Response({'statuscode': '10'})

        return Response({
            'statuscode': '0',
            'token': token,
            'category': user.category,
        })


@api_view(['GET'],)
@permission_classes((IsAuthenticated, ))
def getVillageInfo(request):
    data = {
        'data': [
            {
                'district1': ['village1', 'village2']
            },
            {
                'district2': ['village3', 'village4', 'village5']
            },
            {
                'district3': ['village6', 'village7']
            }
        ]
    }
    return Response(data)


