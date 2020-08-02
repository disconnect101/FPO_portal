from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.models import Meetings, MeetingToken, Farmer, Govt
from rest_framework.response import Response
from datetime import datetime
from farmer.api.utils import statuscode
from farmer.api.serializers import MeetingsSerializer
import json

@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def meetings(request):
    user = request.user
    farmer = Farmer.objects.get(user=user)
    currDate = datetime.now().date()
    meetings = Meetings.objects.filter(date__gte=currDate).values()

    meetingsList = []
    for meeting in meetings:
        #serialized = MeetingsSerializer(meeting)
        try:
            meetingtoken = MeetingToken.objects.get(farmer=farmer, meeting_id=meeting.get('id'))
        except:
            return Response(statuscode('Meeting token for meetingID : ' + str(meeting.get('id'))  + ' couldn\'t be found'))
        if meetingtoken.has_rsvped:
            meeting.update({'meetingtoken': 'at'})
        else:
            meeting.update( { 'meetingtoken': meetingtoken.token_number } )
        meetingsList.append(meeting)

    data = { 'data': meetingsList }
    return Response(statuscode('0', data))


@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def rsvpMeeting(request):
    user = request.user

    try:
        meetingToken = request.data.get('meetingtoken')
    except:
        return Response(statuscode('23'))
    try:
        token = MeetingToken.objects.get(token_number=meetingToken)
    except:
        return Response(statuscode('24'))
    token.has_rsvped = True
    token.save()

    return Response(statuscode('0'))


@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def govtSchemes(request):
    try:
        govtSchemes = Govt.objects.all().values()
    except:
        return Response(statuscode('12'))

    data = {
        'data': govtSchemes
    }
    return Response(statuscode('0', data))



