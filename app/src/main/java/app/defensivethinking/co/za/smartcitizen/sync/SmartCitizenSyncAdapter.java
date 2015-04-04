package app.defensivethinking.co.za.smartcitizen.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by Profusion on 2015-04-02.
 */
public class SmartCitizenSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SmartCitizenSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 360;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SmartCitizenSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

    @Override
    public void onSyncCanceled(Thread thread) {
        super.onSyncCanceled(thread);
    }


    public static void initializeSyncAdapter(Context context) {
        //getSyncAccount(context);
    }
}
