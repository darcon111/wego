package ec.com.wego.app.activity;

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
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.City;
import ec.com.wego.app.clases.EstadoCivil;
import ec.com.wego.app.clases.Identificacion;
import ec.com.wego.app.clases.ImagenCircular.CircleImageView;
import ec.com.wego.app.clases.User;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;

public class CalificarActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SweetAlertDialog pDialog;
    private String TAG = CalificarActivity.class.getName();
    private CircleImageView img;
    private TextView txtNombre;
    private DatabaseReference databaseUsers;
    public  User Utemp;
    private int puntual= 0,calidad = 0,rapidez = 0;
    private Button btnPuntual,btnCalidad,btnRapidez,btnSave;
    private RatingBar calificar;
    private String trabajador_id = "";
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);


        appPreferences= new AppPreferences(CalificarActivity.this);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.perfil));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        img = (CircleImageView) findViewById(R.id.img);
        txtNombre = (TextView) findViewById(R.id.txtNombre);

        btnPuntual = (Button) findViewById(R.id.btnPuntual);
        btnCalidad = (Button) findViewById(R.id.btnCalidad);
        btnRapidez = (Button) findViewById(R.id.btnRapidez);
        btnSave = (Button) findViewById(R.id.btnSave);

        calificar = (RatingBar) findViewById(R.id.calificar);

        btnPuntual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (puntual == 0)
                {
                    puntual = 1;
                    btnPuntual.setBackground(getResources().getDrawable(R.drawable.button1));
                }else
                {
                    puntual = 0;
                    btnPuntual.setBackground(getResources().getDrawable(R.drawable.button2));
                }

            }
        });

        btnCalidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (calidad == 0)
                {
                    calidad = 1;
                    btnCalidad.setBackground(getResources().getDrawable(R.drawable.button1));
                }else
                {
                    calidad = 0;
                    btnCalidad.setBackground(getResources().getDrawable(R.drawable.button2));
                }

            }
        });

        btnRapidez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rapidez == 0)
                {
                    rapidez = 1;
                    btnRapidez.setBackground(getResources().getDrawable(R.drawable.button1));
                }else
                {
                    rapidez = 0;
                    btnRapidez.setBackground(getResources().getDrawable(R.drawable.button2));
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if  (calificar.getRating()  == 0)
                {
                    pDialog = new SweetAlertDialog(CalificarActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getResources().getString(R.string.calificacion));
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
                    saveTask(getIntent().getStringExtra("id"));
                }

            }
        });






        databaseUsers = FirebaseDatabase.getInstance().getReference("users");


        dataTask(getIntent().getStringExtra("id"));

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

    private void dataTask(final String idorden){
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"dataCalificar/format/json",
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



                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            String nombres = Constants.Decrypt(mObj.getString("nombres"));
                                            String apellidos = Constants.Decrypt(mObj.getString("apellidos"));
                                            String email = Constants.Decrypt(mObj.getString("email"));
                                            trabajador_id = Constants.Decrypt(mObj.getString("persona_trabajador_id"));
                                            String imagen = mObj.getString("imagen");


                                            if(! imagen.trim().equals(""))
                                            {
                                                Glide.with(getApplicationContext())
                                                        .load(imagen)
                                                        .fitCenter()
                                                        .into(img);
                                            }else{

                                                Query userquery = databaseUsers
                                                        .orderByChild("email").equalTo(email);


                                                userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                            Utemp = postSnapshot.getValue(User.class);
                                                            databaseUsers.removeEventListener(this);





                                                        }

                                                        if(Utemp.getUrl_imagen()!=null){
                                                            Glide.with(getApplicationContext())
                                                                    .load(Utemp.getUrl_imagen())
                                                                    .fitCenter()
                                                                    .into(img);
                                                        }



                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                                    }
                                                });

                                            }

                                            txtNombre.setText(nombres + " " + apellidos);






                                            pDialog.dismiss();




                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });







                                pDialog.dismiss();





                            }else
                            {
                                pDialog.dismiss();


                                pDialog = new SweetAlertDialog(CalificarActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        finish();
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
                        Toast.makeText(CalificarActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("idorden", Constants.Encrypt(idorden));
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


    private void saveTask(final String idorden){

        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"saveCalificar/format/json",
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

                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(CalificarActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res.getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                       finish();

                                    }
                                });
                                pDialog.show();


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(CalificarActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res.getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        finish();
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
                        Toast.makeText(CalificarActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("idorden", Constants.Encrypt(idorden));
                    params.put("userid", Constants.Encrypt(appPreferences.getUserId()));
                    params.put("puntual", Constants.Encrypt(String.valueOf(puntual)));
                    params.put("calidad", Constants.Encrypt(String.valueOf(calidad)));
                    params.put("rapidez", Constants.Encrypt(String.valueOf(rapidez)));
                    params.put("calificacion", Constants.Encrypt(String.valueOf(calificar.getNumStars())));
                    params.put("origen_crea", Constants.Encrypt(Constants.getIPAddress(true)));

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


}
