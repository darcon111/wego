package com.wego.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wego.app.R;
import com.wego.app.config.AppPreferences;


public class GetServicies2Activity extends AppCompatActivity {

    private AppPreferences appPreferences;
    private Toolbar toolbar;
    private String TAG = GetServiciesActivity.class.getName();
    private Button btnLocation,btnConctat,btnPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_servicies2);

        /* set orientation*/

        appPreferences = new AppPreferences(GetServicies2Activity.this);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }


        btnLocation=(Button) findViewById(R.id.btnLocation);
        btnPhoto=(Button) findViewById(R.id.btnPhoto);
        btnConctat=(Button) findViewById(R.id.btnConctat);




        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GetServicies2Activity.this,MyLocationsActivity.class);
                startActivity(intent);
            }
        });


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GetServicies2Activity.this,PhotoActivity.class);
                startActivity(intent);
            }
        });


        btnConctat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GetServicies2Activity.this,MyLocationsActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
