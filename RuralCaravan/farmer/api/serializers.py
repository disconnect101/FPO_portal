from rest_framework import serializers
from farmer.models import UserProfile, Farmer


class RegistrationSerializer(serializers.ModelSerializer):
    password2 = serializers.CharField(style={'input_type': 'password'}, write_only=True)

    class Meta:
        model = UserProfile
        fields = ['username', 'category', 'password', 'password2']
        extra_kwargs = {
            'password': {'write_only': True}
        }

    def save(self):
        User = UserProfile(
            username=self.validated_data['username'],
            category=self.validated_data['category'],
        )
        password = self.validated_data['password']
        password2 = self.validated_data['password2']

        if password!=password2:
            raise serializers.ValidationError({'error': 'Passwords must match'})

        User.set_password(password)
        User.save()
        return User


class FarmerSerializer(serializers.ModelSerializer):

    class Meta:
        model = Farmer
        fields = ['first_name', 'last_name', 'village', 'photo']


