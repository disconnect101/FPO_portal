import pandas as pd
import numpy as np
import sklearn
from sklearn import linear_model
from farmer.models import Produce, Orders, FarmerCropMap
from django.db.models import Count, Sum

class Recommender:

    crops = ""
    soils = ""
    linearRegressionModel = None


    def gatherData(self):
        self.crops = Produce.objects.values('crop__code').distinct()
        self.soils = Produce.objects.values('land__soil').distinct()
        allProduce = Produce.objects.all()

        data = []
        for produce in allProduce:
            produceRecord = []
            test = []
            year = produce.date.year
            farmer = produce.owner
            investment = Orders.objects.filter(buyer=farmer, date__year=year, is_paid=True).annotate(total=Sum('price'))
            try:
                landarea = FarmerCropMap.objects.filter(farmer=farmer, crop=produce.crop, date__year=year).first().landarea
            except:
                continue

            if investment.count()==0:
                continue
            produceRecord = [investment.first().total, landarea]
            test.append(investment.first().total)
            test.append(landarea)
            if not produce.land:
                continue
            for soil in self.soils:
                if soil.get('land__soil') == produce.land.soil:
                    produceRecord.append(1)
                    test.append(produce.land.soil)
                else:
                    produceRecord.append(0)

            for crop in self.crops:
                if crop.get('crop__code') == produce.crop.code:
                    produceRecord.append(1)
                    test.append(produce.crop.code)
                else:
                    produceRecord.append(0)

            test.append(produce.income)
            profit = ((produce.income - investment.first().total) / investment.first().total) * 100
            produceRecord.append(profit)

            data.append(produceRecord)
            print(test)
        print(self.crops)
        print(data)
        return data



    def trainModel(self, data):
        cols = []
        cols = ['Investments', 'Landarea']

        for soil in self.soils:
            cols.append(soil.get('land__soil'))

        for crop in self.crops:
            cols.append(crop.get('crop__code'))

        cols.append('profit')

        df = pd.DataFrame(data, columns=cols)

        X = np.array(df.drop(['profit'], 1))
        Y = np.array(df['profit'])

        self.linearRegressionModel = linear_model.LinearRegression()
        self.linearRegressionModel.fit(X,Y)



    def serializedata(self, farmerData, liveCropPlans):
        serializedData = []

        for liveCropPlan in liveCropPlans:
            arr = [ farmerData['investment'], farmerData['landarea']]

            for soil in self.soils:
                if soil.get('land__soil')==farmerData['soil']:
                    arr.append(1)
                else:
                    arr.append(0)

            for crop in self.crops:
                if crop.get('crop__code')==liveCropPlan.get('code'):
                    arr.append(1)
                else:
                    arr.append(0)

            serializedData.append(arr)

        return serializedData


    def predict(self, farmerData, liveCropPlans):
        if not self.linearRegressionModel:
            raise Exception("Model not Trained")

        serializedFarmerData = self.serializedata(farmerData, liveCropPlans)
        profit = self.linearRegressionModel.predict(serializedFarmerData)

        return profit






