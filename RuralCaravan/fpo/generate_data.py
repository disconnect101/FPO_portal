import faker 
from random import choice
import pprint
f = faker.Faker()
pp = pprint.PrettyPrinter()

def get_data():
    Leaders = [f.name() for _ in range(3)] + ['', '']
    Locality = ['Bilaspur', 'Mungeli', 'Dhule', 'Amravati', 'Gwalior']
    True_False = [True, False]
    Phone_Type = ['Smartphone', 'Featurephone', 'NoSmartphone']
    tokens = []

    for t in range(100):
        farmer_id = t
        farmer = f.name()
        has_rsvped = choice(True_False)
        leader = choice(Leaders)
        has_leader = True if leader else False
        locality = choice(Locality)
        phone_type = choice(Phone_Type)

        tokens.append({
            "id": farmer_id,
            "farmer": farmer,
            "has_rsvped": has_rsvped,
            "leader": leader,
            "has_leader": has_leader,
            "locality": locality,
            "smartphone_type": phone_type
        })
    
    return tokens

if __name__ == '__main__':
    tokens = get_data()
    print(tokens)