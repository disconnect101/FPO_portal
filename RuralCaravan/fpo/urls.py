from django.urls import path
from . import views
from django.contrib import admin
from django.urls import path, include
from django.urls import path, include, re_path
from django.conf import settings
from django.conf.urls.static import static

urlpatterns = [
    path('', views.home, name = 'home'),
    path('logout/', views.logout_request, name='logout'),
    path('login/', views.login_request, name='login'),
    
    
    # path('plans/', views.plans_view, name = 'plans'),
    path('govtschemes/', views.govtschemes_view, name = 'govtschemes'),
    path('govtschemes/delete/<id>', views.govtschemes_delete, name = 'govtschemes'),
    path('govtschemes/update/<id>', views.govtschemes_update, name = 'govtschemes-update'),

	
	#added by aditya
	path('govtschemes/<int:id>', views.govtschemes_single, name='govtschemes_single'),
    path('govtschemes/<int:id>/add/', views.govtschemes_single_add, name='govtschemes_single_add'),
	
	path('statistics/', views.fpo_statistics, name='fpo_statistics'),


    path('send_message/', views.send_message, name='send_message'),



    path('redeem/', views.redeem, name="redeem"),
    path('populate_farmers/', views.populate_farmers, name='populate_farmers'),
    path('mark_rsvp/', views.mark_rsvp, name='mark_rsvp'),
    path('mark_attendance/', views.mark_attendance, name='mark_attendance'),
    path('mark_redeemed/', views.mark_redeemed, name='mark_redeemed'),
	#added by aditya
	
	
    path('meetings/<id>/', views.detail_meetings, name = 'fpo-detail_meetings'),
    path('meetings/', views.meetings_view, name = 'meetings'),
    path('meetings/update/<id>', views.meetings_update, name = 'meetings-update'),
    path('meetings/delete/<id>', views.meetings_delete, name = 'meetings-delete'),

    path('plans/', views.plans_view, name = 'plans'),
    path('plans/update/<id>', views.plans_update, name = 'plans-update'),
    path('plans/delete/<id>', views.plans_delete, name = 'plans-delete'),
    path('plans/toggle/<id>', views.plans_toggle, name = 'plans-toggle'),
    path('plans/detail/<id>', views.plans_detail, name = 'plans-detail'),
    
    path('plans/api/data_village_count', views.data_village_count, name = 'plans-data-village-count'),
    path('plans/api/data_village_quantity', views.data_village_quantity, name = 'plans-data-village-quantity'),
    path('plans/detail/<id1>/delete/<id2>', views.plan_del_product, name = 'plan_del_product'),
    path('plans/detail/<id>/add/', views.plan_add_product, name = 'plan_add_product'),

    path('products/', views.products_view, name='products'),
    path('products/update/<id>', views.products_update, name='products-update'),
    path('products/delete/<id>', views.products_delete, name='products-delete'),
    path('products/detail/<id>', views.products_detail, name='products-detail'),
    path('products/toggle/<id>', views.products_toggle, name='products-toggle'),
    

    path('orders/', views.orders_view, name='orders'),
    path('orders/delete/<id>', views.orders_delete, name='orders-delete'),
    path('orders/delivered/<id>', views.orders_delivered_toggle, name='orders-toggle-delivered'),
    path('orders/paid/<id>', views.orders_paid_toggle, name='orders-toggle-paid'),

    
    # path('plan/', views.plan, name = 'fpo-plan'),
    # path('meeting/', views.meeting, name = 'fpo-meeting'),
    # path('govt_schemes/', views.govt_schemes, name = 'fpo-govt_schemes'),
   


] 
