package ec.com.wego.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import ec.com.wego.app.R;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SweetAlertDialog pDialog;
    private String TAG = AddContactActivity.class.getName();
    private EditText txtNombre,txtTelefono;
    private static AppPreferences app;
    private int i=-1;
    private int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

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


        txtNombre = (EditText) findViewById(R.id.txtnombre);
        txtTelefono = (EditText) findViewById(R.id.txttelefono);
        app = new AppPreferences(getApplicationContext());



        Bundle bun = getIntent().getExtras();

        if(bun.containsKey("i")){

           i= Integer.parseInt(bun.getString("i"));

            txtNombre.setText(ContactActivity.mListContactos.get(i).getNombre());
            txtTelefono.setText(ContactActivity.mListContactos.get(i).getValor());
            id= ContactActivity.mListContactos.get(i).getId();
        }



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

    public void save(View v){

        if(txtNombre.getText().toString().trim().equals("")){
            txtNombre.setError(getString(R.string.error_contacto_nombre2));
            return;
        }
        if(txtTelefono.getText().toString().trim().equals(""))
        {
            txtTelefono.setError(getString(R.string.error_contacto_telefono2));
            return;
        }


        for (int x=0;x<ContactActivity.mListContactos.size();x++){

            if(ContactActivity.mListContactos.get(x).getNombre().equals(txtNombre.getText().toString().trim()) && ContactActivity.mListContactos.get(x).getId()!=id){


                    txtNombre.setError(getString(R.string.error_contacto_nombre));

                return;
            }

            if(ContactActivity.mListContactos.get(x).getValor().equals(txtTelefono.getText().toString().trim()) && ContactActivity.mListContactos.get(x).getId()!=id){
                txtTelefono.setError(getString(R.string.error_contacto_telefono));
                return;
            }

        }


        saveTask();

    }

    private void saveTask(){
        //Showing the progress dialog


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_contacto/format/json",
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
                                //JSONArray mObjResp = res.getJSONArray("data");



                                pDialog.dismiss();

                                pDialog= new SweetAlertDialog(AddContactActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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



                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(AddContactActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
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
                        Toast.makeText(AddContactActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.AESEncryptEntity(app.getUserId()));
                    params.put("nombre", Constants.AESEncryptEntity(txtNombre.getText().toString()));
                    params.put("valor", Constants.AESEncryptEntity(txtTelefono.getText().toString()));
                    params.put("origen_crea", Constants.AESEncryptEntity(Constants.getIPAddress(true)));
                    params.put("id", Constants.AESEncryptEntity(String.valueOf(id)));


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
