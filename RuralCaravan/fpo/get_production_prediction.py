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
    data = {
        'Year': [2019, 2020],
        'Production': [100, 200]
    }

    prediction = predict_production(data)
    print(prediction)
