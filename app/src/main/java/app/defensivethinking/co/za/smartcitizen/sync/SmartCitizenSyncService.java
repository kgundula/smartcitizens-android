package app.defensivethinking.co.za.smartcitizen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SmartCitizenSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SmartCitizenSyncAdapter sSmartCitizenSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if ( sSmartCitizenSyncAdapter == null){
                sSmartCitizenSyncAdapter = new SmartCitizenSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSmartCitizenSyncAdapter.getSyncAdapterBinder();
    }


}
