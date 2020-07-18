from django.shortcuts import render

# Create your views here.

def test(request):
    context = {
        test: 'test',
    }
    return render(request, 'fpo/test.html', context)