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

class MeetingTokenAdmin(admin.ModelAdmin):
    list_display = ('id', '__str__', 'has_rsvped', 'did_attend', 'get_village', 'created_at')
    list_filter = ('has_rsvped', 'did_attend')
    list_editable = ('has_rsvped', 'did_attend')
    list_perpage = 25
	
    def get_village(self, obj):
        return obj.farmer.village

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
admin.site.register(MeetingToken, MeetingTokenAdmin)
admin.site.register(Govt)



