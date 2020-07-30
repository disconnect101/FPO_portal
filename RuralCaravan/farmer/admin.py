from django.contrib import admin
from farmer.models import *
from django.contrib.auth.admin import UserAdmin
# Register your models here.

class UserProfileAdmin(UserAdmin):
    list_display = ('username', 'category', 'is_admin',)
    search_fields = ('username',)
    readonly_fields = ('date_joined', 'last_login',)

    filter_horizontal = ()
    list_filter = ()
    fieldsets = ()

admin.site.register(UserProfile, UserProfileAdmin)
admin.site.register(Leader)
admin.site.register(Farmer)
admin.site.register(Crops)
admin.site.register(FarmerCropMap)
admin.site.register(BankDetails)
admin.site.register(Land)
admin.site.register(Meetings)
admin.site.register(Products)
admin.site.register(Orders)
admin.site.register(Produce)
admin.site.register(Kart)
admin.site.register(ew_transaction)
admin.site.register(FPOLedger)
#admin.site.register(Produce_FPOLedger_Map)
admin.site.register(Contact)
admin.site.register(Ewallet)
admin.site.register(MeetingToken)
admin.site.register(Govt)



