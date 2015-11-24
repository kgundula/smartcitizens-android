package app.defensivethinking.co.za.smartcitizen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SmartCitizenAuthenticatorService extends Service {
    private SmartCitizenAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new SmartCitizenAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
