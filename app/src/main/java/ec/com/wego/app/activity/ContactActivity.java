package ec.com.wego.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.Contacts;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;
import ec.com.wego.app.holder.Categories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mContactoRecyclerView;
    public static ArrayList<Contacts> mListContactos;
    private ContactoRecycleAdapter mContactoAdapter;

    private SweetAlertDialog pDialog;
    private AppPreferences appPreferences;

    private String TAG = ContactActivity.class.getName();

    private PopupMenu popupMenu;
    private int select=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


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

        appPreferences= new AppPreferences(ContactActivity.this);


        mContactoRecyclerView = (RecyclerView) findViewById(R.id.contacto);
        // Create a grid layout with two columns

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mContactoRecyclerView.setLayoutManager(layoutManager);
        mContactoAdapter = new ContactoRecycleAdapter();
        mContactoRecyclerView.setAdapter(mContactoAdapter);

        mListContactos = new ArrayList<Contacts>();


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
                    Intent intent = new Intent(ContactActivity.this,AddContactActivity.class);
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

        mListContactos.clear();

        final JSONObject[] res = {null};
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(ContactActivity.this);

        final String finalPersona = persona_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getContacto/format/json",
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

                            if(Constants.AESDecryptEntity(res[0].getString("result")).equals("OK") ){




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
                                                    mListContactos.add(new Contacts(Integer.parseInt(Constants.AESDecryptEntity(finalMObj.getString("id"))),Constants.AESDecryptEntity(finalMObj.getString("tipo_contacto")),Constants.AESDecryptEntity(finalMObj.getString("valor")), Constants.AESDecryptEntity(finalMObj.getString("nombre")),Integer.parseInt(Constants.AESDecryptEntity(finalMObj.getString("is_principal"))) ));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                //mensaje que viene del select servicio
                                                if(select==1){
                                                    pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.WARNING_TYPE);
                                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                                    pDialog.setContentText(getResources().getString(R.string.select_contacto));
                                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                        }
                                                    });
                                                    pDialog.show();

                                                    select++;

                                                }

                                                mContactoAdapter.notifyItemChanged(finalX);




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


                                pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.AESDecryptEntity(res[0].getString("message")));
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
                        Toast.makeText(ContactActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.AESEncryptEntity(finalPersona));
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

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_contacto_setprincipal/format/json",
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


                                for(int x=0;x<mListContactos.size();x++){

                                    if(mListContactos.get(x).getIs_principal()==1){
                                        mListContactos.get(x).setIs_principal(0);
                                        mContactoAdapter.notifyItemChanged(x);
                                    }

                                }

                                mListContactos.get(i).setIs_principal(1);
                                mContactoAdapter.notifyItemChanged(i);


                                pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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


                                pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(ContactActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.AESEncryptEntity(appPreferences.getUserId()));
                    params.put("id", Constants.AESEncryptEntity(String.valueOf(mListContactos.get(i).getId())));
                    params.put("origen_crea", Constants.AESEncryptEntity(Constants.getIPAddress(true)));


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

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_contacto_delete/format/json",
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


                                mListContactos.remove(i);
                                mContactoAdapter.notifyDataSetChanged();

                                pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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


                                pDialog= new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(ContactActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.AESEncryptEntity(appPreferences.getUserId()));
                    params.put("id", Constants.AESEncryptEntity(String.valueOf(mListContactos.get(i).getId())));
                    params.put("origen_crea", Constants.AESEncryptEntity(Constants.getIPAddress(true)));


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

    public class ContactoRecycleAdapter extends RecyclerView.Adapter<ContactoRecycleHolder> {
        private int lastPosition = -1;

        @Override
        public ContactoRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contactos, viewGroup, false);
            setAnimation(v,i);
            return new ContactoRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final ContactoRecycleHolder productHolder, final int i) {

            productHolder.mtxtNombre.setText(mListContactos.get(i).getNombre());
            productHolder.mtxtValor.setText(mListContactos.get(i).getValor());

            if(mListContactos.get(i).getIs_principal()==1){
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
                        if (mListContactos.get(i).getIs_principal() == 1) {
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

                                        Intent intent = new Intent(ContactActivity.this, AddContactActivity.class);
                                        intent.putExtra("i", String.valueOf(i));
                                        startActivity(intent);
                                        return true;

                                    case R.id.eliminar:

                                        if (mListContactos.get(i).getIs_principal() == 1) {
                                            pDialog = new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.ERROR_TYPE);
                                            pDialog.setTitleText(getResources().getString(R.string.app_name));
                                            pDialog.setContentText(getString(R.string.error_contacto_delete));
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
            }else{
                productHolder.mImage.setVisibility(View.GONE);
            }

            if(select>=1){
                productHolder.mContenedor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("id_contacto",String.valueOf(mListContactos.get(i).getId()));
                        returnIntent.putExtra("contacto",String.valueOf(mListContactos.get(i).getNombre()));
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();

                    }
                });
            }






            setAnimation(productHolder.itemView, i);



        }


        @Override
        public int getItemCount() {
            return mListContactos.size();
        }

        public void removeItem(int position) {
            mListContactos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListContactos.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(ContactActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(ContactActivity.this, R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class ContactoRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mtxtNombre;
        public TextView mtxtValor;
        public ImageView mImage;
        public ImageView mStart;
        public CardView mContenedor;


        public ContactoRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtValor = (TextView) itemView.findViewById(R.id.txtValor);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
            mStart = (ImageView) itemView.findViewById(R.id.start);
            mContenedor = (CardView) itemView.findViewById(R.id.contenedor);
        }
    }





}
