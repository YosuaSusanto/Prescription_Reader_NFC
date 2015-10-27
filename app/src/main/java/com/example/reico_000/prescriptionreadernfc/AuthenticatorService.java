package com.example.reico_000.prescriptionreadernfc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Yosua Susanto on 26/10/2015.
 *
 * A bound Service that instantiates the authenticator
 * when started.
 */

public class AuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private StubAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new StubAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
