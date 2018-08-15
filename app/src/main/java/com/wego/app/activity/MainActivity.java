package com.wego.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wego.app.R;
import com.wego.app.adapter.MenuAdapter;
import com.wego.app.config.AppPreferences;
import com.wego.app.config.Constants;
import com.wego.app.holder.Categories;
import com.wego.app.holder.Servicies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView_main,mCategoriesRecyclerView;
    private String[] TITLES = new String[8];
    private int[] ICONS = new int[8];
    private ActionBarDrawerToggle mDrawerToggle;

    private int PROFILE = R.drawable.ic_user;
    //private RecyclerView.Adapter mAdapter;
    private MenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private String TAG = MainActivity.class.getName();
    private String name="Usuario";
     private static FirebaseUser user;

    private ArrayList<Categories> mListCategories;
    private CategoriesRecycleAdapter mCategoriesAdapter;

    private SweetAlertDialog pDialog;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* set orientation*/
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        appPreferences = new AppPreferences(MainActivity.this);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_list_white_24dp));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }

        /*main menu*/
        mRecyclerView_main = (RecyclerView) findViewById(R.id.RecyclerView_main); // Assigning the RecyclerView Object to the xml View
        mRecyclerView_main.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size


        /* menu main*/
        TITLES[0] = getString(R.string.help);
        TITLES[1] = getString(R.string.config);
        TITLES[2] = getString(R.string.share);
        TITLES[3] = getString(R.string.terms);
        TITLES[4] = getString(R.string.condition);
        TITLES[5] = getString(R.string.exit);

        ICONS[0] = R.drawable.ic_help;
        ICONS[1] = R.drawable.ic_config;
        ICONS[2] = R.drawable.ic_share;
        ICONS[3] = R.drawable.ic_terms;
        ICONS[4] = R.drawable.ic_condition;
        ICONS[5] = R.drawable.ic_exit;





        user = FirebaseAuth.getInstance().getCurrentUser();


        if(appPreferences.getUser().equals("")) {
            String[] temp = user.getEmail().split("@");
            name = temp[0];
            appPreferences.setUser(name);
        }

        appPreferences.setActualizar("1");


        mCategoriesRecyclerView = (RecyclerView) findViewById(R.id.servicies_recycler_view);
        // Create a grid layout with two columns

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);

        mCategoriesRecyclerView.setLayoutManager(layoutManager);
        mCategoriesAdapter = new CategoriesRecycleAdapter();
        mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);

        mListCategories = new ArrayList<Categories>();

        validaTask(user.getEmail());

        menu();


    }

    public void menu()
    {


            mAdapter = new MenuAdapter(TITLES, ICONS, appPreferences.getUser(), PROFILE, appPreferences.getImagen(), MainActivity.this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

            mRecyclerView_main.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

            mRecyclerView_main.setLayoutManager(mLayoutManager);

            Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
            mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                    // open I am not going to put anything here)
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    // Code here will execute once drawer is closed
                }

            }; // Drawer Toggle Object Made

            Drawer.addDrawerListener(mDrawerToggle);

            mRecyclerView_main.addOnItemTouchListener(new Constants.RecyclerTouchListener(getApplicationContext(), mRecyclerView_main, new Constants.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                }

                @Override
                public void onLongClick(View view, final int position) {

                    Intent intent;

                    switch (position) {
                        case 1:

                            break;
                        case 2:

                            break;
                        case 3:

                            break;
                        case 4:

                            break;

                        case 5:


                            break;

                        case 6:

                            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                            pDialog.setTitleText(getResources().getString(R.string.app_name));
                            pDialog.setContentText(getResources().getString(R.string.msg_exit));
                            pDialog.setConfirmText(getString(R.string.yes));
                            pDialog.setCancelText(getString(R.string.no));
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    signOut();
                                    finish();
                                }
                            });
                            pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.cancel();
                                }
                            });
                            pDialog.show();

                            break;


                        default:

                            break;
                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerToggle.onDrawerClosed(mRecyclerView_main);
                            Drawer.closeDrawers();
                        }
                    }, 200);


                }
            }));
        }



    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }



    private void validaTask(String email){

        final JSONObject[] res = {null};
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(MainActivity.this);

        final String finalEmail = email;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"validaUser/format/json",
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
                                    JSONObject mObj = mObjResp[0].getJSONObject(0);

                                    appPreferences.setUserId(Constants.AESDecryptEntity(mObj.getString("id_persona")));
                                    mObj = mObjResp[0].getJSONObject(1);
                                    appPreferences.setImagen(mObj.getString("imagen"));

                                    mObj = mObjResp[0].getJSONObject(2);

                                    //if(!appPreferences.getUser().equals(Constants.AESDecryptEntity(mObj.getString("nombres"))))
                                    //{
                                        appPreferences.setUser(Constants.AESDecryptEntity(mObj.getString("nombres")));
                                        //appPreferences.setActualizar("1");

                                    //}


                                    for (int x = 3; x< mObjResp[0].length(); x++)
                                    {
                                        mObj = mObjResp[0].getJSONObject(x);

                                        //mListCategories.add(new Categories(Integer.parseInt(Constants.AESDecryptEntity(mObj.getString("id"))),Constants.AESDecryptEntity(mObj.getString("nombre")),Constants.AESDecryptEntity(mObj.getString("descripcion")),mObj.getString("imagen")));
                                        //mCategoriesAdapter.notifyItemChanged(x-2);

                                        final JSONObject finalMObj = mObj;
                                        final int finalX = x;
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    mListCategories.add(new Categories(Integer.parseInt(Constants.AESDecryptEntity(finalMObj.getString("id"))),Constants.AESDecryptEntity(finalMObj.getString("nombre")),Constants.AESDecryptEntity(finalMObj.getString("descripcion")), finalMObj.getString("imagen")));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                mCategoriesAdapter.notifyItemChanged(finalX -3);
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


                                pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("email", Constants.AESEncryptEntity(finalEmail));
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




    /* adapter*/

    public class CategoriesRecycleAdapter extends RecyclerView.Adapter<CategoriesRecycleHolder> {
        private int lastPosition = -1;

        @Override
        public CategoriesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicies, viewGroup, false);
            setAnimation(v,i);
            return new CategoriesRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final CategoriesRecycleHolder productHolder, final int i) {

            productHolder.mTitle.setText(mListCategories.get(i).getNombre());

            Glide.with(MainActivity.this)
                    .load(mListCategories.get(i).getImagen())
                    .fitCenter()
                    .into(productHolder.mImage);

            productHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(MainActivity.this,ServiciesActivity.class);
                    intent.putExtra("categoria",String.valueOf(mListCategories.get(i).getId()));
                    startActivity(intent);

                }
            });



            setAnimation(productHolder.itemView, i);



        }


        @Override
        public int getItemCount() {
            return mListCategories.size();
        }

        public void removeItem(int position) {
            mListCategories.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListCategories.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }


    }

    public class CategoriesRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mImage;


        public CategoriesRecycleHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txttitle);
            mImage = (ImageView) itemView.findViewById(R.id.imagen);
        }
    }


    @Override
    public void onResume() {
        super.onResume();



        if(appPreferences.getActualizar().equals("1")){

            mAdapter.setName(appPreferences.getUser());
            mAdapter.notifyItemChanged(0);
            appPreferences.setActualizar("0");
        }




    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}
