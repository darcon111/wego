package ec.com.wego.app.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.config.AppPreferences;
import ec.com.wego.app.config.ConfigPay;
import ec.com.wego.app.config.Constants;


public class PayPal extends AppCompatActivity  {

    private static final String TAG = PayPal.class.getSimpleName();
    private JSONArray result;
    private SweetAlertDialog pDialog;


    //parametros
    String costo,fecha,hora,servicio_id,contacto,ubicacion;

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(ConfigPay.PAYPAL_CLIENT_ID)
            .merchantName("Wego")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

     AppPreferences app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        app = new AppPreferences(getApplicationContext());

        Bundle bundle = getIntent().getExtras();


        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
        DataPay();
        getPayment();

    }


    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void DataPay(){

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            // TODO: parametros que se reciben

            costo = getIntent().getStringExtra("costo");
            fecha = getIntent().getStringExtra("fecha");
            hora = getIntent().getStringExtra("hora");
            servicio_id = getIntent().getStringExtra("servicio_id");
            contacto = getIntent().getStringExtra("id_contacto");
            ubicacion = getIntent().getStringExtra("id_ubicacion");


        }else {

            Toast.makeText(this,"No se reciben valores!", Toast.LENGTH_SHORT).show();


        }
    }

    public void getPayment() {
        //Getting the amount from editText
//        paymentAmount = editTextAmount.getText().toString();

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(costo)), "USD",
                getResources().getString(R.string.Text_Payment_to)+ " "  ,
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);


                        pDialog = new SweetAlertDialog(PayPal.this, SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                        pDialog.setContentText(getResources().getString(R.string.gracias_pago));
                        pDialog.setConfirmText(getResources().getString(R.string.ok));
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                save();

                            }
                        });
                        pDialog.show();





                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
                finish();

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

    }

    private void save()
    {
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"save_servicio/format/json",
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





                                pDialog.dismiss();

                            }else
                            {
                                pDialog.dismiss();

                                JSONArray mObjResp = res.getJSONArray("data");
                                final JSONObject mObj = mObjResp.getJSONObject(0);


                                pDialog = new SweetAlertDialog(PayPal.this, SweetAlertDialog.WARNING_TYPE);
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
                        Toast.makeText(PayPal.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {

                    params.put("userid", Constants.AESEncryptEntity(app.getUserId()));
                    params.put("costo", Constants.AESEncryptEntity(costo));
                    params.put("fecha", Constants.AESEncryptEntity(fecha));
                    params.put("hora", Constants.AESEncryptEntity(hora));
                    params.put("servicio_id", Constants.AESEncryptEntity(servicio_id));
                    params.put("contacto", Constants.AESEncryptEntity(contacto));
                    params.put("ubicacion", Constants.AESEncryptEntity(ubicacion));

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
