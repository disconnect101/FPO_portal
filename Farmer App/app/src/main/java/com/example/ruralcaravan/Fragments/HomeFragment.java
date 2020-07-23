package com.example.ruralcaravan.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.ruralcaravan.R;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private CardView cardViewEWallet;
    private CardView cardViewCatalogue;
    private CardView cardViewMeetings;
    private CardView cardViewNews;
    private CardView cardViewYourOrders;
    private CardView cardViewCart;
    private CardView cardViewWeather;
    private CardView cardViewPlans;

    private NavigationView navigationView;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardViewEWallet = rootView.findViewById(R.id.cardViewEWallet);
        cardViewCatalogue = rootView.findViewById(R.id.cardViewCatalogue);
        cardViewMeetings = rootView.findViewById(R.id.cardViewMeetings);
        cardViewNews = rootView.findViewById(R.id.cardViewNews);
        cardViewYourOrders = rootView.findViewById(R.id.cardViewYourOrders);
        cardViewCart = rootView.findViewById(R.id.cardViewYourCart);
        cardViewWeather = rootView.findViewById(R.id.cardViewWeather);
        cardViewPlans = rootView.findViewById(R.id.cardViewPlans);

        cardViewEWallet.setOnClickListener(this);
        cardViewCatalogue.setOnClickListener(this);
        cardViewMeetings.setOnClickListener(this);
        cardViewNews.setOnClickListener(this);
        cardViewYourOrders.setOnClickListener(this);
        cardViewCart.setOnClickListener(this);
        cardViewWeather.setOnClickListener(this);
        cardViewPlans.setOnClickListener(this);

        navigationView = getActivity().findViewById(R.id.navigationView);
        menu = navigationView.getMenu();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        int menuItemId;
        switch(v.getId()) {
            case R.id.cardViewEWallet:
                fragment = new EWalletFragment();
                menuItemId = R.id.eWallet;
                break;
            case R.id.cardViewCatalogue:
                fragment = new CatalogueFragment();
                menuItemId = R.id.catalogue;
                break;
            case R.id.cardViewMeetings:
                fragment = new MeetingsFragment();
                menuItemId = R.id.meetings;
                break;
            case R.id.cardViewNews:
                fragment = new NewsFragment();
                menuItemId = R.id.news;
                break;
            case R.id.cardViewYourOrders:
                fragment = new YourOrdersFragment();
                menuItemId = R.id.yourOrders;
                break;
            case R.id.cardViewYourCart:
                fragment = new YourCartFragment();
                menuItemId = R.id.yourCart;
                break;
            case R.id.cardViewWeather:
                fragment = new WeatherFragment();
                menuItemId = R.id.weather;
                break;
            case R.id.cardViewPlans:
                fragment = new PlansFragment();
                menuItemId = R.id.plans;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        if(fragment != null) {
                getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
                MenuItem menuItem = menu.findItem(menuItemId);
                menuItem.setChecked(true);
                getActivity().setTitle(menuItem.getTitle());
        } else {
            Log.e("HomeFragment", "Error");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


}
