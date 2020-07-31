from django.shortcuts import (get_object_or_404, 
                              render, 
                              HttpResponseRedirect,redirect)
from django.http import HttpResponse, JsonResponse
from django.contrib.auth.decorators import login_required
from django.contrib.auth import login, logout, authenticate
from django.contrib.auth.forms import UserCreationForm, AuthenticationForm 
from django.contrib import messages
from .forms import *
#from .models import *
from farmer.models import *
# Changes
import random
import datetime
import faker 
import json
f = faker.Faker()
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.db.models import Count, Q
from .sms import send_message as Send_Text_Message
from .get_production_prediction import predict_production

# Create your views here.

posts = [
    {
        'author': 'CoreyMS',
        'created_at': 'August 27, 2018',
        'title': 'Blog Post 1',
        'synopsis': 'First post synopsis',
        'date_of_event': 'August 27, 2018'
    },
    {
        'author': 'Jane Doe',
        'title': 'Blog Post 2',
        'created_at': 'August 27, 2018',
        'synopsis': 'Second post synopsis',
        'date_of_event': 'August 28, 2018'
    }

]



def farmer_profile(request):
    return render(request, "fpo/farmer_profile.html") 

def member_page(request):
    context = {
        'farmers' : Farmer.objects.all()
        # 'leaders' :
    }
    return render(request, "fpo/member_page.html",context) 

def logout_request(request):
    logout(request)
    messages.info(request, "Logged out successfully!")
    return redirect("/fpo/index")


def login_request(request):
    if request.method == 'POST':
        form = AuthenticationForm(request=request, data=request.POST)
        if form.is_valid():
            username = form.cleaned_data.get('username')
            password = form.cleaned_data.get('password')
            user = authenticate(username=username, password=password)
            if user is not None:
                login(request, user)

                return redirect('/fpo/')
            else:
                messages.error(request, "Invalid username or password.")
        else:
            messages.error(request, "Invalid username or password.")
    form = AuthenticationForm()
    return render(request = request,
                    template_name = "fpo/index.html",
                    context={"form":form})
# def login_request(request):
#     form = AuthenticationForm()
#     return render(request,"fpo/login.html")

# ...................................................................................................................
@login_required
def delete_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Event, id = id) 
  
  
    if request.method =="POST": 
        # delete object 
        obj.delete() 
        # after deleting redirect to  
        # home page 
        return redirect("/fpo/member_page/")
  
    return render(request, "fpo/delete_farmer.html", context) 



@login_required
def update_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Event, id = id) 
  
    # pass the object as instance in form 
    form = EventForm(request.POST or None, instance = obj) 
  
    # save the data from the form and 
    # redirect to detail_farmer 
    if form.is_valid(): 
        form.save() 
        return redirect("/fpo/member_page/"+id)
  
    # add form dictionary to context 
    context["form"] = form 
  
    return render(request, "fpo/update_farmer.html", context)


@login_required
def detail_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # add the dictionary during initialization 
    context["data"] = Farmer.objects.get(id = id) 
          
    return render(request, "fpo/farmer_profile.html", context) 

@login_required
def add_farmer(request): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # add the dictionary during initialization 
    form = FarmerForm(data = request.POST , files=request.FILES) 
    if form.is_valid(): 
        form.save() 
        return redirect('/fpo/member_page')
          
    context['form']= form 
    return render(request, 'fpo/add_farmer.html', context) 


#..............................................................................................................







def error_404_view(request, exception):
    return render(request, 'fpo/404.html')
def error_500_view(request):
    return render('fpo/500.html')


        # Right now it will pass only one singular meeting object for all cases. 
##### POPULATE DATABASE
def populate_farmers(request):

    for _ in range(0):
        village = random.choice(['Konkan', 'Pune', 'Nashik', 'Aurangabad', 'Amravati'])

        farmer = Farmer(first_name=f.first_name(), last_name=f.last_name(), aadhar=str(f.random_number(12)), contact=str(f.random_number(10)), village=village, district='Mumbai', pin=str(f.random_number(6)))

        farmer.save()
    
    return HttpResponse('Thanks')



#### Changes made to this function

# This method will mark all the tokens as rsvped
def mark_rsvp(request):
    if request.method == 'POST':
        token_ids = json.loads(request.body)["token_ids"]
        for token_id in token_ids:
            token = MeetingToken.objects.get(id=token_id)
            token.has_rsvped = True
            token.save()
        return JsonResponse({"message": "The tokens have been RSVPed"})

# This method will mark all the tokens as attended
def mark_attendance(request):
    if request.method == 'POST':
        token_ids = json.loads(request.body)["token_ids"]
        for token_id in token_ids:
            token = MeetingToken.objects.get(id=token_id)
            token.did_attend = True
            token.save()
        farmers = MeetingToken.objects.values('farmer').annotate(total=Count('farmer'))
        for farmer in farmers:
            farmer_id = farmer['farmer']
            farmer_object = Farmer.objects.get(id=farmer_id)
            attended_meetings = MeetingToken.objects.filter(farmer=farmer_object, did_attend=True)
            engagement_score = 0 if farmer['total'] == 0 else round(len(attended_meetings)/farmer['total'],2)*100
            farmer_object.engagement = engagement_score
            farmer_object.save()
        return JsonResponse({"message": "The tokens have been marked as attended"})

# This method will mark all the tokens as attended
def mark_redeemed(request):
    if request.method == 'POST':
        farmer_ids = json.loads(request.body)["token_ids"]
        for farmer_id in farmer_ids:
            farmer = Farmer.objects.get(id=farmer_id)
            tokens = MeetingToken.objects.all().filter(farmer=farmer)
            for token in tokens:
                token.is_redeemed = True
                token.save()
        return JsonResponse({"message": "The tokens have been marked as redeemed"})

def redeem(request):
    meeting_tokens = MeetingToken.objects.all()
    Phone_Type = ['Smartphone', 'Featurephone', 'NoSmartphone']

    farmers = Farmer.objects.all()
    farmers_and_tokens = []
    for farmer in farmers:
        num_tokens = len(MeetingToken.objects.all().filter(farmer=farmer, is_redeemed=False, did_attend=True))
        if num_tokens > 0:
            farmers_and_tokens.append({
                "id": farmer.id,
                "farmer": f"{farmer.first_name} {farmer.last_name}",
                "locality": farmer.village,
                "number": num_tokens
            })
    farmers_and_tokens = sorted(farmers_and_tokens, key=lambda x: x['farmer'])
    
    return render(request, 'fpo/redeem_page.html', {'farmers': farmers_and_tokens})

@login_required
# # pass id attribute from urls 
def detail_meetings(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 

    context["data"] = Meetings.objects.get(id=id) # getting the specific meeting

    meeting_tokens = MeetingToken.objects.all().filter(meeting=context['data'])
    Phone_Type = ['Smartphone', 'Featurephone', 'NoSmartphone']
        
    tokens = []
    
    for t in meeting_tokens:
        leader = Leader.objects.filter(farmers=t.farmer.user)

        communication_type = t.farmer.user.category
        if communication_type == 'F':
            communication_type = Phone_Type[0]
        elif communication_type == 'P':
            communication_type = Phone_Type[1]
        else:
            communication_type = Phone_Type[2]
        
        if leader:
            leader_name = f'{leader[0].first_name} {leader[0].last_name}'
            has_leader = True
        else:
            leader_name = ''
            has_leader = False
        tokens.append({
            "id": t.id,
            "farmer": f'{t.farmer.first_name} {t.farmer.last_name}',
            "has_rsvped": t.has_rsvped,
            "did_attend": t.did_attend,
            "leader": leader_name,
            "has_leader": has_leader,
            "engagement": t.farmer.engagement,
            "locality": t.farmer.village,
            "smartphone_type": communication_type
        })
    
    leader_engagement = {}
    village_engagement = {}

    for token in tokens:
        if token['has_leader']:
            if token['leader'] in leader_engagement.keys():
                leader_engagement[token['leader']].append(token['engagement'])
            else:
                leader_engagement[token['leader']] = [token['engagement']]
        else:
            if token['locality'] in village_engagement.keys():
                village_engagement[token['locality']].append(token['engagement'])
            else:
                village_engagement[token['locality']] = [token['engagement']]

    TOTAL_TOKENS = len(tokens)
    

    normal_farmers = []
    farmers_by_leaders = []

    for token in tokens:
        if token['has_leader']:
            placed = False
            for leader in farmers_by_leaders:
                if leader['leader_name'] == token['leader']:
                    leader['farmers'].append(token)
                    leader['farmers'] = sorted(leader['farmers'], key=lambda x: x['farmer'])
                    placed = True
            if not placed:
                engagement_score = round(sum(leader_engagement[token['leader']])/len(leader_engagement[token['leader']]), 2)
                farmers_by_leaders.append({
                    "leader_id": random.randint(10000, 99999), 
                    "leader_name": token['leader'], 
                    "engagement": engagement_score,
                    "farmers": [token],
                })
                
        else:
            normal_farmers.append(token)
    
    
    farmers_by_locality = []
    for farmer in normal_farmers:
        placed = False
        for locality in farmers_by_locality:
            if locality['locality_name'] == farmer['locality']:
                locality['farmers'].append(farmer)
                locality['farmers'] = sorted(locality['farmers'], key=lambda x: x['farmer'])
                placed = True
        if not placed:
            engagement_score = round(sum(village_engagement[farmer['locality']])/len(village_engagement[farmer['locality']]), 2)
            farmers_by_locality.append({
                "locality_id": random.randint(10000, 99999), 
                "locality_name": farmer['locality'], 
                "engagement": engagement_score,
                "farmers": [farmer],
            })

    # There are three cases for the event: Before, Active and After
    # Before the event we show the RSVP
    # Active - Here we have the ability to mark the attendance
    # After - Here we can have the statistics of the event

    today_date = datetime.datetime.now().date()
    meeting_date = context['data'].date
    

    if(today_date > meeting_date):
        # This if form rendering the page of an event that is done
        # So only for the statistics
        context['present'] = sum(x['did_attend'] for x in tokens)
        context['absent'] = TOTAL_TOKENS - context['present']
        context['rsvp'] = sum(x['has_rsvped'] for x in tokens)
        context['no_rsvp'] = TOTAL_TOKENS - context['rsvp']

        context['villages'] = {
            "names": [x['locality_name'] for x in farmers_by_locality],
            "engagements": [round((sum(y['did_attend'] for y in  x['farmers'])/len(x['farmers'])), 2)*100 for x in farmers_by_locality],
            "rsvp": [round((sum(y['has_rsvped'] for y in  x['farmers'])/len(x['farmers'])), 2)*100 for x in farmers_by_locality]
        }

        context['leaders'] = {
            "names": [x['leader_name'] for x in farmers_by_leaders],
            "engagements": [round((sum(y['did_attend'] for y in  x['farmers'])/len(x['farmers'])), 2)*100 for x in farmers_by_leaders],
            "rsvp": [round((sum(y['has_rsvped'] for y in  x['farmers'])/len(x['farmers'])), 2)*100 for x in farmers_by_leaders]
        }

        meeting = context['data']

        context['average'] = round(context['present']/(context['present']+context['absent']), 2)*100

        return render(request, 'fpo/meeting_stats.html', context)        
    elif (today_date == meeting_date):
        # This is for rendering the active event page
        # Getting all the tokens
        tokens = list(filter(lambda x: not x["did_attend"], sorted(tokens, key=lambda x: x['farmer'])))

        context['farmers'] = tokens

        # Get the total number of attended tokens
        context['present'] = TOTAL_TOKENS - len(tokens)

        context['absent'] = len(tokens)

        return render(request, 'fpo/active_meeting.html', context) 
  
    else:
        ###### Before an Event
        normal_farmers = []
        farmers_by_leaders = []

        for token in tokens:
            if not token['has_rsvped']:
                if token['has_leader']:
                    placed = False
                    for leader in farmers_by_leaders:
                        if leader['leader_name'] == token['leader']:
                            leader['farmers'].append(token)
                            leader['farmers'] = sorted(leader['farmers'], key=lambda x: x['farmer'])
                            placed = True
                    if not placed:
                        engagement_score = round(sum(leader_engagement[token['leader']])/len(leader_engagement[token['leader']]), 2)
                        farmers_by_leaders.append({
                            "leader_id": random.randint(10000, 99999), 
                            "leader_name": token['leader'], 
                            "engagement": engagement_score,
                            "farmers": [token],
                        })
                        
                else:
                    normal_farmers.append(token)

        farmers_by_locality = []
        for farmer in normal_farmers:
            placed = False
            for locality in farmers_by_locality:
                if locality['locality_name'] == farmer['locality']:
                    locality['farmers'].append(farmer)
                    locality['farmers'] = sorted(locality['farmers'], key=lambda x: x['farmer'])
                    placed = True
            if not placed:
                engagement_score = round(sum(village_engagement[farmer['locality']])/len(village_engagement[farmer['locality']]), 2)
                farmers_by_locality.append({
                    "locality_id": random.randint(10000, 99999), 
                    "locality_name": farmer['locality'], 
                    "engagement": engagement_score,
                    "farmers": [farmer],
                })

        temp_farmers_by_locality = []
        special_attention_villages = []
        for locality in farmers_by_locality:
            if locality['engagement'] < 20 and len(special_attention_villages) <= 5:
                special_attention_villages.append(locality)
            else:
                temp_farmers_by_locality.append(locality)
        
        farmers_by_locality = temp_farmers_by_locality
        special_attention_villages = sorted(special_attention_villages, key=lambda x: len(x['farmers']), reverse=True)
        # add the dictionary during initialization
        # Right now it will pass only one singular meeting object for all cases. 

        context['leaders'] = sorted(farmers_by_leaders, key=lambda x: x['leader_name'])

        context['villages'] = sorted(farmers_by_locality, key=lambda x: x['locality_name'])

        context['specials'] = special_attention_villages

        context['reached'] = sum(x['has_rsvped'] for x in tokens)

        context['no_response'] = len(tokens) - context['reached']

        return render(request, "fpo/meeting.html", context) 







@login_required
def home(request):
    context = {
        'posts' : Meetings.objects.all()
    }
    return render(request, 'fpo/home.html',context)
#..........................................................................................................


@login_required
def meetings_view(request):
    # dictionary for initial data with  
    # field names as keys 
    context ={} 

    meetings = Meetings.objects.order_by('date')

    # add the dictionary during initialization      # Add and view both
    form = MeetingsForm(request.POST or None) 
    if form.is_valid(): 
        meeting = form.save() 
        # Creating the tokens for meetings
        farmers = Farmer.objects.all()
        for farmer in farmers:
            token = MeetingToken(token_number=random.randint(100000,999999), meeting=meeting, farmer=farmer)
            token.save()
        return redirect('/fpo/meetings')

    # Code for pagination
    paginator = Paginator(meetings, 2)
    page = request.GET.get('page')

    try:
        meetings = paginator.page(page)
    except PageNotAnInteger:
        meetings = paginator.page(1)
    except EmptyPage:
        meetings = paginator.page(paginator.num_pages)

        
    context['page'] = paginator.get_page(page)
    
    context['govt'] = meetings

    context['form']= form 
    return render(request, 'fpo/meetings.html',context)


@login_required
def meetings_delete(request, id):
    # dictionary for initial data with  
    # field names as keys
    context = {}


    obj = get_object_or_404(Meetings, id=id)
    obj.delete()
    return redirect("/fpo/meetings")

@login_required	
def meetings_update(request, id):
    # dictionary for initial data with  
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Meetings, id=id)

    # pass the object as instance in form
    form = MeetingsForm(request.POST or None, instance=obj)

    # save the data from the form and
    # redirect to detail_farmer
    if form.is_valid():
        form.save()
        return redirect("/fpo/meetings/")

    # add form dictionary to context
    context["form"] = form

    return render(request, "fpo/update_meeting.html", context)


#..................................................................................................................


@login_required
def govtschemes_view(request):
    
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    schemes = Govt.objects.all()

    # add the dictionary during initialization 
    form = GovtForm(request.POST or None) 
    if form.is_valid(): 
        form.save() 
        return redirect('/fpo/govtschemes')
          
    # Code for pagination
    paginator = Paginator(schemes, 4)
    page = request.GET.get('page')

    try:
        schemes = paginator.page(page)
    except PageNotAnInteger:
        schemes = paginator.page(1)
    except EmptyPage:
        schemes = paginator.page(paginator.num_pages)

        
    context['page'] = paginator.get_page(page)
    
    context['govt'] = schemes


    context['form']= form 
    return render(request, 'fpo/govtschemes.html',context)

@login_required
def govtschemes_delete(request, id):
    # dictionary for initial data with  
    # field names as keys 
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Govt, id=id)
    obj.delete()

    # if request.method =="POST":
    #     # delete object
    #
    #     # after deleting redirect to
    #     # home page
    #     return redirect("/fpo/govtschemes")
    # # else :
    # #     return redirect("/fpo/govtschemes")

    return redirect("/fpo/govtschemes")

@login_required
def govtschemes_update(request, id):
    # dictionary for initial data with  
    # field names as keys 
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Govt, id=id)

    # pass the object as instance in form
    form = GovtForm(request.POST or None, instance=obj)

    # save the data from the form and
    # redirect to detail_farmer
    if form.is_valid():
        form.save()
        return redirect("/fpo/govtschemes/")

    # add form dictionary to context
    context["form"] = form

    return render(request, "fpo/update_govt.html", context)

def govtschemes_single_add(request, id):
    if request.method == 'POST':
        farmer_ids = json.loads(request.body)["farmer_ids"]
        govt_scheme = Govt.objects.get(id=id)
        for farmer_id in farmer_ids:
            farmer = Farmer.objects.get(id=farmer_id)
            farmer.govt_schemes.add(govt_scheme)
        return JsonResponse({"message": "The farmers have been signed up for the government scheme"})


##### Changes
def govtschemes_single(request, id):
    context = {}
    
    scheme = Govt.objects.get(id=id)

    Phone_Type = ['Smartphone', 'Featurephone', 'NoSmartphone']

    villages = Farmer.objects.values('village').annotate(total=Count('village'))
    villages_total = {x['village']:x['total'] for x in villages}

    #Condition: filter(~Q(govt_schemes=scheme))
    farmers = Farmer.objects.all()
    context['data'] = scheme

    farmers_by_scheme = []
    
    for farmer in farmers:
        communication_type = farmer.user.category
        if communication_type == 'F':
            communication_type = Phone_Type[0]
        elif communication_type == 'P':
            communication_type = Phone_Type[1]
        else:
            communication_type = Phone_Type[2]

        farmers_by_scheme.append({
            "id": farmer.id,
            "farmer": f'{farmer.first_name} {farmer.last_name}',
            "locality": farmer.village,
            "smartphone_type": communication_type,
            "schemes": farmer.govt_schemes.all()
        })
    
    farmers_by_locality = []
    for farmer in farmers_by_scheme:
        placed = False
        for locality in farmers_by_locality:
            if locality['locality_name'] == farmer['locality']:
                locality['farmers'].append(farmer)
                print('Sum')
                print(sum(scheme in x['schemes'] for x in locality['farmers']))
                print()
                locality['engagement'] = round(sum(scheme in x['schemes'] for x in locality['farmers'])/villages_total[farmer['locality']], 2)*100
                locality['farmers'] = sorted(locality['farmers'], key=lambda x: x['farmer'])
                placed = True
        if not placed:
            farmers_by_locality.append({
                "locality_id": random.randint(10000, 99999), 
                "locality_name": farmer['locality'], 
                "engagement": (round([1 if scheme in farmer['schemes'] else 0][0]/villages_total[farmer['locality']], 2))*100,
                "farmers": [farmer],
            })

    temp_farmers_by_locality = []
    special_attention_villages = []
    for locality in farmers_by_locality:
        if locality['engagement'] < 20 and len(special_attention_villages) <= 5:
            special_attention_villages.append(locality)
        else:
            temp_farmers_by_locality.append(locality)
    
    farmers_by_locality = temp_farmers_by_locality
    special_attention_villages = sorted(special_attention_villages, key=lambda x: len(x['farmers']), reverse=True)

    special_attention_villages = sorted(special_attention_villages, key=lambda x: x['locality_name'])
    farmers_by_locality = sorted(farmers_by_locality, key=lambda x: x['locality_name'])

    for locality in farmers_by_locality:
        locality['farmers'] = list(filter(lambda x: scheme not in x['schemes'], locality['farmers']))

    for locality in special_attention_villages:
        locality['farmers'] = list(filter(lambda x: scheme not in x['schemes'], locality['farmers']))

    village_data = [(x['locality_name'], x['engagement']) for x in farmers_by_locality] + [(x['locality_name'], x['engagement']) for x in special_attention_villages]
    
    village_names = []
    village_total = []
    
    for village, total in village_data:
        village_names.append(village)
        village_total.append(total)

    context['village_names'] = village_names
    context['village_total'] = village_total
    context['village_population'] = villages_total


    special_attention_villages = list(filter(lambda x: len(x['farmers'])>0, special_attention_villages))
    farmers_by_locality = list(filter(lambda x: len(x['farmers'])>0, farmers_by_locality))


    context['specials'] = special_attention_villages
    context['villages'] = farmers_by_locality


    return render(request, 'fpo/govt_scheme.html', context)


#---------------------------------------------------------------------------------------------------------
def fpo_statistics(request):
    context = {}
    
    # Get all the farmers
    farmers = Farmer.objects.all()
    
    # Basic numbers
    num_farmers = len(farmers)
    num_villages = len(farmers.values('village').distinct())
    avg_production_every_year = 3000
    avg_profits_every_year = 10

    context['num_farmers'] = num_farmers
    context['num_villages'] = num_villages
    context['avg_production'] = avg_production_every_year
    context['avg_profits'] = avg_profits_every_year


    # Communication Channels
    communication_channels = UserProfile.objects.values('category').annotate(total=Count('category'))
    communication_channels_data = [x['total'] for x in communication_channels if x['category'] not in ['A','L']]
    communication_channels_labels = [x['category'] for x in communication_channels if x['category'] not in ['A','L']]
    context['communication_channels_data'] = communication_channels_data if len(communication_channels_data) == 3 else communication_channels_data + [0 for x in range(3-len(communication_channels_data))]

    for i in range(len(communication_channels_labels)):
        if communication_channels_labels[i] == 'F':
            communication_channels_labels[i] = 'Farmers with Smartphones'
        elif communication_channels_labels[i] == 'P':
            communication_channels_labels[i] = 'Farmers with Feature Phones'
        elif communication_channels_labels[i] == 'N':
            communication_channels_labels[i] = 'Farmers with No Phones'

    context['communication_channels_labels'] = communication_channels_labels 
    
    # Farmers by Leaders
    leaders = Leader.objects.all()
    farmers_with_leaders = sum([len(x.farmers.all()) for x in leaders])
    farmers_without_leaders = len(farmers) - farmers_with_leaders
    context['farmers_with_leaders_data'] = [farmers_with_leaders, farmers_without_leaders]



    # Farmer Engagement 

    SP_by_months = {
        'label': 'Farmers with Smartphones',
        'data':[]
    }

    FP_by_months = {
        'label': 'Farmers with Feature Phones',
        'data':[]
    }

    NP_by_months = {
        'label': 'Farmers with No Phones',
        'data':[]
    }

    for month in range(1, 13):
        meetings = Meetings.objects.filter(date__month=month)
        meetings_tokens = []
        for meeting in meetings:
            meetings_tokens.extend(MeetingToken.objects.filter(meeting=meeting))
        
        SP = []
        FP = []
        NP = []
        
        for token in meetings_tokens:
            if token.farmer.user.category == 'F':
                SP.append(token)
            elif token.farmer.user.category == 'P':
                FP.append(token)
            else:
                NP.append(token)
        
        SP_engagement = round(sum(x.did_attend for  x in SP) / len(SP), 2)*100 if SP else 0.0
        FP_engagement = round(sum(x.did_attend for  x in FP) / len(FP), 2)*100 if FP else 0.0
        NP_engagement = round(sum(x.did_attend for  x in NP) / len(NP), 2)*100 if NP else 0.0

        SP_by_months['data'].append(SP_engagement)
        FP_by_months['data'].append(FP_engagement)
        NP_by_months['data'].append(NP_engagement)

    farmer_engagement_data = [SP_by_months, FP_by_months, NP_by_months]
    
    context['farmer_engagement_data'] = farmer_engagement_data



    # Villages
    villages = farmers.values('village').annotate(total=Count('village'))
    villages_data = [x['total'] for x in villages]
    villages_labels = [x['village'] for x in villages]
    context['villages_data'] = villages_data
    context['villages_labels'] = villages_labels


    # Crops
    context['crops_data'] = [1570, 1200, 1500, 580, 1900, 1650, 100]
    context['crops_labels'] = ['Rice', 'Wheat', 'Corn', 'Sugarcane', 'Bajra', 'Paddy', 'Maize']


    # Crop Production By Years
    crops_by_years_data = [
    {
        'name': 'Rice',
        'data': [100, 200, 300, 400, 500],
        'years': ['2015', '2016', '2017', '2018', '2019']
    },
    {
        'name': 'Wheat',
        'data': [200, 300, 400, 500, 600],
        'years': ['2015', '2016', '2017', '2018', '2019']
    },
    {
        'name': 'Bajra',
        'data': [400, 300, 500, 600, 900],
        'years': ['2015', '2016', '2017', '2018', '2019']
    }
    ]

    for crop in crops_by_years_data:
        data = {'Production': crop['data'], 'Year': [int(x) for x in crop['years']]}
        prediction = predict_production(data)
        crop['years'].append(f'{prediction[0]} (Prediction)')
        crop['data'].append(round(prediction[1], 2))

    context['crop_selector_options'] = [x['name'] for x in crops_by_years_data]

    context['crops_by_years_data'] = crops_by_years_data


    # Profits Per Crop
    crops_profits_by_years_data = [
    {
        'name': 'Corn',
        'data': [80, 60, 100, 150, 300],
        'years': ['2015', '2016', '2017', '2018', '2019']
    },
    {
        'name': 'Sugarcane',
        'data': [60, 180, 120, 150, 130],
        'years': ['2015', '2016', '2017', '2018', '2019']
    },
    {
        'name': 'Paddy',
        'data': [120, 300, 180, 80, 250],
        'years': ['2015', '2016', '2017', '2018', '2019']
    }
    ]

    for crop in crops_profits_by_years_data:
        data = {'Production': crop['data'], 'Year': [int(x) for x in crop['years']]}
        prediction = predict_production(data)
        crop['years'].append(f'{prediction[0]} (Prediction)')
        crop['data'].append(round(prediction[1], 2))

    context['crop_price_selector_options'] = [x['name'] for x in crops_profits_by_years_data]

    context['crops_profits_by_years_data'] = crops_profits_by_years_data


    # Profits By Year
    profit_by_years_data = [
    {
        'year': '2017',
        'data': [100, 200, 300, 400, 500, 500, 100, 200, 300, 400, 900, 200],
    },
    {
        'year': '2018',
        'data': [200, 300, 400, 500, 600, 400, 500, 500, 100, 200, 300, 400],
    },
    {
        'year': '2019',
        'data': [400, 300, 500, 600, 900, 100, 200, 300, 400, 500, 200, 100],
    }
    ]

    context['profit_by_years_data'] = profit_by_years_data


    return render(request, 'fpo/fpo_statistics.html', context)









#...................................................................................................................


#...................................................................................................................


@login_required
def plans_view(request):
    # dictionary for initial data with  
    # field names as keys 
    context ={
        'crop' : Crops.objects.all()
    }

    products = Products.objects.all() 
    context['products'] = products
  
    # add the dictionary during initialization      # Add and view both
    form = CropsForm(request.POST or None, request.FILES)
    if form.is_valid(): 
        print(request.POST)
        print(request.FILES)
        form.save() 
        return redirect('/fpo/plans')
          
    context['form']= form 
    return render(request, 'fpo/plans.html',context)


@login_required
def plans_delete(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Crops, id = id) 
  
  
    if request.method =="POST": 
        # delete object 
        obj.delete() 
        # after deleting redirect to  
        # home page 
        return redirect("/fpo/plans")
  
    return render(request, "fpo/plans_delete.html", context)
@login_required
def plans_toggle(request,id):
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Crops, id = id) 
  
  
    obj.live^=True
    obj.save()

    return redirect("/fpo/plans")

@login_required
def plans_detail(request, id):
    plans = get_object_or_404(Crops,id=id)

    return render(request, "fpo/plan_detail.html",context={'plans': plans})
	
@login_required	
def plans_update(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
    # fetch the object related to passed id 
    obj = get_object_or_404(Crops, id = id) 
  
    # pass the object as instance in form 
    form = CropsForm(request.POST or None, request.FILES or None, instance = obj) 

    products = Products.objects.all() 
    context['products'] = products
    context['selected_products'] = obj.products.all()


    # save the data from the form and 
    # redirect to detail_farmer 
    if form.is_valid(): 
        form.save() 
        return redirect("/fpo/plans/")
  
    # add form dictionary to context 
    context["form"] = form 
  
    return render(request, "fpo/update_plans.html", context)

# api data for plans............................................................................. 
def data_village_count(request, *args, **kwargs):
    
    data={}
    
    farmers = FarmerCropMap.objects.all()
    for farmer in farmers:
        obj = get_object_or_404(Farmer, user=farmer.farmer)#, category='F' and or 'P' or 'N')
        print(obj.village)
        if obj.village in data:
            data[obj.village]+=1
        else:
            data[obj.village]=1
    
    return JsonResponse(data)
def data_village_quantity(request, *args, **kwargs):
    
    data={}
    
    farmers = FarmerCropMap.objects.all()
    for farmer in farmers:
        obj = get_object_or_404(Farmer, user=farmer.farmer)#, category='F' and or 'P' or 'N')
        print(obj.village)
        crop1 = farmer.crop
        obj1 = get_object_or_404(Crops, id=crop1.id)
        print(obj1.weigth_per_land)
        weight = obj1.weigth_per_land
        # print(farmer.crop.weight_per_land)
        if obj.village in data:
            data[obj.village]+=(farmer.landarea)*weight
        else:
            data[obj.village]=(farmer.landarea)*weight
    
    return JsonResponse(data)    

#..........................................................
#add products in a plan 

def plan_del_product(request, id1, id2):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # add the dictionary during initialization
    # user
    # context["leader"] = Leader2.objects.get(user_id = id).all()
    obj = get_object_or_404(Crops, id=id1)
    sub = get_object_or_404(obj.products, id=id2)
    # sub = Leader.objects.filter(user_id = id1, farmers_id = id2)

    obj.products.remove(sub)
    if (obj.products.count() >= 0):
        print(obj.products.all())
    return redirect('plans-detail',id=id1)

    
    return render(request, "fpo/plans.html", context)

def plan_add_product(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    form = plan_add_productForm(request.POST or None, files=request.FILES)
    if form.is_valid():
        # form.save()
        context["data"] = form
        context["form"] = form

        ID = form.cleaned_data['ID']
        # print(ID)
        # q1 = Q(category='F')
        # q2 = Q(category='P')
        # q3 = Q(category='N')
        # q4 = Q(ID=ID)
        # obj = UserProfile.objects.filter((q1 | q2 | q3) & q4)   
        obj = Products.objects.filter( id=ID)
        print(obj.count())

        
        if obj.count() < 1:
            return redirect('/fpo/plans/detail/'+id)
        
        # print(obj[0].username)
    
        
        obj2 = get_object_or_404(Products, id=obj.first().id)   
        print(obj2)
        sub = get_object_or_404(Crops, id=id)
        sub.products.add(obj2)
        sub.save()
        if (sub.products.count() > 0):
            for far in sub.products.all():
                print(far)
            # print(sub.farmers.all())
            # return redirect('detail_leader',id=id)
        return redirect('/fpo/plans/detail/'+id)

    context['form'] = form
    context['crops'] = Crops.objects.all()
    return render(request, "fpo/add.html", context)  # change according to tempalate

#.............................................................................................

@login_required
def products_view(request):
    # dictionary for initial data with
    # field names as keys
    context = {
        'products': Products.objects.all()
    }

    # add the dictionary during initialization
    form = ProductsForm(request.POST or None, files=request.FILES)
    if form.is_valid():
        form.save()
        return redirect('/fpo/products')

    context['form'] = form

    return render(request, 'fpo/products.html', context)


@login_required
def products_delete(request, id):

    context = {}


    obj = get_object_or_404(Products, id=id)
    obj.delete()


    return redirect("/fpo/products")


@login_required
def products_update(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Products, id=id)

    # pass the object as instance in form
    form = ProductsForm(request.POST or None, instance=obj)#, request.FILES

    if form.is_valid():
        form.save()
        return redirect("/fpo/products/")

    # add form dictionary to context
    context["form"] = form

    return render(request, "fpo/update_products.html", context) #update_products

@login_required
def products_detail(request, id):
    products = get_object_or_404(Products,id=id)

    return render(request, "fpo/product_detail.html",context={'products': products})


#Orders
@login_required
def orders_view(request):
    # dictionary for initial data with
    # field names as keys
    context = {
        'orders': Orders.objects.all()
    }

    return render(request, 'fpo/orders.html', context)

@login_required
def orders_delivered_toggle(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Orders, id=id)

    obj.is_delivered ^= True
    obj.save()

    return render(request, "fpo/orders.html", context)

@login_required
def orders_paid_toggle(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Orders, id=id)

    obj.is_paid ^= True
    obj.save()

    return render(request, "fpo/orders.html", context)

@login_required
def orders_delete(request, id):
    obj = get_object_or_404(Meetings, id=id)
    obj.delete()
    return redirect("/fpo/orders")



@login_required
def about(request):
    return HttpResponse('<h1> About </h1>')


def send_message(request):
    if request.method == 'POST':
        message_body = json.loads(request.body)["message_body"]
        farmers = Farmer.objects.all()
        for farmer in farmers:
            if farmer.user.category != 'N':
                Send_Text_Message(f'+91{farmer.contact}', message_body=message_body)
        return JsonResponse({"message": "The message has been broadcast"})