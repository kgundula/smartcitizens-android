package app.defensivethinking.co.za.smartcitizen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Profusion on 2015-04-02.
 */
public class SmartCitizenSyncService extends Service {

    @Override
    public void onCreate() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
