from farmer.models import *
from django.db.models import Count, Sum
from datetime import datetime

class StatisticalAnalysis:
    productionYearWise = ""
    cropWiseProduction = ""

    def __init__(self):
        self.productionYearWise = Produce.objects.values('date__year').annotate(amount=Sum('amount'), income=Sum('income'))
        self.cropWiseProduction = Produce.objects.values('crop__code').annotate(amount=Sum('amount')).filter(date__year=datetime.now().year)

    def getAvgProduction(self):
        years = self.productionYearWise.values('date__year').distinct().count()
        total = sum(production.get('amount') for production in self.productionYearWise )
        if years==0:
            return 0
        return total/years


    def getAvgProfit(self):
        years = self.productionYearWise.values('date__year').distinct().count()
        total = sum(production.get('income') for production in self.productionYearWise)
        if years==0:
            return 0
        return total/years



    def getCropProductionByYear(self):
        crops = Crops.objects.values('name').distinct()

        cropProductionByYear = []
        for crop in crops:
            cropProduction = self.productionYearWise.filter(crop__name=crop.get('name'))
            years = []
            production = []
            for produce in cropProduction:
                years.append(produce.get('date__year'))
                production.append(produce.get('amount'))

            data = {
                'name': crop.get('name'),
                'years': years,
                'data': production
            }

            cropProductionByYear.append(data)
    
        return cropProductionByYear



    def getCropProfitsByYear(self):
        crops = Crops.objects.values('name').distinct()

        cropProfitsByYear = []
        for crop in crops:
            cropProfits = self.productionYearWise.filter(crop__name=crop.get('name'))
            years = []
            profits = []
            for produce in cropProfits:
                years.append(produce.get('date__year'))
                profits.append(produce.get('income'))

            data = {
                'name': crop.get('name'),
                'years': years,
                'data': profits
            }

            cropProfitsByYear.append(data)
        
        return cropProfitsByYear


    def getProfitsByYear(self):
        yearMonthWiseProfits = Produce.objects.values('date__year', 'date__month').annotate(income=Sum('income'))
        years = self.productionYearWise.values('date__year').distinct()
        profitsByYear = []

        profitsByYear = []
        for year in years:
            monthWise = yearMonthWiseProfits.filter(date__year=year.get('date__year'))
            MONTHS = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
            profits = [0 for _ in MONTHS]
            for month in monthWise:
                month_name = datetime.strptime(str(month['date__month']), "%m")
                profits[MONTHS.index(month_name.strftime("%B"))] = month.get('income')

            yeardata = {
                'year': str(year.get('date__year')),
                'data': profits
            }

            profitsByYear.append(yeardata)

        return profitsByYear


    def getCropWiseProduce(self):
        crops = []
        productions = []

        distinct_years = [list(x.values())[0] for x in Produce.objects.values('date__year').distinct()]

        crop_amounts = {}

        for year in distinct_years:
            yearly_produce = (Produce.objects.filter(date__year=year).values('crop__name').annotate(total=Sum('amount')))
            for produce in yearly_produce:
                if produce['crop__name'] in crop_amounts.keys():
                    crop_amounts[produce['crop__name']] += produce['total']
                else:
                    crop_amounts[produce['crop__name']] = produce['total']
        
        for k, v in crop_amounts.items():
            crops.append(k)
            productions.append(v)

        data = {
            'crops': crops,
            'productions': productions
        }


        return data