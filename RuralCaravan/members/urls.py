from django.urls import path
from . import views
from django.conf import settings
from django.conf.urls.static import static

urlpatterns = [
    path('member_page/add_farmer', views.add_farmer, name = 'add_farmer'),
    path('member_page/view_farmer/<id>/', views.detail_farmer, name = 'detail_farmer'),
    path('member_page/edit_farmer/<id>', views.update_farmer, name = 'update_farmer'),
    path('member_page/delete_farmer/<id>', views.delete_farmer, name = 'delete_farmer'),
    
    path('member_page/add_leader', views.add_leader, name = 'add_leader'),
    path('member_page/view_leader/<id>/', views.detail_leader, name = 'detail_leader'),
    path('member_page/edit_leader/<id>', views.update_leader, name = 'update_leader'),
    path('member_page/delete_leader/<id>', views.delete_leader, name = 'delete_leader'),

    path('member_page/add_user', views.add_user, name='add_user'),
       
    # path('farmer_profile/', views.farmer_profile, name = 'farmer_profile'),
    # path('leader_profile/', views.leader_profile, name = 'leader_profile'),
    path('member_page/', views.member_page, name = 'members'),

    path('member_page/view_leader/<id1>/delete/<id2>', views.leader_del_farmer, name = 'leader_del_farmer'),
    path('member_page/view_leader/<id>/add/', views.leader_add_farmer, name = 'leader_add_farmer'),

    path('member_page/fpoledger', views.FPOLedger_page, name='FPOLedger'),
    path('member_page/fpoledger/add', views.add_FPOLedger, name = 'add_FPOLedger'),
    path('member_page/fpoledger/delete/<id>', views.del_FPOLedger, name = 'del_FPOLedger'),

    path('member_page/produce',views.produce_page,name='produce'),
    path('member_page/produce/add', views.add_Produce, name = 'add_Produce'),
    path('member_page/produce/delete/<id>', views.del_Produce, name = 'del_Produce'),

    path('member_page/transaction', views.view_transaction, name = 'transaction'),
    path('member_page/transaction/add', views.add_transaction, name = 'add_transaction'),
    path('member_page/transaction/delete/<id>', views.del_transaction, name = 'del_transaction'),

    path('member_page/orders/', views.orders_view, name='orders'),
    path('member_page/orders/add', views.orders_add, name = 'orders-add'),
    path('member_page/orders/edit/<id>', views.orders_edit, name = 'orders-edit'),
    path('member_page/orders/delete/<id>', views.orders_delete, name='orders-delete'),
    path('member_page/orders/delivered/<id>', views.orders_delivered_toggle, name='orders-toggle-delivered'),
    path('member_page/orders/paid/<id>', views.orders_paid_toggle, name='orders-toggle-paid'),
    #path('transaction/edit/<id>', views.del_transaction, name = 'edit_transaction'),
    # path('plan/', views.plan, name = 'fpo-plan'),
    # path('meeting/', views.meeting, name = 'fpo-meeting'),
    # path('govt_schemes/', views.govt_schemes, name = 'fpo-govt_schemes'),
    #path('orders/',views.orders, name= 'order')

]