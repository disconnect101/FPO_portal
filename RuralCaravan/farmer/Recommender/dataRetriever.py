from farmer.models import Produce, Orders, FarmerCropMap
from django.db.models import Count, Sum


def getData():
    crops = Produce.objects.values('crop__code').distinct()
    soils = Produce.objects.values('land__soil').distinct()
    allProduce = Produce.objects.all()

    data = []
    for produce in allProduce:
        produceRecord = []
        year = produce.date.year
        farmer = produce.owner
        investment = Orders.objects.filter(buyer=farmer, date__year=year, is_paid=True).annotate(total=Sum('price'))
        landarea = FarmerCropMap.objects.filter(farmer=farmer ,crop=produce.crop, date__year=year).first().landarea
        produceRecord = [investment.first().total, landarea]
        for crop in crops:
            if crop.get('crop__code')==produce.crop.code:
                produceRecord.append(1)
            else:
                produceRecord.append(0)

        for soil in soils:
            if soil.get('land__soil')==produce.land.soil:
                produceRecord.append(1)
            else:
                produceRecord.append(0)

        profit = ((produce.income-investment.first().total)/investment.first().total)*100
        produceRecord.append(profit)

        data.append(produceRecord)


    print(data, "fverv")
    return data, crops, soils




