from rest_framework import serializers
from farmer.models import UserProfile, Farmer


class RegistrationSerializer(serializers.ModelSerializer):

    class Meta:
        model = UserProfile
        fields = ['username', 'category', 'password']
        extra_kwargs = {
            'password': {'write_only': True}
        }

    def save(self):
        User = UserProfile(
            username=self.validated_data['username'],
            category=self.validated_data['category'],
        )
        password = self.validated_data['password']


        User.set_password(password)
        User.save()
        return User


class FarmerSerializer(serializers.ModelSerializer):

    class Meta:
        model = Farmer
        fields = ['first_name', 'last_name', 'village', 'district', 'contact']

    def save(self, user):
        farmer = Farmer(
            user=user,
            first_name=self.validated_data['first_name'],
            last_name=self.validated_data['last_name'],
            district=self.validated_data['district'],
            village=self.validated_data['village'],
            contact=self.validated_data['contact'],
        )

        farmer.save()

