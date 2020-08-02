package com.example.ruralcaravan.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ruralcaravan.Fragments.CatalogueCategoryFragment;
import com.example.ruralcaravan.Fragments.EWalletFragment;
import com.example.ruralcaravan.Fragments.HomeFragment;
import com.example.ruralcaravan.Fragments.ListPlansFragment;
import com.example.ruralcaravan.Fragments.MeetingsFragment;
import com.example.ruralcaravan.Fragments.NewsFragment;
import com.example.ruralcaravan.Fragments.WeatherFragment;
import com.example.ruralcaravan.Fragments.YourCartFragment;
import com.example.ruralcaravan.Fragments.YourOrdersFragment;
import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.Constants;
import com.example.ruralcaravan.Utilities.SharedPreferenceUtils;
import com.example.ruralcaravan.Utilities.VolleySingleton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private ACProgressFlower dialog;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        doubleBackToExitPressedOnce = false;

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView = findViewById(R.id.navigationView);
        setupDrawerContent(navigationView);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();

        SharedPreferenceUtils.setActivityState(MainActivity.this, Constants.ACTIVITY_HOME);

        if(SharedPreferenceUtils.isUserDataRequired(MainActivity.this)) {
            dialog = new ACProgressFlower.Builder(MainActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Loading")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();
            String getUserDataUrl = getResources().getString(R.string.base_end_point_ip) + "userdata/";
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferenceUtils.setUserData(MainActivity.this, response.toString());
                    Toast.makeText(MainActivity.this,
                            "Hello " + SharedPreferenceUtils.getUserData(MainActivity.this).getFirstName(),
                            Toast.LENGTH_LONG);
                    dialog.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            };
            JsonObjectRequest userDataRequest = new JsonObjectRequest(Request.Method.GET, getUserDataUrl, null, responseListener, errorListener){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization", "Token " + SharedPreferenceUtils.getToken(MainActivity.this));
                    return params;
                }
            };
            VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(userDataRequest);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.eWallet:
                switchToNewFragment(EWalletFragment.class, menuItem);
                break;
            case R.id.catalogue:
                switchToNewFragment(CatalogueCategoryFragment.class, menuItem);
                break;
            case R.id.meetings:
                switchToNewFragment(MeetingsFragment.class, menuItem);
                break;
            case R.id.news:
                switchToNewFragment(NewsFragment.class, menuItem);
                break;
            case R.id.yourOrders:
                switchToNewFragment(YourOrdersFragment.class, menuItem);
                break;
            case R.id.yourCart:
                switchToNewFragment(YourCartFragment.class, menuItem);
                break;
            case R.id.weather:
                switchToNewFragment(WeatherFragment.class, menuItem);
                break;
            case R.id.plans:
                switchToNewFragment(ListPlansFragment.class, menuItem);
                break;
            case R.id.contact:
                contactFPO(menuItem);
                break;
            case R.id.logOut:
                logOut();
                break;
            default:
                switchToNewFragment(HomeFragment.class, menuItem);
        }
    }

    private void switchToNewFragment(Class fragmentClass, MenuItem menuItem) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    private void contactFPO(MenuItem menuItem) {
        //TODO: Add FPO contact number
        String url = "tel:1234567890";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        menuItem.setChecked(false);
    }

    private void logOut() {
        SharedPreferenceUtils.clearUserData(MainActivity.this, true);
        Intent intent = new Intent(MainActivity.this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        if(SharedPreferenceUtils.isLeader(MainActivity.this)) {
            menu.findItem(R.id.leaderAccess).setVisible(true);
        } else {
            menu.findItem(R.id.leaderAccess).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.profile: {
                Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.notification: {
                Toast.makeText(MainActivity.this, SharedPreferenceUtils.getUserData(MainActivity.this).getFirstName(), Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.leaderAccess: {
                Intent intent = new Intent(MainActivity.this, LeaderAccessActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (currentFragment instanceof HomeFragment) {
                if(doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            } else {
                setTitle(R.string.home);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
            }
        }
    }

}
