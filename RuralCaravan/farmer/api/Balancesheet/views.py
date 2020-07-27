from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from farmer.models import ew_transaction, Ewallet
from farmer.api.utils import statuscode


@api_view(['GET', ])
@permission_classes((IsAuthenticated, ))
def balancesheet(request):
    user = request.user
    try:
        balancesheet = ew_transaction.objects.filter(user=user).order_by('-date').values()
        current_amount = Ewallet.objects.filter(user=user).values('amount').first()
    except:
        return Response(statuscode('12'))

    data = {
        'balancesheet': balancesheet,
        'current_amount': current_amount,

    }
    return Response(statuscode('0', data))