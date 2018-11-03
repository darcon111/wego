package ec.com.wego.app.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import ec.com.wego.app.config.Constants;
import ec.com.wego.app.holder.Servicies;
import ec.com.wego.app.holder.Serviciescarac;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ServiciesCaractActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mServiciesRecyclerView;
    private String TAG = ServiciesActivity.class.getName();
    private ArrayList<Serviciescarac> mListServicies;
    private ServiciesRecycleAdapter mServiciesAdapter;

    private SweetAlertDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicies_caract);


        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);
        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.servicios_det));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        mServiciesRecyclerView = (RecyclerView) findViewById(R.id.servicies);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mServiciesRecyclerView.setLayoutManager(layoutManager);
        mServiciesAdapter = new ServiciesRecycleAdapter();
        mServiciesRecyclerView.setAdapter(mServiciesAdapter);

        mListServicies = new ArrayList<Serviciescarac>();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mListServicies.clear();
            loadTask(extras.getString("servicioid"));
        }
    }



    private void loadTask(String id){
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(ServiciesCaractActivity.this);

        final String finalid = id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getServiciosCaract/format/json",
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
                            pDialog.dismiss();
                        }

                        try {

                            if(Constants.AESDecryptEntity(res.getString("result")).equals("OK") ){
                                final JSONArray mObjResp = res.getJSONArray("data");
                                final JSONObject[] mObj = new JSONObject[1];


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int x=0;x<mObjResp.length();x++)
                                        {
                                            try {
                                                mObj[0] = mObjResp.getJSONObject(x);
                                                mListServicies.add(new Serviciescarac(Integer.parseInt(/*Constants.AESDecryptEntity(*/mObj[0].getString("id")/*)*/),/*Constants.AESDecryptEntity(*/mObj[0].getString("nombre")/*)*/,/*Constants.AESDecryptEntity(*/mObj[0].getString("descripcion")/*)*/,/*Constants.AESDecryptEntity(*/mObj[0].getString("respt")/*)*/,/*Constants.AESDecryptEntity(*/mObj[0].getString("respc")/*)*/,/*Constants.AESDecryptEntity(*/mObj[0].getString("costo")/*)*/,Integer.parseInt(mObj[0].getString("ref_id")),Integer.parseInt(mObj[0].getString("servicio_id")) ));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }

                                        load(true,Integer.parseInt(finalid));




                                    }
                                });

                                pDialog.dismiss();




                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(ServiciesCaractActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.AESDecryptEntity(res.getString("message")));
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
                        Toast.makeText(ServiciesCaractActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("servicio_id", Constants.AESEncryptEntity(finalid));
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

    public class ServiciesRecycleAdapter extends RecyclerView.Adapter<ServiciesRecycleHolder> {
        private int lastPosition = -1;
        ArrayList<Serviciescarac> mListServiciesData= new ArrayList<Serviciescarac>();

        @Override
        public ServiciesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicies_carac, viewGroup, false);
            setAnimation(v,i);
            return new ServiciesRecycleHolder(v);
        }

        public void setData(ArrayList<Serviciescarac> temp)
        {
            mListServiciesData=temp;
        }


        @Override
        public void onBindViewHolder(final ServiciesRecycleHolder productHolder, final int i) {


                productHolder.mTitle.setText(mListServiciesData.get(i).getNombre());
                productHolder.mTxtrespotd.setText(mListServiciesData.get(i).getRespt());
                productHolder.mTxtrespcd.setText(mListServiciesData.get(i).getRespc());
                productHolder.mTxtcosto.setText("Costo $" + mListServiciesData.get(i).getCosto());

                productHolder.mBtnobtener.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ServiciesCaractActivity.this, GetServiciesActivity.class);
                        intent.putExtra("servicio_id", String.valueOf(mListServiciesData.get(i).getId()));
                        intent.putExtra("costo", String.valueOf(mListServiciesData.get(i).getCosto()));
                        startActivity(intent);

                    }
                });



                    productHolder.mCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(mListServiciesData.get(i).getRespc().equals("null")){

                                load(false,mListServiciesData.get(i).getId());

                            }else {
                                if (mListServiciesData.get(i).getView() == 0) {

                                    Animation slideUp = AnimationUtils.loadAnimation(ServiciesCaractActivity.this, R.anim.modal_in);
                                    productHolder.mContenedor.setVisibility(View.VISIBLE);
                                    productHolder.mContenedor.startAnimation(slideUp);
                                    mListServiciesData.get(i).setView(1);
                                } else {

                                    productHolder.mContenedor.setVisibility(View.GONE);

                                    mListServiciesData.get(i).setView(0);

                                }
                            }



                               /* if (mListServicies.get(i).getView() == 0) {

                                    Animation slideUp = AnimationUtils.loadAnimation(ServiciesCaractActivity.this, R.anim.modal_in);
                                    productHolder.mContenedor.setVisibility(View.VISIBLE);
                                    productHolder.mContenedor.startAnimation(slideUp);
                                    mListServicies.get(i).setView(1);
                                } else {

                                    productHolder.mContenedor.setVisibility(View.GONE);

                                    mListServicies.get(i).setView(0);

                                }*/
                           /* } else {

                                Intent intent = new Intent(ServiciesCaractActivity.this, RecursivoActivity.class);
                                intent.putExtra("servicio_id", String.valueOf(mListServicies.get(i).getId()));
                                startActivity(intent);

                            }*/

                        }
                    });



                setAnimation(productHolder.itemView, i);


        }


        @Override
        public int getItemCount() {
            return mListServiciesData.size();
        }

        public void removeItem(int position) {
            mListServiciesData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListServiciesData.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(ServiciesCaractActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(ServiciesCaractActivity.this, R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class ServiciesRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mTxtrespotd;
        public TextView mTxtrespcd;
        public TextView mTxtcosto;
        public CardView mCard;
        public LinearLayout mContenedor;
        public RecyclerView mServicies_ref;
        public LinearLayout mProgress;
        public Button mBtnobtener;






        public ServiciesRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txttitle);
            mTxtrespotd = (TextView) itemView.findViewById(R.id.txtrespotd);
            mTxtrespcd = (TextView) itemView.findViewById(R.id.txtrespcd);
            mTxtcosto = (TextView) itemView.findViewById(R.id.txtcosto);
            mCard = (CardView) itemView.findViewById(R.id.card);
            mContenedor = (LinearLayout) itemView.findViewById(R.id.contenedor);
            mServicies_ref=(RecyclerView) itemView.findViewById(R.id.servicies_ref);
            mProgress = (LinearLayout) itemView.findViewById(R.id.progress);
            mBtnobtener = (Button) itemView.findViewById(R.id.btnobtener);
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






    private void load(boolean flag,int id){



        ArrayList<Serviciescarac> mListServiciestemp= new ArrayList<Serviciescarac>();

        for (int x=0;x<mListServicies.size();x++)
        {
            if(flag)
            {
                if(mListServicies.get(x).getServicio_id()==id && mListServicies.get(x).getRef_id()==0)
                {
                    mListServiciestemp.add(mListServicies.get(x));
                }
            }else
            {
                if(mListServicies.get(x).getRef_id()==id)
                {
                    mListServiciestemp.add(mListServicies.get(x));
                }
            }
        }

        mServiciesAdapter.setData(mListServiciestemp);
        mServiciesAdapter.notifyDataSetChanged();




    }


}
