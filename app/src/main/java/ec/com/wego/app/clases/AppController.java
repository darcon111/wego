package ec.com.wego.app.clases;

/**
 * Created by darco on 25/07/2016.
 */
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;



public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;


    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (this.mRequestQueue != null) {
            this.mRequestQueue.cancelAll(tag);
        }
    }





}
