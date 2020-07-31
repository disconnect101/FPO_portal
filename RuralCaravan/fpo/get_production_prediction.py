import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.svm import SVR
import pprint

pp = pprint.PrettyPrinter()


def predict_production(data):
    # data is simply -> {'Year': [2000, 2001, 2002], 'Production': [1,2,3]}
    if not data['Year']:
        return 0, 0
    new_data = pd.DataFrame(data=data)
    regressor = LinearRegression()
    # regressor = SVR(kernel="rbf")    
    X = np.array(new_data['Year']).reshape(-1, 1)
    y = np.array(new_data['Production'])
    regressor.fit(X, y)
    next_year = [data['Year'][-1]+1]
    return next_year[0], regressor.predict(np.array(next_year).reshape(-1,1))[0]



if __name__ == '__main__':

    data = pd.read_csv('Cleaned_Annual_Production.csv')

    def get_production_data(dataFrame, element):
        index = data.iloc[element, :].index
        values = data.iloc[element, :].values
        years = [int(x[3:]) for x in index[4:]]
        production = values[4:]
        return {'Year': years, 'Production': production}
    
    predict_for = get_production_data(data, 1)
    pp.pprint(predict_for)

    prediction = predict_production(predict_for)
    print(prediction)
