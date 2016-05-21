package example.com.capstoneproject.management_layer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import example.com.capstoneproject.management_layer.web_services.WeatherSyncAdapter;

public class SyncService extends Service
{
    private static final String TAG = SyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static WeatherSyncAdapter syncAdapter = null;

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock)
        {
            if (syncAdapter == null)
            {
                syncAdapter = new WeatherSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return syncAdapter.getSyncAdapterBinder();
    }
}

