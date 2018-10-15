package ec.com.wego.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.City;
import ec.com.wego.app.clases.EstadoCivil;
import ec.com.wego.app.clases.Identificacion;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;


public class GetServicies2Activity extends AppCompatActivity {

    private AppPreferences app;
    private Toolbar toolbar;
    private String TAG = GetServiciesActivity.class.getName();
    private Button btnLocation,btnConctat,btnPhoto,btncontinuar;
    private TextView txtDia, txtRango;
    private SweetAlertDialog pDialog;
    private int id_ubicacion=0;
    private int id_contacto=0;
    private int ubicacion = 205;
    private int contacto = 206;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_servicies2);

        /* set orientation*/

        app = new AppPreferences(GetServicies2Activity.this);

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
        btncontinuar=(Button) findViewById(R.id.btncontinuar);

        txtDia=(TextView) findViewById(R.id.txtDia);
        txtRango=(TextView) findViewById(R.id.txtRango);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                txtRango.setText(txtRango.getText().toString()+" : "+ extras.getString("hora"));
                txtDia.setText(txtDia.getText().toString()+" : "+ extras.getString("fecha"));
            }
        }




        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GetServicies2Activity.this,LocationActivity.class);
                intent.putExtra("select","1");
                startActivityForResult(intent, ubicacion);

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

                Intent intent = new Intent(GetServicies2Activity.this,ContactActivity.class);
                intent.putExtra("select","1");
                startActivityForResult(intent, contacto);
            }
        });

        dataTask();

        btncontinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                if(id_contacto==0)
                {
                    message += getResources().getString(R.string.select_location2)+ "\n";
                }
                if(id_ubicacion==0)
                {
                    message += getResources().getString(R.string.select_contacto2)+ "\n";
                }
                if(PhotoActivity.image1.equals("") || PhotoActivity.image1.equals("")){
                    message += getResources().getString(R.string.select_imagen);
                }
                if(!message.equals("")){
                    pDialog = new SweetAlertDialog(GetServicies2Activity.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(message);
                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    });
                    pDialog.show();
                }else
                {
                    pDialog = new SweetAlertDialog(GetServicies2Activity.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText("");
                    pDialog.setConfirmText("falta");
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    });
                    pDialog.show();
                }

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

    private void dataTask(){
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getVisita/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response

                        JSONObject res= null;
                        try {
                            res = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            if(res.getString("result").equals("OK") ){
                                JSONArray mObjResp = res.getJSONArray("data");
                                final JSONObject mObj = mObjResp.getJSONObject(0);



                                show(mObj);

                                pDialog.dismiss();

                            }else
                            {
                                pDialog.dismiss();

                                JSONArray mObjResp = res.getJSONArray("data");
                                final JSONObject mObj = mObjResp.getJSONObject(0);
                                show(mObj);

                                pDialog = new SweetAlertDialog(GetServicies2Activity.this, SweetAlertDialog.WARNING_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                    }
                                });
                                pDialog.show();




                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            Log.d(TAG, e.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        pDialog.dismiss();

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(GetServicies2Activity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid", Constants.AESEncryptEntity(app.getUserId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //returning parameters
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ubicacion) {
            if(resultCode == Activity.RESULT_OK){
                id_ubicacion= Integer.parseInt(data.getStringExtra("id_ubicacion"));
                btnLocation.setText(data.getStringExtra("ubicacion"));
            }

        }else{
            if(resultCode == Activity.RESULT_OK){
                id_contacto= Integer.parseInt(data.getStringExtra("id_contacto"));
                btnConctat.setText(data.getStringExtra("contacto"));
            }

        }
    }//onActivityResult

    private void show(final JSONObject mObj){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {


                   if(!mObj.getString("id_contacto").equals("")) {
                       btnConctat.setText(Constants.AESDecryptEntity(mObj.getString("nombre_contacto")));
                       id_contacto = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("id_contacto")));
                   }
                    if(!mObj.getString("id_ubicacion").equals("")) {
                        id_ubicacion = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("id_ubicacion")));
                        btnLocation.setText(Constants.AESDecryptEntity(mObj.getString("nombre_ubicacion")));
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
