from django.shortcuts import (get_object_or_404, 
                              render, 
                              HttpResponseRedirect,redirect)
from django.http import HttpResponse
from django.contrib.auth.decorators import login_required
from django.contrib.auth import login, logout, authenticate
from django.contrib.auth.forms import UserCreationForm, AuthenticationForm 
from django.contrib import messages
from .forms import FarmerForm, leader_add_farmerForm, ProduceForm, FPOLedgerForm, TransactionForm, OrdersForm, LandForm, \
    BankForm
from .forms import LeaderForm
from .forms import UserProfileForm
from farmer.models import *
from django.db.models import Q


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
    return render(request, "members/farmer_profile.html") 

def member_page(request):
    context = {
        'farmers' : Farmer.objects.all(),
        'leaders' : Leader.objects.all()
        # 'leaders' :
    }
    return render(request, "members/members.html",context) 


# def login_request(request):
#     form = AuthenticationForm()
#     return render(request,"members/login.html")

# ...................................................................................................................

def delete_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Farmer, id = id) 
  
  
    if request.method =="POST": 
        # delete object 
        obj.delete() 
        # after deleting redirect to  
        # home page 
        return redirect("/members/member_page/")
    obj.delete()
    return redirect("/members/member_page/")
   # return render(request, "members/members.html", context)




def update_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={
        # 'farmer' : farmer,
    }
    #ph = request.FILES['photo']
    # fetch the object related to passed id 
    farmer = get_object_or_404(Farmer, id = id) 
    context["farmer"] = Farmer.objects.get(id = id)
    context["users"] = UserProfile.objects.all()
    # pass the object as instance in form 
    form = FarmerForm(request.POST, request.FILES or None, instance = farmer)
  
    # save the data from the form and 
    # redirect to detail_farmer 
    if form.is_valid():
        #form.__dict__["fields"]["photo"] = ph
        form.save() 
        return redirect("/members/member_page/")
  
    # add form dictionary to context 
    context["form"] = form 
  
    return render(request, "members/editfarmer.html", context)  #editfarmer


# pass id attribute from urls 
def detail_farmer(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # add the dictionary during initialization 
    context["data"] = Farmer.objects.get(id = id)
    context["produce"] = Produce.objects.all()
    context["transactions"] = ew_transaction.objects.filter(user = context["data"].user)
    context["orders"] = Orders.objects.filter(buyer = context["data"].user)
    context["landdetails"] = Land.objects.filter(owner=context["data"].user)
    context["bankdetails"] = BankDetails.objects.filter(user=context["data"].user)
    return render(request, "members/farmer_profile.html", context) 


def add_farmer(request): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # add the dictionary during initialization 
    form = FarmerForm(data = request.POST , files=request.FILES) 
    if form.is_valid():
        aadhar = form.cleaned_data['aadhar']
        contact = form.cleaned_data['contact']
        if len(aadhar) != 12 or len(contact) != 10:
            return redirect('/members/member_page/add_farmer')
        form.save() 
        return redirect('/members/member_page')
          
    context['form']= form
    context['users'] = UserProfile.objects.all()
    return render(request, 'members/addnewfarmer.html', context)


#..............................................................................................................


def delete_leader(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Leader, id = id) 
  
  
    if request.method =="POST": 
        # delete object 
        obj.delete() 
        # after deleting redirect to  
        # home page 
        return redirect("/fpo/member_page/")
    obj.delete()
    return redirect("/members/member_page/")
  
    #return render(request, "members/delete_leader.html", context)




def update_leader(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
  
    # fetch the object related to passed id 
    obj = get_object_or_404(Leader, id = id)
    context["leader"] = Leader.objects.get(id = id)
    context["users"] = UserProfile.objects.all()
    context["farmers"] = Farmer.objects.all()
    # pass the object as instance in form 
    form = LeaderForm(request.POST, request.FILES or None, instance = obj)
  
    # save the data from the form and 
    # redirect to detail_leader
    if form.is_valid(): 
        form.save() 
        return redirect("/members/member_page/")
  
    # add form dictionary to context 

    return render(request, "members/editleader.html", context)


# pass id attribute from urls 
def detail_leader(request, id): 
    # dictionary for initial data with  
    # field names as keys 
    context ={} 
    leader = Leader.objects.get(id = id)
    users = leader.farmers.all()
    farmer1 = []
    print(users)
    for farmer in users:
        far = Farmer.objects.filter(user_id = farmer.id)
        farmer1.append(far.first())

        # print(far.first().village)


  
    # add the dictionary during initialization 
    context["leader"]=farmer1
    context["data"] = Leader.objects.get(id = id)
    context["farmers"] = Farmer.objects.all()
    context["transactions"] = ew_transaction.objects.filter(user=context["data"].user)
    return render(request, "members/leader_profile.html", context)



def add_leader(request):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # add the dictionary during initialization
    form = LeaderForm(data=request.POST, files=request.FILES)
    if form.is_valid():
        aadhar = form.cleaned_data['aadhar']
        contact = form.cleaned_data['contact']
        if len(aadhar) != 12 or len(contact) != 10:
            return redirect('/members/member_page/add_farmer')
        form.save()
        return redirect('/members/member_page')

    context['form'] = form
    context['farmers'] = Farmer.objects.all()
    context['users'] = UserProfile.objects.all()
    return render(request, 'members/addnewleader.html', context)


def add_user(request):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # add the dictionary during initialization
    # form = FarmerForm(data = request.POST , files=request.FILES)
    form = UserProfileForm(data=request.POST, files=request.FILES)

    if form.is_valid():
        user = form.save()
        # password = form.cleaned_data['password']
        user.set_password(user.password)
        user.save()
        # form_u.save()
        return redirect('/members/member_page')
    context['form'] = form
    context['users'] = UserProfile.objects.all()
    # context['form_u']=form
    return render(request, 'members/add.html', context) #add


# def leader_del_farmer(request, id1, id2):
#     # dictionary for initial data with
#     # field names as keys
#     context = {}

#     # add the dictionary during initialization
#     # user
#     # context["leader"] = Leader2.objects.get(user_id = id).all()
#     obj = get_object_or_404(Leader, id=id1)
#     sub = get_object_or_404(obj.farmers, id=id2)
#     # sub = Leader.objects.filter(user_id = id1, farmers_id = id2)

#     obj.farmers.remove(sub)
#     if (obj.farmers.count() >= 0):
#         print(obj.farmers.all())
#         return redirect('detail_leader',id=id1)

#     # farmer = []

#     # print(sub)
#     # for leader in sub :
#     #     obj = leader.farmers
#     #     farmer.append(obj)
#     #     print(obj)
#     # context["farmers"] = farmer
#     return render(request, "members/leader_farmer.html", context)


# def leader_add_farmer(request, id):
#     # dictionary for initial data with
#     # field names as keys
#     context = {}

#     form = leader_add_farmerForm(request.POST or None, files=request.FILES)
#     if form.is_valid():
#         # form.save()
#         context["form"] = form
#         username = form.cleaned_data['username']
#         print(username)
#         obj = get_object_or_404(UserProfile, username=username, category='F')
#         print(obj.id)
#         obj2 = get_object_or_404(Farmer, user=obj.id)
#         sub = get_object_or_404(Leader, id=id)
#         sub.farmers.add(obj2)
#         sub.save()
#         if (sub.farmers.count() > 0):
#             # for far in sub.farmers.all():
#             #     print("hello")
#             print(sub.farmers.all())
#             return redirect('detail_leader',id=id)

#     context['form'] = form
#     context['users'] = UserProfile.objects.all()
#     return render(request, "members/leader_profile.html", context)  # change according to tempalate


def leader_del_farmer(request, id1, id2):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # add the dictionary during initialization
    # user
    # context["leader"] = Leader2.objects.get(user_id = id).all()
    obj = get_object_or_404(Leader, id=id1)
    sub = get_object_or_404(obj.farmers, id=id2)
    # sub = Leader.objects.filter(user_id = id1, farmers_id = id2)

    obj.farmers.remove(sub)
    if (obj.farmers.count() >= 0):
        print(obj.farmers.all())
    return redirect('detail_leader',id=id1)

    # farmer = []

    # print(sub)
    # for leader in sub :
    #     obj = leader.farmers
    #     farmer.append(obj)
    #     print(obj)
    # context["farmers"] = farmer
    return render(request, "members/leader_farmer.html", context)


def leader_add_farmer(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    form = leader_add_farmerForm(request.POST or None, files=request.FILES)
    if form.is_valid():
        # form.save()
        context["data"] = form
        context["form"] = form

        username = form.cleaned_data['username']
        print(username)
        q1 = Q(category='F')
        q2 = Q(category='P')
        q3 = Q(category='N')
        q4 = Q(username=username)
        obj = UserProfile.objects.filter((q1 | q2 | q3) & q4)   
        # obj = get_object_or_404(UserProfile, username=username, category='F')
        print(obj.count())

        
        if obj.count() < 1:
            return redirect('/members/member_page/view_leader/'+id)
        objt = get_object_or_404(UserProfile, username=obj[0].username)
        # print(obj[0].username)
    
        
        obj2 = get_object_or_404(Farmer, user=objt.id)   
        print(obj2)
        sub = get_object_or_404(Leader, id=id)
        sub.farmers.add(objt)
        sub.save()
        if (sub.farmers.count() > 0):
            for far in sub.farmers.all():
                print(far.id)
            # print(sub.farmers.all())
            # return redirect('detail_leader',id=id)
        return redirect('/members/member_page/view_leader/'+id)

    context['form'] = form
    context['users'] = UserProfile.objects.all()
    return render(request, "members/leader_profile.html", context)  # change according to tempalate

#def add_FPOLedger(request):
    # field names as keys
   # context = {}

    # add the dictionary during initialization
    # form = FarmerForm(data = request.POST , files=request.FILES)
    #form = FPOLedgerForm(data=request.POST, files=request.FILES)
    #if form.is_valid():
     #   form.save()
        # form_u.save()
      #  return redirect('/members/member_page/fpoledger')

    #context['form'] = form
    #context['crops'] = Crops.objects.all()

    # context['form_u']=form
    #return render(request, 'members/addfpoledger.html', context)


def add_FPOLedger(request):
    # field names as keys
    context = {}

    # add the dictionary during initialization
    # form = FarmerForm(data = request.POST , files=request.FILES)
    form = FPOLedgerForm(data=request.POST, files=request.FILES)
    if form.is_valid():
        form.save()
        name = form.cleaned_data['crop']
        price = form.cleaned_data['price']
        amount_sold = form.cleaned_data['amount_sold']
        sub = Produce.objects.filter(crop=name)
        total = 0.0
        for e in sub:
            total = total + e.amount
        for e in sub:
            print(e.amount)
            cost = (e.amount / total) * price
            e.income += cost
            e.save()
            owner = e.owner
            last_amount = 0.0
            if ew_transaction.objects.count() > 0:
                last = ew_transaction.objects.filter(user=owner).last()
                if last:
                    last_amount = last.currrent_amount
                else:
                    last_amount = 0

            p = ew_transaction.objects.create(user=owner, amount=cost, currrent_amount=last_amount + cost, description='crop sold')
        # form_u.save()
        return redirect('/members/member_page/fpoledger')

    context['form'] = form
    context['users'] = UserProfile.objects.all()
    context['crops'] = Crops.objects.all()

    # context['form_u']=form
    return render(request, 'members/addfpoledger.html', context)


def del_FPOLedger(request, id):
    context = {}

    obj = get_object_or_404(FPOLedger, id=id)

    if obj.id > 0:
        # delete object
        obj.delete()
        # after deleting redirect to
        # home page
        return redirect("/members/member_page/fpoledger")

    # context['form_u']=form
    return render(request, 'members/add.html', context)


def FPOLedger_page(request):
    context = {
        'fpoledger': FPOLedger.objects.all()
        # 'leaders' :
    }
    return render(request, "members/fpoledger.html", context)


def add_Produce(request):
    # field names as keys
    context = {}

    # add the dictionary during initialization
    # form = FarmerForm(data = request.POST , files=request.FILES)
    form = ProduceForm(data=request.POST, files=request.FILES)
    if form.is_valid():
        form.save()
        # form_u.save()
        return redirect('/members/member_page/produce')

    context['form'] = form
    context['farmers'] = Farmer.objects.all()
    context['crops'] = Crops.objects.all()

    # context['form_u']=form
    return render(request, 'members/addnewprorecord.html', context)




def del_Produce(request, id):
    context = {}

    obj = get_object_or_404(Produce, id=id)

    if obj.id > 0:
        # delete object
        obj.delete()
        # after deleting redirect to
        # home page
        return redirect("/members/member_page/produce")

    # context['form_u']=form
    return render(request, 'members/add.html', context)


def produce_page(request):
    context = {
        'produce': Produce.objects.all()
        # 'leaders' :
    }
    return render(request, "members/production.html", context)


#def add_transaction(request):
    # field names as keys
 #   context = {}

    # add the dictionary during initialization

  #  form = TransactionForm(data=request.POST, files=request.FILES)
   # if form.is_valid():
    #    form.save()
        # form_u.save()
     #   return redirect('/members/member_page/transaction')

    #context['form'] = form
    #context['users'] = UserProfile.objects.all()

    # context['form_u']=form
    #return render(request, 'members/addnewtransaction.html', context)


def add_transaction(request):
    # field names as keys
    context = {}

    # add the dictionary during initialization

    form = TransactionForm(data=request.POST, files=request.FILES)

    # obj = get_object_or_404(ew_transaction, user = form.user)

    if form.is_valid():
        user = form.cleaned_data['user']
        amount = form.cleaned_data['amount']
        description = form.cleaned_data['description']
        last = ew_transaction.objects.filter(user=user).last()
        if last:
            last_amount = last.currrent_amount
        else:
            last_amount = 0
        currrent_amount = last_amount + amount
        p = ew_transaction.objects.create(user=user, amount=amount, currrent_amount=currrent_amount,
                                          description=description)
        # form.save()
        # form_u.save()
        return redirect('/members/member_page/transaction')

    context['form'] = form
    context['users'] = UserProfile.objects.all()

    # context['form_u']=form
    return render(request, 'members/addnewtransaction.html', context)


def del_transaction(request, id):
    context = {}

    obj = get_object_or_404(ew_transaction, id=id)

    if obj.id > 0:
        # delete object
        obj.delete()
        # after deleting redirect to
        # home page
        return redirect("/members/member_page/transaction")

    # context['form_u']=form
    return render(request, 'members/add.html', context)


def edit_transaction(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(ew_transaction, id=id)

    # pass the object as instance in form
    form = TransactionForm(request.POST or None, instance=obj)

    # save the data from the form and
    # redirect to detail_leader
    if form.is_valid():
        form.save()
        return redirect("/members/member_page/")

    # add form dictionary to context
    context["form"] = form

    return render(request, "members/add.html", context)


def view_transaction(request):
    context = {
        'transactions': ew_transaction.objects.all(),

    }
    return render(request, "members/transactions.html", context)


def view_transaction_id(request, user):
    context = {
        'transaction': ew_transaction.objects.filter(user=user),

    }
    print(context['transaction'])
    for e in context['transaction']:
        print(e)
    return render(request, "members/add.html", context)


def orders_view(request):
    # dictionary for initial data with
    # field names as keys
    context = {
        'orders': Orders.objects.all()
    }

    return render(request, 'members/orders.html', context)


def orders_delivered_toggle(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Orders, id=id)

    obj.is_delivered ^= True
    obj.save()

    return redirect("/members/member_page/orders")
    #return render(request, "members/orders.html", context)


def orders_paid_toggle(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Orders, id=id)

    obj.is_paid ^= True
    obj.save()
    return redirect("/members/member_page/orders")
    #return render(request, "members/orders.html", context)


def orders_delete(request, id):
    # dictionary for initial data with
    # field names as keys
    context = {}

    # fetch the object related to passed id
    obj = get_object_or_404(Orders, id=id)

    if obj.id > 0:
        # delete object
        obj.delete()
        # after deleting redirect to
        # home page
        return redirect("/member/member_page/orders")

    return render(request, "members/orders.html", context)


def orders_add(request):
    context = {
        'orders': Orders.objects.all()
    }
    form = OrdersForm(request.POST or None)
    if form.is_valid():
        form.save()
        return redirect('/members/member_page/orders')

    context['form'] = form
    context['farmers'] = Farmer.objects.all()
    context['items'] = Products.objects.all()
    return render(request, 'members/addneworder.html', context)


def orders_edit(request, id):
    context = {
        'orders': Orders.objects.all()
    }
    obj = get_object_or_404(Orders, id=id)

    # pass the object as instance in form
    form = OrdersForm(request.POST or None, instance=obj)

    # save the data from the form and
    # redirect to detail_farmer
    if form.is_valid():
        form.save()
        return redirect("/members/member_page/orders")

    # add form dictionary to context
    context["form"] = form

    return render(request, "members/orders.html", context)


def bank_add_farmer(request,id):
    context = {}
    form = BankForm(data=request.POST, files=request.FILES)
    if form.is_valid():
        form.save()
        return redirect('/members/member_page')

    context['form'] = form
    context['users'] = UserProfile.objects.all()
    return render(request, 'members/addbank.html', context)


def land_add_farmer(request,id):
    context = {}
    form = LandForm(data=request.POST, files=request.FILES)
    if form.is_valid():
        form.save()
        return redirect('/members/member_page/')

    context['form'] = form
    context['users'] = UserProfile.objects.all()
    return render(request, 'members/addland.html', context)
# def order(request):
#   return render(request, "members/farmer_profile.html")
# def leader_profile(request):
#     return render(request, "members/leader_profile.html")     