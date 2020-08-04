package com.example.ruralcaravan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ruralcaravan.R;
import com.example.ruralcaravan.Utilities.Constants;

public class GovtSchemeDetailsActivity extends AppCompatActivity {

    private TextView textViewGovtSchemeDetails;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_govt_scheme_details);
        textViewGovtSchemeDetails = findViewById(R.id.textViewGovtSchemeDetails);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.GOVT_SCHEME_NAME);
        String details = intent.getStringExtra(Constants.GOVT_SCHEME_DETAILS);
        textViewGovtSchemeDetails.setText(details);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
