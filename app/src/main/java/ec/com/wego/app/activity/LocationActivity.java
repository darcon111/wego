package ec.com.wego.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import ec.com.wego.app.clases.Contacts;
import ec.com.wego.app.clases.Locations;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LocationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mLocationRecyclerView;
    public static ArrayList<Locations> mListLocations;
    private LocationRecycleAdapter mLocationAdapter;

    private SweetAlertDialog pDialog;
    private AppPreferences appPreferences;

    private String TAG = ContactActivity.class.getName();

    private PopupMenu popupMenu;
    private int select=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

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

        appPreferences= new AppPreferences(LocationActivity.this);


        mLocationRecyclerView = (RecyclerView) findViewById(R.id.ubicacion);
        // Create a grid layout with two columns

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mLocationRecyclerView.setLayoutManager(layoutManager);
        mLocationAdapter = new LocationRecycleAdapter();
        mLocationRecyclerView.setAdapter(mLocationAdapter);

        mListLocations = new ArrayList<Locations>();


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {

                select = Integer.parseInt(extras.getString("select"));


            }
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
            case R.id.add:
                Intent intent = new Intent(LocationActivity.this,AddLocationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadTask(appPreferences.getUserId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* secondary menu*/
        getMenuInflater().inflate(R.menu.add, menu);
        //item = menu.findItem(R.id.action_save);
        return true;
    }


    private void loadTask(String persona_id){

        mListLocations.clear();

        final JSONObject[] res = {null};
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();



        final String finalPersona = persona_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getUbicaciones/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(Constants.Decrypt(res[0].getString("result")).equals("OK") ){




                                //Handler handler = new Handler(Looper.getMainLooper());
                                // handler.post(new Runnable() {
                                //   @Override
                                //   public void run() {

                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");




                                    for (int x =0; x< mObjResp[0].length(); x++)
                                    {
                                        JSONObject mObj = mObjResp[0].getJSONObject(x);


                                        final JSONObject finalMObj = mObj;
                                        final int finalX = x;
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    mListLocations.add(new Locations(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))),Constants.Decrypt(finalMObj.getString("nombre")),Constants.Decrypt(finalMObj.getString("latitud")), Constants.Decrypt(finalMObj.getString("longitud")),Constants.Decrypt(finalMObj.getString("direccion")),Constants.Decrypt(finalMObj.getString("piso")),Constants.Decrypt(finalMObj.getString("departamento")),Integer.parseInt(Constants.Decrypt(finalMObj.getString("is_principal"))) ));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                if(select==1){
                                                    select++;
                                                    pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.WARNING_TYPE);
                                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                                    pDialog.setContentText(getResources().getString(R.string.select_location));
                                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                        }
                                                    });
                                                    pDialog.show();
                                                }
                                                mLocationAdapter.notifyItemChanged(finalX);
                                                //Constants.deleteCache(LocationActivity.this);

                                            }
                                        });


                                    }





                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                pDialog.dismiss();

                                //  }
                                //});







                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
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
                        Toast.makeText(LocationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.Encrypt(finalPersona));
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



    private void primaryTask(final int i){
        //Showing the progress dialog


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_ubicacion_setprincipal/format/json",
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


                                for(int x=0;x<mListLocations.size();x++){

                                    if(mListLocations.get(x).getIs_principal()==1){
                                        mListLocations.get(x).setIs_principal(0);
                                        mLocationAdapter.notifyItemChanged(x);
                                    }

                                }

                                mListLocations.get(i).setIs_principal(1);
                                mLocationAdapter.notifyItemChanged(i);


                                pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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



                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(LocationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.Encrypt(appPreferences.getUserId()));
                    params.put("id", Constants.Encrypt(String.valueOf(mListLocations.get(i).getId())));
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




    private void deleteTask(final int i){
        //Showing the progress dialog


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_ubicacion_delete/format/json",
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


                                mListLocations.remove(i);
                                mLocationAdapter.notifyDataSetChanged();

                                pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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



                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(LocationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.Encrypt(appPreferences.getUserId()));
                    params.put("id", Constants.Encrypt(String.valueOf(mListLocations.get(i).getId())));
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


    /* adapter*/

    public class LocationRecycleAdapter extends RecyclerView.Adapter<LocationRecycleHolder> {
        private int lastPosition = -1;

        @Override
        public LocationRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ubicacion, viewGroup, false);
            setAnimation(v,i);
            return new LocationRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final LocationRecycleHolder productHolder, final int i) {

            productHolder.mtxtNombre.setText(mListLocations.get(i).getNombre());
            productHolder.mtxtValor.setText(mListLocations.get(i).getDireccion());

            if(mListLocations.get(i).getIs_principal()==1){
                productHolder.mStart.setVisibility(View.VISIBLE);
            }

            if(select==0) {


                productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(View v) {

                        if (popupMenu != null) {
                            popupMenu.dismiss();
                        }


                        popupMenu = new PopupMenu(v.getContext(), v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupMenu.setGravity(Gravity.CENTER);
                        }
                        if (mListLocations.get(i).getIs_principal() == 1) {
                            popupMenu.inflate(R.menu.menu2);
                        } else {
                            popupMenu.inflate(R.menu.menu);
                        }


                        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                            @Override
                            public void onDismiss(PopupMenu menu) {
                                popupMenu = null;
                            }
                        });


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.principal:

                                        primaryTask(i);
                                        return true;

                                    case R.id.editar:

                                        Intent intent = new Intent(LocationActivity.this, AddLocationActivity.class);
                                        intent.putExtra("i", String.valueOf(i));
                                        startActivity(intent);
                                        return true;

                                    case R.id.eliminar:

                                        if (mListLocations.get(i).getIs_principal() == 1) {
                                            pDialog = new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.ERROR_TYPE);
                                            pDialog.setTitleText(getResources().getString(R.string.app_name));
                                            pDialog.setContentText(getString(R.string.error_ubicacion_delete));
                                            pDialog.setConfirmText(getResources().getString(R.string.ok));
                                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();

                                                }
                                            });
                                            pDialog.show();

                                        } else {

                                            deleteTask(i);
                                        }
                                        return true;

                                    default:
                                        return false;
                                }


                            }
                        });


                        try {
                            Field[] fields = popupMenu.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                if ("mPopup".equals(field.getName())) {
                                    field.setAccessible(true);
                                    Object menuPopupHelper = field.get(popupMenu);
                                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                    setForceIcons.invoke(menuPopupHelper, true);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        popupMenu.show();


                    }
                });
            }else
            {
                productHolder.mImage.setVisibility(View.GONE);
            }

            if(select>=1){
                productHolder.mContenedor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("id_ubicacion",String.valueOf(mListLocations.get(i).getId()));
                        returnIntent.putExtra("ubicacion",String.valueOf(mListLocations.get(i).getNombre()));
                        setResult(Activity.RESULT_OK,returnIntent);


                    }
                });
            }




            setAnimation(productHolder.itemView, i);



        }


        @Override
        public int getItemCount() {
            return mListLocations.size();
        }

        public void removeItem(int position) {
            mListLocations.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListLocations.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(LocationActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(LocationActivity.this, R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class LocationRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mtxtNombre;
        public TextView mtxtValor;
        public ImageView mImage;
        public ImageView mStart;
        public CardView mContenedor;


        public LocationRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtValor = (TextView) itemView.findViewById(R.id.txtValor);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
            mStart = (ImageView) itemView.findViewById(R.id.start);
            mContenedor = (CardView) itemView.findViewById(R.id.contenedor);
        }
    }


}
