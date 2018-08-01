package com.wego.app.servicies;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.wego.app.config.AppPreferences;


public class FCMInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInstanceIdService";
    private AppPreferences app;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        try {
            app = new AppPreferences(getApplicationContext());
            app.setFirebasetoken(token);
            app.setFlag("1");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





}
