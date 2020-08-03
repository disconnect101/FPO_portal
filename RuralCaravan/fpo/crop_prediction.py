import pandas as pd
import numpy as np
from sklearn.utils import shuffle
from sklearn.preprocessing import LabelEncoder
from sklearn.neighbors import KNeighborsClassifier
import pickle


# # Loading the data from the csv file
# # There are total 101 crops in this csv file with NPK ph temp and climate values
data = pd.read_csv('fpo/Crop1.csv')

# Throws out a random permutation of the data
data = shuffle(data)

# We get the crop names from the dataframe 
y = data.loc[:, 'Crop']

# We're using label encoding so that the names of the crops (which are strings) can be converted into numbers that are easily interpreted by a model
labelEncoded_y = LabelEncoder()

# Applying the transformation
y = labelEncoded_y.fit_transform(y)

# Creating a new column for the transformed names
data['crop_num'] = y

# Now we get the features for predictions
X = data[['N', 'P', 'K', 'pH','temp', 'climate']]

# The labels 
y = data['crop_num']


# Getting the K Nearest Neighbors
# Why is the weights parameter equal to 'distance'?
# -> This is because when the points we need to predict for are very close (Euclidean distance) to a certain crop's parameters, we give it more importance
classifier = KNeighborsClassifier(n_neighbors=20, n_jobs=3, weights='distance')

# Now we fit our data
classifier.fit(X, y)


def get_crop_prediction(Nitrogen, Phosphorous, Potassium, pH, temperature, climate):
    # All the values are floats except the climate one 
    # Climate can be: summer (1) winter (2) and monsoon (3)
    columns = columns = ['N', 'P', 'K', 'pH', 'temp', 'climate']

    if climate.lower() == 'summer':
        climate = 1
    elif climate.lower() == 'winter':
        climate = 2
    elif climate.lower() == 'monsoon' or 'rainy':
        climate = 3
    
    # Create an array of the values
    values = np.array([Nitrogen, Phosphorous, Potassium, pH, temperature, climate])    

    # Reshape the values so that all the values show up as columns
    values = values.reshape(-1, len(values))

    # Creating a dataframe with columns for the prediction
    predict_for = pd.DataFrame(values, columns=columns)

    # Just gives us the top prediction's label
    top_prediction_label = classifier.predict(predict_for)

    # This will return the probabilities of matches with other crops
    probabilities = classifier.predict_proba(predict_for).flatten()

    # Now we're getting the crops in the incresing order of probabilities
    top_predicted_crops = [data[data['crop_num'] == index] for index in np.argsort(probabilities)] 

    # The probabilities will have to be reversed so that we get the highest one first
    top_predicted_crops = top_predicted_crops[::-1]

    top_5 = []

    for crop in top_predicted_crops[:5]:
        crop = crop.iloc[:, 1:].to_dict()
        crop_data = {k: list(v.values())[0] for k, v in crop.items()}
        if crop_data['climate'] == climate:
            top_5.append(crop_data)
    
    return top_5

if __name__ == "__main__":
    print(get_crop_prediction(23, 35, 41, 4.9, 14, 'winter'))