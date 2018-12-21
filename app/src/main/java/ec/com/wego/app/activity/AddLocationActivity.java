package ec.com.wego.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.auth.FirebaseAuth;
import ec.com.wego.app.R;
import ec.com.wego.app.clases.GPS;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private GoogleMap map;
    private MapView mapView;
    private String lat=null,log=null;
    private GPS gps = null;
    private String TAG = MyLocationsActivity.class.getName();
    private SweetAlertDialog pDialog;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Button btnGuardar;
    private GoogleApiClient googleApiClient;
    private EditText txtNombre;
    private EditText txtPiso;
    private EditText txtDepartamento;
    private static AppPreferences app;
    private int i=-1;
    private int id=0;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private TextView busqueda;
    private TextView txtBusqueda;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);


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

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        gps = new GPS(AddLocationActivity.this);
        if (!gps.canGetLocation()) {
            //gps.showSettingsAlert();
            settingsrequest();
        }else {
            //gps.city();
            lat = String.valueOf(gps.getLatitude());
            log = String.valueOf(gps.getLongitude());
        }

        mapView = (MapView) findViewById(R.id.mapView);
        //try {
        mapView.onCreate(savedInstanceState);




        app = new AppPreferences(getApplicationContext());

        txtNombre=(EditText) findViewById(R.id.txtNombre);
        txtPiso=(EditText) findViewById(R.id.txtPiso);
        txtDepartamento=(EditText) findViewById(R.id.txtDepartamento);
        txtBusqueda=(TextView) findViewById(R.id.txtBusqueda);



        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setCountry("EC").build();

        ImageView searchIcon = (ImageView)((LinearLayout)placeAutocompleteFragment.getView()).getChildAt(0);
        ImageView closed = (ImageView)((LinearLayout)placeAutocompleteFragment.getView()).getChildAt(2);
        busqueda = (TextView) ((LinearLayout)placeAutocompleteFragment.getView()).getChildAt(1);

        busqueda.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        busqueda.setHintTextColor(getResources().getColor(R.color.colorPrimaryText));
        // Set the desired icon
        searchIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        closed.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));



        placeAutocompleteFragment.setFilter(autocompleteFilter);

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                eventSearch(place.getName().toString());

            }

            @Override
            public void onError(Status status) {


            }
        });


        btnGuardar=(Button) findViewById(R.id.btnsave);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtNombre.getText().toString().trim().equals("")){
                    txtNombre.setError(getString(R.string.error_contacto_nombre2));
                    return;
                }

                if(lat.equals("0") || lat.equals("0.00") || log.equals("0") || log.equals("0.00"))
                {
                    pDialog= new SweetAlertDialog(AddLocationActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getResources().getString(R.string.error_ubicacion));
                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();


                        }
                    });
                    pDialog.show();

                    return;
                }


                for (int x=0;x<LocationActivity.mListLocations.size();x++){

                    if(LocationActivity.mListLocations.get(x).getNombre().equals(txtNombre.getText().toString().trim()) && LocationActivity.mListLocations.get(x).getId()!=id){


                        txtNombre.setError(getString(R.string.error_ubicacion_nombre));

                        return;
                    }

                }
                saveTask();

            }
        });




        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {



                map=googleMap;

                try {
                    boolean success = map.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    AddLocationActivity.this, R.raw.mapstyle));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }


                UiSettings mapUiSettings = map.getUiSettings();
                mapUiSettings.setZoomControlsEnabled(true);
                //mapUiSettings.setAllGesturesEnabled(true);
                mapUiSettings.setScrollGesturesEnabled(true);
                mapUiSettings.setZoomGesturesEnabled(true);
                mapUiSettings.setMapToolbarEnabled(false);



                map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //get latlng at the center by calling
                        LatLng mylocation = map.getCameraPosition().target;

                        lat=String.valueOf(mylocation.latitude);
                        log=String.valueOf(mylocation.longitude);


                        Geocoder geocoder;
                        List<Address> direccion;
                        geocoder = new Geocoder(AddLocationActivity.this, Locale.getDefault());

                        try {
                            direccion = geocoder.getFromLocation(mylocation.latitude, mylocation.longitude, 1);

                            if(direccion.size()>=1) {
                                txtBusqueda.setText(direccion.get(0).getAddressLine(0));
                            }else
                            {
                                txtBusqueda.setText("");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            busqueda.setText("");
                        }


                    }
                });

                Bundle bun = getIntent().getExtras();
                if(bun.containsKey("i")){

                    i= Integer.parseInt(bun.getString("i"));

                    txtNombre.setText(LocationActivity.mListLocations.get(i).getNombre());
                    busqueda.setText(LocationActivity.mListLocations.get(i).getDireccion().toString());
                    txtPiso.setText(LocationActivity.mListLocations.get(i).getPiso());
                    txtDepartamento.setText(LocationActivity.mListLocations.get(i).getDepartamento());
                    id= LocationActivity.mListLocations.get(i).getId();




                    lat=  LocationActivity.mListLocations.get(i).getLatitud();
                    log = LocationActivity.mListLocations.get(i).getLongitud();
                    changePosition();

                }else{
                    if(lat!=null) {
                        changePosition();
                    }
                }


            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //--> ARROW BACK
                onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* secondary menu*/
        //getMenuInflater().inflate(R.menu.menu_save, menu);
        //item = menu.findItem(R.id.action_save);
        return true;
    }


    private void changePosition()
    {

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_my_location);

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createBitmap(b);

        // Add a marker in Sydney and move the camera
        LatLng posicion = new LatLng(Double.parseDouble(lat), Double.parseDouble(log));
        map.clear();
        map.addMarker(new MarkerOptions().position(posicion).title("Me")).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        map.moveCamera(CameraUpdateFactory.newLatLng(posicion));

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 20));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicion)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }



    private void eventSearch(final String search){


        //Showing the progress dialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GOOGLE_GEPCODE+"?address="+search.replaceAll(" ", "%20")+"&key="+getString(R.string.api_maps),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response

                        JSONObject res= null;
                        try {
                            res = new JSONObject(responde);

                            lat = ((JSONArray)res.get("results")).getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").get("lat").toString();
                            log = ((JSONArray)res.get("results")).getJSONObject(0).getJSONObject("geometry")
                                    .getJSONObject("location").get("lng").toString();

                            if(lat!=null && log!=null) {
                                changePosition();
                            }else
                            {


                                pDialog= new SweetAlertDialog(AddLocationActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(getString(R.string.error_location));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                    }
                                });
                                pDialog.show();

                            }

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progressDialog.dismiss();

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(AddLocationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("address", search);
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
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();





    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

    }





    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AddLocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        lat = String.valueOf(gps.getLatitude());
                        log = String.valueOf(gps.getLongitude());
                        changePosition();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    private void saveTask(){
        //Showing the progress dialog


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_ubicacion/format/json",
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

                                pDialog= new SweetAlertDialog(AddLocationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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


                                pDialog= new SweetAlertDialog(AddLocationActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(AddLocationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("persona_id", Constants.Encrypt(app.getUserId()));
                    params.put("nombre", Constants.Encrypt(txtNombre.getText().toString().trim()));
                    params.put("direccion", Constants.Encrypt(txtBusqueda.getText().toString().trim()));
                    params.put("piso", Constants.Encrypt(txtPiso.getText().toString().trim()));
                    params.put("departamento", Constants.Encrypt(txtDepartamento.getText().toString().trim()));
                    params.put("latitud", Constants.Encrypt(lat));
                    params.put("longitud", Constants.Encrypt(log));
                    params.put("origen_crea", Constants.Encrypt(Constants.getIPAddress(true)));
                    params.put("id", Constants.Encrypt(String.valueOf(id)));


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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
