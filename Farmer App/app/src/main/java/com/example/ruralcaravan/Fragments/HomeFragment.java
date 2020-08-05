package com.example.ruralcaravan.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.ruralcaravan.Activities.LeaderAccessActivity;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.google.android.material.navigation.NavigationView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private CardView cardViewEWallet;
    private CardView cardViewCatalogue;
    private CardView cardViewMeetings;
    private CardView cardViewNews;
    private CardView cardViewYourOrders;
    private CardView cardViewCart;
    private CardView cardViewWeather;
    private CardView cardViewPlans;
    private CardView cardViewLeaderAccess;

    private NavigationView navigationView;
    private Menu menu;
    private CarouselView carouselView;

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
        cardViewLeaderAccess = rootView.findViewById(R.id.cardViewLeaderAccess);

        cardViewEWallet.setOnClickListener(this);
        cardViewCatalogue.setOnClickListener(this);
        cardViewMeetings.setOnClickListener(this);
        cardViewNews.setOnClickListener(this);
        cardViewYourOrders.setOnClickListener(this);
        cardViewCart.setOnClickListener(this);
        cardViewWeather.setOnClickListener(this);
        cardViewPlans.setOnClickListener(this);
        cardViewLeaderAccess.setOnClickListener(this);

        navigationView = getActivity().findViewById(R.id.navigationView);
        menu = navigationView.getMenu();

        if(SharedPreferenceUtils.isLeader(getActivity())) {
            cardViewLeaderAccess.setVisibility(View.VISIBLE);
        } else {
            cardViewLeaderAccess.setVisibility(View.GONE);
        }

        carouselView = rootView.findViewById(R.id.carouselView);
        final int[] carouselImages = {R.drawable.carousel1, R.drawable.carousel2, R.drawable.carousel3, R.drawable.carousel4};
        carouselView.setPageCount(carouselImages.length);
        ImageListener carouselImageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(carouselImages[position]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        };
        carouselView.setImageListener(carouselImageListener);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        int menuItemId;
        switch(v.getId()) {
            case R.id.cardViewEWallet:
                fragment = new BalanceSheetFragment();
                menuItemId = R.id.eWallet;
                break;
            case R.id.cardViewCatalogue:
                fragment = new CatalogueCategoryFragment();
                menuItemId = R.id.catalogue;
                break;
            case R.id.cardViewMeetings:
                fragment = new MeetingsFragment();
                menuItemId = R.id.meetings;
                break;
            case R.id.cardViewNews:
                fragment = new GovtSchemesFragment();
                menuItemId = R.id.govt_schemes;
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
                fragment = new ListPlansFragment();
                menuItemId = R.id.plans;
                break;
            case R.id.cardViewLeaderAccess:
                fragment = null;
                menuItemId = 0;
                moveToLeaderAccess();
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

    public void moveToLeaderAccess() {
        Intent intent = new Intent(getActivity(), LeaderAccessActivity.class);
        startActivity(intent);
    }

}
