package com.wego.app.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
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
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wego.app.R;
import com.wego.app.clases.Ciudad;
import com.wego.app.clases.EstadoCivil;
import com.wego.app.clases.Identificacion;
import com.wego.app.clases.ImagenCircular.CircleImageView;
import com.wego.app.clases.Spinner.MaterialSpinner;
import com.wego.app.config.AppPreferences;
import com.wego.app.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vansuita.pickimage.listeners.IPickResult;
import com.wego.app.holder.Categories;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PerfilActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,IPickResult{

    private String TAG = PerfilActivity.class.getName();
    private static AppPreferences app;

    private EditText txtIdentificacion,txtNombres,txtApellidos,txtFecha,txttelefono,txtpass,txtnewpass;

    private TextInputLayout textInputLayout8,textInputLayout7;

    private CircleImageView img;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private File outPutFile = null;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;
    private String image = "";
    private Button save;
    private SweetAlertDialog pDialog;
    private MaterialSpinner tipo_identificacion,estado_civil,ciudad,genero;



    private ArrayAdapter<String> dataAdapter_tipo_identificacion;
    private ArrayAdapter<String> dataAdapter_ciudad;
    private ArrayAdapter<String> dataAdapter_estado;
    private ArrayAdapter<String> dataAdapter_genero;


    private ArrayList<String> list_tipo_identificacion;
    private ArrayList<String> list_estado_civil;
    private ArrayList<String> list_cuidad;
    private ArrayList<String> list_genero;


    private ArrayList<EstadoCivil> arrayEstadoCivil;
    private ArrayList<Ciudad> arrayCiudad;
    private ArrayList<Identificacion> arrayIdentificacion;

    private int select_tipo_identificacion=0;
    private int select_ciudad=0;
    private int select_estado=0;
    private int select_genero=0;


    private Toolbar toolbar;

    private FirebaseUser user;

    private String telefono= "";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks= null;

    private FrameLayout fragment;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_perfil);


        user = FirebaseAuth.getInstance().getCurrentUser();

        app = new AppPreferences(getApplicationContext());


        List<String> listProvider =user.getProviders();
        if(!listProvider.get(0).equals("password")){

            textInputLayout7 = (TextInputLayout) findViewById(R.id.textInputLayout7);
            textInputLayout8 = (TextInputLayout) findViewById(R.id.textInputLayout8);
            textInputLayout7.setVisibility(View.GONE);
            textInputLayout8.setVisibility(View.GONE);

        }



        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        txtIdentificacion= (EditText) findViewById(R.id.txtIdentificacion);
        txtNombres= (EditText) findViewById(R.id.txtNombres);
        txtApellidos= (EditText) findViewById(R.id.txtApellidos);
        txtFecha= (EditText) findViewById(R.id.txtFecha);
        txttelefono= (EditText) findViewById(R.id.txttelefono);
        txtpass= (EditText) findViewById(R.id.txtpass);
        txtnewpass= (EditText) findViewById(R.id.txtnewpass);
        fragment= (FrameLayout) findViewById(R.id.fragment);



        img=(CircleImageView) findViewById(R.id.imgPerfil);

        tipo_identificacion=(MaterialSpinner) findViewById(R.id.tipo_identificacion);
        estado_civil=(MaterialSpinner) findViewById(R.id.estado_civil);
        ciudad=(MaterialSpinner) findViewById(R.id.ciudad);
        genero=(MaterialSpinner) findViewById(R.id.genero);

        save=(Button) findViewById(R.id.save);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*if(tipo_identificacion.getSelectedItemPosition()==0){

                    tipo_identificacion.setError(getResources().getString(R.string.error_tipo_identificacion));
                    return;

                }*/


                /*if(txtIdentificacion.getText().toString().trim().equals("")){

                    txtIdentificacion.setError(getResources().getString(R.string.error_identificacion));
                    return;

                }

                if(genero.getSelectedItemPosition()==0){

                    genero.setError(getResources().getString(R.string.error_genero));
                    return;

                }*/



                if(txtNombres.getText().toString().trim().equals("")){

                    txtNombres.setError(getResources().getString(R.string.error_nombres));
                    return;

                }


                if(txtApellidos.getText().toString().trim().equals("")){

                    txtApellidos.setError(getResources().getString(R.string.error_apellido));
                    return;

                }

                if(txttelefono.getText().toString().trim().equals("")){

                    txttelefono.setError(getResources().getString(R.string.error_telefono));
                    return;

                }

                /*if(estado_civil.getSelectedItemPosition()==0){

                    estado_civil.setError(getResources().getString(R.string.error_estado_civil));
                    return;

                }

                if(ciudad.getSelectedItemPosition()==0){

                    ciudad.setError(getResources().getString(R.string.error_ciudad));
                    return;

                }*/



                if(txtFecha.getText().toString().trim().equals("")){

                    txtFecha.setError(getResources().getString(R.string.error_fecha));
                    return;

                }



                if(!txtpass.getText().toString().equals(""))
                {
                    if(txtnewpass.getText().toString().trim().length()<6)
                    {
                        txtnewpass.setError(getResources().getString(R.string.error_newpass));
                        return;
                    }
                }else
                {
                    if(!txtnewpass.getText().toString().trim().equals("") && txtpass.getText().toString().equals("")){
                        txtpass.setError(getResources().getString(R.string.error_pass));
                        return;
                    }

                }


                if(!txtpass.getText().toString().trim().equals("") && !txtnewpass.getText().toString().trim().equals(""))
                {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), txtpass.getText().toString().trim());


                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(txtnewpass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Password updated");

                                                    saveTask(image);


                                                } else {

                                                    pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                                    pDialog.setContentText(getResources().getString(R.string.error_newpass_update));
                                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            return;

                                                        }
                                                    });
                                                    pDialog.show();

                                                }
                                            }
                                        });
                                    } else {
                                        pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                                        pDialog.setContentText(getResources().getString(R.string.error_newpass_update));
                                        pDialog.setConfirmText(getResources().getString(R.string.ok));
                                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                return;

                                            }
                                        });
                                        pDialog.show();
                                    }
                                }
                            });


                }else
                {

                    saveTask(image);


                }




            }
        });



        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        txtFecha.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            PerfilActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
                Log.i("click text", "kakak");
                return false;
            }
        });


    }


    private void carga_combo()
    {


        if(list_tipo_identificacion.size()>0)
        {
            // Creating adapter for spinner
            dataAdapter_tipo_identificacion= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_tipo_identificacion);

            // Drop down layout style - list view with radio button
            dataAdapter_tipo_identificacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            tipo_identificacion.setAdapter(dataAdapter_tipo_identificacion);



            tipo_identificacion.setSelection(select_tipo_identificacion+1);


        }



                        if(list_estado_civil.size()>0)
                        {
                            // Creating adapter for spinner
                            dataAdapter_estado = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_estado_civil);

                            // Drop down layout style - list view with radio button
                            dataAdapter_estado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            estado_civil.setAdapter(dataAdapter_estado);



                                estado_civil.setSelection(select_estado+1);


                        }

                        if(list_cuidad.size()>0)
                        {
                            // Creating adapter for spinner
                            dataAdapter_ciudad= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_cuidad);

                            // Drop down layout style - list view with radio button
                            dataAdapter_ciudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            ciudad.setAdapter(dataAdapter_ciudad);



                                ciudad.setSelection(select_ciudad+1);

                        }
                        list_genero = new ArrayList<String>();
                        list_genero.add("Masculino");
                        list_genero.add("Femenino");

                        // Creating adapter for spinner
                        dataAdapter_genero= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_genero);

                        // Drop down layout style - list view with radio button
                        dataAdapter_genero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        genero.setAdapter(dataAdapter_genero);



                        genero.setSelection(select_genero+1);





    }


    private void dataTask(){
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getDataPerfil/format/json",
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

                                            txtNombres.setText(Constants.AESDecryptEntity(mObj.getString("nombres")));
                                            txtApellidos.setText(Constants.AESDecryptEntity(mObj.getString("apellidos")));
                                            txtFecha.setText(Constants.AESDecryptEntity(mObj.getString("fecha_nacimiento")));
                                            txtIdentificacion.setText(Constants.AESDecryptEntity(mObj.getString("identificacion")));

                                            telefono =  Constants.AESDecryptEntity(mObj.getString("telefono"));
                                            txttelefono.setText(telefono);

                                            select_tipo_identificacion = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("tipo_identificacion")));
                                            select_estado = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("estado_civil")));
                                            select_ciudad = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("ciudad_id")));
                                            select_genero = Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("genero")));




                                            String imagen= mObj.getString("imagen");

                                            if(!imagen.trim().equals(""))
                                            {
                                                Glide.with(getApplicationContext())
                                                        .load(imagen)
                                                        .fitCenter()
                                                        .into(img);
                                            }









                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        JSONArray list = null;
                                        try {
                                            list = new JSONArray(mObj.getString("list_tipo_identificacion"));

                                        list_tipo_identificacion = new ArrayList<String>();
                                        arrayIdentificacion = new ArrayList<Identificacion>();

                                        for (int x=0;x<list.length();x++)
                                        {
                                            JSONObject temp= list.getJSONObject(x);

                                            list_tipo_identificacion.add(Constants.AESDecryptEntity(temp.getString("nombre")));
                                            arrayIdentificacion.add(new Identificacion(Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))),Constants.AESDecryptEntity(temp.getString("nombre"))));

                                            if (Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))) == select_tipo_identificacion)
                                            {
                                                select_tipo_identificacion = x;
                                            }

                                        }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                        JSONArray list2 = new JSONArray(mObj.getString("list_estado_civil"));
                                        list_estado_civil = new ArrayList<String>();
                                        arrayEstadoCivil = new ArrayList<EstadoCivil>();
                                        for (int x=0;x<list2.length();x++)
                                        {
                                            JSONObject temp = list2.getJSONObject(x);

                                            list_estado_civil.add(Constants.AESDecryptEntity(temp.getString("nombre")));
                                            arrayEstadoCivil.add(new EstadoCivil(Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))),Constants.AESDecryptEntity(temp.getString("nombre"))));

                                            if (Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))) == select_estado)
                                            {
                                                select_estado = x;
                                            }



                                        }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }



                                    }
                                });



                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                            JSONArray list3 = new JSONArray(mObj.getString("list_ciudad"));
                                            list_cuidad = new ArrayList<String>();
                                            arrayCiudad = new ArrayList<Ciudad>();
                                            for (int x=0;x<list3.length();x++)
                                            {
                                                JSONObject temp = list3.getJSONObject(x);

                                                list_cuidad.add(Constants.AESDecryptEntity(temp.getString("nombre")));
                                                arrayCiudad.add(new Ciudad(Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))),Constants.AESDecryptEntity(temp.getString("nombre"))));

                                                if (Integer.parseInt(Constants.AESDecryptEntity(temp.getString("id"))) == select_ciudad)
                                                {
                                                    select_ciudad = x;
                                                }

                                            }

                                            carga_combo();




                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }



                                    }
                                });

                                pDialog.dismiss();





                            }else
                            {
                                pDialog.dismiss();


                                pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(PerfilActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


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
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }



    private void saveTask(final String image){
        //Showing the progress dialog


        /*if(!telefono.equals(txttelefono.getText().toString().trim()))
        {

            fragment.setVisibility(View.VISIBLE);

            mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    Log.d("JEJE", "onVerificationCompleted:" + phoneAuthCredential);


                    fragment.setVisibility(View.GONE);


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.w("JEJE", "onVerificationFailed", e);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.d("JEJE", "INVALID REQUEST");
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Log.d("JEJE", "Too many Request");
                    }
                }
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    Log.d("JEJE", "onCodeSent:" + s);
                    //mResendToken = forceResendingToken;
                    loadVerification(s, txttelefono.getText().toString().trim());
                }
            };


            verifyPhone("+593985086078",mCallBacks);
        }*/


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"saveImagen/format/json",
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

                                app.setUser(txtNombres.getText().toString().trim()+" "+ txtApellidos.getText().toString().trim());

                                app.setActualizar("1");

                                pDialog.dismiss();

                                pDialog= new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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


                                pDialog= new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(PerfilActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters


                params.put("image", image);
                try {
                    params.put("userid", Constants.AESEncryptEntity(app.getUserId()));
                    params.put("nombres", Constants.AESEncryptEntity(txtNombres.getText().toString()));
                    params.put("apellidos", Constants.AESEncryptEntity(txtApellidos.getText().toString()));
                    //params.put("identificacion", Constants.AESEncryptEntity(txtIdentificacion.getText().toString()));
                    params.put("fecha_nacimiento", Constants.AESEncryptEntity(txtFecha.getText().toString()));
                    params.put("telefono", Constants.AESEncryptEntity(txttelefono.getText().toString()));

                    //params.put("tipo_identificacion", Constants.AESEncryptEntity(tipo_identificacion));
                    // params.put("estado_civil", Constants.AESEncryptEntity(estado_civil));
                    // params.put("ciudad_id", Constants.AESEncryptEntity(ciudad));

                } catch (Exception e) {
                    e.printStackTrace();
                }



                //returning parameters
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();






    }

    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallback
    }



    public void loadVerification(String codeID, String phone){
        Verification verification = new Verification();
        Bundle args = new Bundle();
        args.putString(Verification.ARGS_PHONE, phone);
        args.putString(Verification.ARGS_VER_CODE, codeID);
        verification.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, verification).commit();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;
        txtFecha.setText(date);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_FROM_FILE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            //bitmap = ProcessImage.compressImage(filePath, getApplicationContext(), null);
            //Getting the Bitmap from Gallery
            performCrop(filePath);

        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {

            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(PerfilActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            performCrop(uri);
                        }
                    });


        }

        if(requestCode==CROP_FROM_CAMERA) {
            try {
                if(outPutFile.exists()){
                    //bitmap = decodeFile(outPutFile);

                    InputStream ims = new FileInputStream(outPutFile);
                    bitmap= BitmapFactory.decodeStream(ims);

                    //imagen.setImageBitmap(bitmap);

                    image = Constants.getStringImage(bitmap);
                    img.setImageBitmap(bitmap);

                    //imagen.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void showFileChooser() {

        PickImageDialog.build(new PickSetup()
                .setTitle(getResources().getString(R.string.image))
                .setTitleColor(getResources().getColor(R.color.colorPrimaryText))
                .setCameraButtonText(getResources().getString(R.string.camera))
                .setGalleryButtonText(getResources().getString(R.string.sd))
                .setButtonTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setBackgroundColor(getResources().getColor(R.color.colorIcons))
                .setCancelText(getResources().getString(R.string.cancelar))
                .setCancelTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setGalleryIcon(R.drawable.ic_perm_media_black_24dp)
                .setCameraIcon(R.drawable.ic_photo_camera_black_24dp)

        ).show(getSupportFragmentManager());


    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),  r.getBitmap(), "temp", null);
            performCrop(Uri.parse(path));

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void performCrop(Uri uri) {

        int x=dpToPx(280);
        int y=dpToPx(280);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", x);
        intent.putExtra("outputY", y);
        //intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);
        //Create output file here
        try {
            /*mImageCaptureUri = FileProvider.getUriForFile(AddPlatoActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());*/
            outPutFile =createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }



        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
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

    @Override
    public void onResume() {
        super.onResume();

        dataTask();

    }


}
