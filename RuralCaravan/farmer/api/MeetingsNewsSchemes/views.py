from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.models import Meetings, MeetingToken, Farmer
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
        meetingtoken = MeetingToken.objects.get(farmer=farmer, meeting_id=meeting.get('id'))
        meeting.update( { 'meetingtoken': meetingtoken.token_number } )
        meetingsList.append(meeting)

    data = { 'data': meetingsList }
    return Response(statuscode('0', data))


@api_view(['POST', ])
@permission_classes((IsAuthenticated, ))
def rsvpMeeting(request):
    user = request.user

    meetingToken = request.data.get('meetingtoken')
    token = MeetingToken.objects.get(token_number=meetingToken)
    token.has_rsvped = True
    token.save()

    return Response(statuscode('0'))




