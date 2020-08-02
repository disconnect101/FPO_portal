from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from farmer.models import Meetings, MeetingToken
from rest_framework.response import Response
from datetime import datetime
from farmer.api.utils import statuscode
from farmer.api.serializers import MeetingsSerializer
import json

@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def meetings(request):
    currDate = datetime.now().date()
    meetings = Meetings.objects.filter(date__gte=currDate)

    meetingsList = []
    for meeting in meetings:
        serialized = MeetingsSerializer(meeting)
        meetingsList.append(serialized.data)

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




