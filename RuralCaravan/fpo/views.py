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
from .generate_data import get_data
import random
import datetime
import faker 
import json
f = faker.Faker()
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.db.models import Count, Q


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
        leader = Leader.objects.filter(farmers=t.farmer)
        
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
            "locality": t.farmer.village,
            "smartphone_type": random.choice(Phone_Type)
        })
    
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
                farmers_by_leaders.append({
                    "leader_id": random.randint(10000, 99999), 
                    "leader_name": token['leader'], 
                    "engagement": int(round(random.uniform(0,1), 2)*100),
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
            farmers_by_locality.append({
                "locality_id": random.randint(10000, 99999), 
                "locality_name": farmer['locality'], 
                "engagement": int(round(random.uniform(0,1), 2)*100),
                "farmers": [farmer],
            })


    # There are three cases for the event: Before, Active and After
    # Before the event we show the RSVP
    # Active - Here we have the ability to mark the attendance
    # After - Here we can have the statistics of the event

    today_date = datetime.datetime.now().date()
    meeting_date = context['data'].date
    print(today_date, meeting_date)

    if(today_date > meeting_date):
        # This if form rendering the page of an event that is done
        # So only for the statistics
        context['present'] = sum(x['did_attend'] for x in tokens)
        context['absent'] = TOTAL_TOKENS - context['present']
        context['rsvp'] = sum(x['has_rsvped'] for x in tokens)
        context['no_rsvp'] = TOTAL_TOKENS - context['rsvp']

        context['villages'] = {
            "names": [x['locality_name'] for x in farmers_by_locality],
            "engagements": [(sum(y['did_attend'] for y in  x['farmers'])//len(x['farmers']))*100 for x in farmers_by_locality],
            "rsvp": [(sum(y['has_rsvped'] for y in  x['farmers'])//len(x['farmers']))*100 for x in farmers_by_locality]
        }

        context['leaders'] = {
            "names": [x['leader_name'] for x in farmers_by_leaders],
            "engagements": [(sum(y['did_attend'] for y in  x['farmers'])//len(x['farmers']))*100 for x in farmers_by_leaders],
            "rsvp": [(sum(y['has_rsvped'] for y in  x['farmers'])//len(x['farmers']))*100 for x in farmers_by_leaders]
        }

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
                        farmers_by_leaders.append({
                            "leader_id": random.randint(10000, 99999), 
                            "leader_name": token['leader'], 
                            "engagement": int(round(random.uniform(0,1), 2)*100),
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
                farmers_by_locality.append({
                    "locality_id": random.randint(10000, 99999), 
                    "locality_name": farmer['locality'], 
                    "engagement": int(round(random.uniform(0,1), 2)*100),
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

    farmers = Farmer.objects.filter(~Q(govt_schemes=scheme))
    context['data'] = scheme

    farmers_by_scheme = []
    
    for farmer in farmers:
        farmers_by_scheme.append({
            "id": farmer.id,
            "farmer": f'{farmer.first_name} {farmer.last_name}',
            "locality": farmer.village,
            "smartphone_type": random.choice(Phone_Type)
        })
    
    farmers_by_locality = []
    for farmer in farmers_by_scheme:
        placed = False
        for locality in farmers_by_locality:
            if locality['locality_name'] == farmer['locality']:
                locality['farmers'].append(farmer)
                locality['engagement'] = 100 - round(len(locality['farmers'])/villages_total[farmer['locality']], 2)*100
                locality['farmers'] = sorted(locality['farmers'], key=lambda x: x['farmer'])
                placed = True
        if not placed:
            farmers_by_locality.append({
                "locality_id": random.randint(10000, 99999), 
                "locality_name": farmer['locality'], 
                "engagement": 100 - (round(1/villages_total[farmer['locality']], 2))*100,
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

    context['specials'] = special_attention_villages
    context['villages'] = farmers_by_locality

    village_data = [(x['locality_name'], x['engagement']) for x in farmers_by_locality] + [(x['locality_name'], x['engagement']) for x in special_attention_villages]
    
    village_names = []
    village_total = []
    
    for village, total in village_data:
        village_names.append(village)
        village_total.append(total)

    context['village_names'] = village_names
    context['village_total'] = village_total
    context['village_population'] = villages_total
    return render(request, 'fpo/govt_scheme.html', context)


#---------------------------------------------------------------------------------------------------------
def fpo_statistics(request):
    return render(request, 'fpo/fpo_statistics.html')







#...................................................................................................................


#...................................................................................................................


@login_required
def plans_view(request):
    # dictionary for initial data with  
    # field names as keys 
    context ={
        'crop' : Crops.objects.all()
    } 
  
    # add the dictionary during initialization      # Add and view both
    form = CropsForm(request.POST or None) 
    if form.is_valid(): 
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
    form = CropsForm(request.POST or None, instance = obj) 
  
    # save the data from the form and 
    # redirect to detail_farmer 
    if form.is_valid(): 
        form.save() 
        return redirect("/fpo/plans/")
  
    # add form dictionary to context 
    context["form"] = form 
  
    return render(request, "fpo/update_plans.html", context)


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

