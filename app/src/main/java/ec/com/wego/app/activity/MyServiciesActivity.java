package ec.com.wego.app.activity;

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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.Ordenes;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;
import ec.com.wego.app.holder.Categories;

public class MyServiciesActivity extends AppCompatActivity {

    private SweetAlertDialog pDialog;
    private Toolbar toolbar;
    private AppPreferences appPreferences;
    private String TAG = MyServiciesActivity.class.getName();
    private RecyclerView mServiciosRecyclerView;
    public static ArrayList<Ordenes> mListServicios;
    private ArrayList<Ordenes> mListServiciosFilter;
    private ServiciesRecycleAdapter mServiciesAdapter;

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
            case R.id.proceso:
                mServiciesAdapter.getFilter().filter("2");
                return true;
            case R.id.termi:
                mServiciesAdapter.getFilter().filter("3");
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

                                                    mListServicios.add(new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))),Constants.Decrypt(finalMObj.getString("nombre")),Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))),Constants.Decrypt(finalMObj.getString("fecha"))));


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



        public  ServiciesRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtFecha = (TextView) itemView.findViewById(R.id.txtFecha);

        }
    }


}
