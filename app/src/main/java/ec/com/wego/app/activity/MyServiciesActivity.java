package ec.com.wego.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.Ordenes;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;

public class MyServiciesActivity extends AppCompatActivity {

    private SweetAlertDialog pDialog;
    private Toolbar toolbar;
    private AppPreferences appPreferences;
    private String TAG = MyServiciesActivity.class.getName();
    private RecyclerView mServiciosRecyclerView;
    public static ArrayList<Ordenes> mListServicios;
    private ArrayList<Ordenes> mListServiciosFilter;
    private ServiciesRecycleAdapter mServiciesAdapter;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_servicies);

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

        appPreferences= new AppPreferences(MyServiciesActivity.this);

        mServiciosRecyclerView = (RecyclerView) findViewById(R.id.ordenes);
        // Create a grid layout with two columns

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mServiciosRecyclerView.setLayoutManager(layoutManager);
        mServiciesAdapter = new ServiciesRecycleAdapter();
        mServiciosRecyclerView.setAdapter(mServiciesAdapter);

        mListServicios = new ArrayList<Ordenes>();
        mListServiciosFilter = new ArrayList<Ordenes>();





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
        getMenuInflater().inflate(R.menu.servicios, menu);
        //item = menu.findItem(R.id.action_save);
        super.onCreateOptionsMenu(menu);
        return true;
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
            case R.id.all:
                mServiciesAdapter.getFilter().filter("-1");
                return true;
            case R.id.pendi:
                mServiciesAdapter.getFilter().filter("1");
                return true;
            case R.id.cancel:
                mServiciesAdapter.getFilter().filter("0");
                return true;
            case R.id.asig:
                mServiciesAdapter.getFilter().filter("2");
                return true;
            case R.id.proceso:
                mServiciesAdapter.getFilter().filter("5");
                return true;
            case R.id.termi:
                mServiciesAdapter.getFilter().filter("3");
                return true;
            case R.id.califi:
                mServiciesAdapter.getFilter().filter("4");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadTask(String persona_id){

        mListServicios.clear();

        final JSONObject[] res = {null};
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();



        final String finalPersona = persona_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getServicio/format/json",
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

                            if(res[0].getString("result").equals("OK") ){

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

                                                    mListServicios.add(new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))),Constants.Decrypt(finalMObj.getString("nombre")),Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))),Constants.Decrypt(finalMObj.getString("fecha")),Constants.Decrypt(finalMObj.getString("fechaC"))));


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                mListServiciosFilter = mListServicios;
                                                mServiciesAdapter.notifyItemChanged(finalX);
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


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(MyServiciesActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(MyServiciesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                try {
                    params.put("userid", Constants.Encrypt(finalPersona));
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


    private void cancelarTask(final int orden_id, final int position,final String observacion){

        final JSONObject[] res = {null};
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"cancelar_orden/format/json",
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

                            if(res[0].getString("result").equals("OK") ){

                                 pDialog.dismiss();


                                pDialog= new SweetAlertDialog(MyServiciesActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        mListServicios.get(position).setEstado(0);
                                        mServiciesAdapter.notifyItemChanged(position);

                                    }
                                });
                                pDialog.show();


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(MyServiciesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
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
                        Toast.makeText(MyServiciesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                try {
                    params.put("userid", Constants.Encrypt(appPreferences.getUserId()));
                    params.put("idorden", Constants.Encrypt(String.valueOf(orden_id)));
                    params.put("observacion", Constants.Encrypt(observacion));

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

    public class ServiciesRecycleAdapter extends RecyclerView.Adapter<ServiciesRecycleHolder>   implements Filterable {
        private int lastPosition = -1;

        @Override
        public  ServiciesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicios, viewGroup, false);
            setAnimation(v,i);
            return new  ServiciesRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final  ServiciesRecycleHolder productHolder, final int i) {

            productHolder.mtxtNombre.setText(mListServiciosFilter.get(i).getName());
            productHolder.mtxtFecha.setText(mListServiciosFilter.get(i).getFecha());

            if (mListServiciosFilter.get(i).getEstado()!=0)
            {

                productHolder.mimgMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (popupMenu != null) {
                            popupMenu.dismiss();
                        }


                        popupMenu = new PopupMenu(v.getContext(), v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupMenu.setGravity(Gravity.CENTER);
                        }
                        if (mListServiciosFilter.get(i).getEstado() == 1) {
                            popupMenu.inflate(R.menu.servicio_cancelar);
                        } else if (mListServiciosFilter.get(i).getEstado() == 2){
                            popupMenu.inflate(R.menu.servicio_terminar);
                        }else if(mListServiciosFilter.get(i).getEstado() == 4){
                            popupMenu.inflate(R.menu.servicio_calificar);
                        }else
                        {
                            popupMenu.inflate(R.menu.servicio_ver);
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
                                    case R.id.calificar:

                                        Intent intent = new Intent(MyServiciesActivity.this, CalificarActivity.class);
                                        intent.putExtra("id", String.valueOf(mListServiciosFilter.get(i).getId()));
                                        startActivity(intent);


                                        return true;

                                    case R.id.detalle:

                                        Intent intent2 = new Intent(MyServiciesActivity.this, OrdenesActivity.class);
                                        intent2.putExtra("id", String.valueOf(mListServiciosFilter.get(i).getId()));
                                        startActivity(intent2);


                                        return true;

                                    case R.id.cancelar:


                                            pDialog = new SweetAlertDialog(MyServiciesActivity.this, SweetAlertDialog.WARNING_TYPE);
                                            pDialog.setTitleText(getResources().getString(R.string.app_name));
                                            pDialog.setContentText(getResources().getString(R.string.cancelarconsulta));
                                            pDialog.setConfirmText(getResources().getString(R.string.ok));
                                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();


                                                    cancelar(mListServiciosFilter.get(i).getId(),i);

                                                }
                                            });
                                            pDialog.setCancelText(getResources().getString(R.string.no));
                                            pDialog.show();


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
                productHolder.mimgMenu.setVisibility(View.GONE);
            }



            if (mListServiciosFilter.get(i).getEstado()==0){
                productHolder.mEstado.setText("Cancelado");
            }else if (mListServiciosFilter.get(i).getEstado()==1){
                productHolder.mEstado.setText("Creado");
            }else if (mListServiciosFilter.get(i).getEstado()==2){
                productHolder.mEstado.setText("Asignado");
            }
            else if (mListServiciosFilter.get(i).getEstado()==4){
                productHolder.mEstado.setText("Pen. Calif");
            } else if (mListServiciosFilter.get(i).getEstado()==5){
                productHolder.mEstado.setText("En Proceso");
            }
            else{
                //3 terminado
                productHolder.mEstado.setText("Terminado");
            }

            productHolder.mCreacion.setText(mListServiciosFilter.get(i).getFechaCreacion());



            setAnimation(productHolder.itemView, i);



        }


        @Override
        public int getItemCount() {
            return mListServiciosFilter.size();
        }

        public void removeItem(int position) {
            mListServiciosFilter.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListServiciosFilter.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(MyServiciesActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(MyServiciesActivity.this, R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mListServiciosFilter = mListServicios;
                    } else {
                        ArrayList<Ordenes> filteredList = new ArrayList<>();
                        for (Ordenes row : mListServicios) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match

                            if(charString.equals("-1"))
                            {
                                filteredList.add(row);
                            }else if (row.getEstado()==Integer.parseInt(charString)) {
                                filteredList.add(row);
                            }
                        }

                        mListServiciosFilter = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mListServiciosFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mListServiciosFilter = (ArrayList<Ordenes>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }


    }

    public class  ServiciesRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mtxtNombre;
        public TextView mtxtFecha;
        public ImageView mimgMenu;
        public TextView mEstado;
        public TextView mCreacion;



        public  ServiciesRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtFecha = (TextView) itemView.findViewById(R.id.txtFechac);
            mEstado = (TextView) itemView.findViewById(R.id.txtEstado);
            mCreacion = (TextView) itemView.findViewById(R.id.txtCreacion);
            mimgMenu = (ImageView) itemView.findViewById(R.id.menu);


        }
    }

    public void cancelar(final int orden, final int position)
    {

        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v=getLayoutInflater().inflate(R.layout.item_cancelar
                , null);

        Button btnclose=(Button) v.findViewById(R.id.close);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        final EditText txtmotivo=(EditText) v.findViewById(R.id.txtMotivo);

        Button btnsend=(Button) v.findViewById(R.id.send);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!txtmotivo.getText().toString().equals("")) {

                    settingsDialog.dismiss();
                    cancelarTask(orden,position,txtmotivo.getText().toString());
                }else
                {
                    txtmotivo.setError(getString(R.string.motivo_ingrese));
                }
            }
        });

        settingsDialog.setContentView(v);
        settingsDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(settingsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.show();
        settingsDialog.getWindow().setAttributes(lp);
    }


}
