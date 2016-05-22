package example.com.capstoneproject.management_layer.web_services;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import example.com.capstoneproject.R;
import example.com.capstoneproject.gui_layer.activities.PermisionActivity;
import example.com.capstoneproject.management_layer.Utilities;
import hugo.weaving.DebugLog;

/**
 * Created by Arin on 20/05/16.
 */
public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter
{
    private final static String FORECAST_BASE_URL_TEMPLATE = "http://api.openweathermap.org/data/2.5/weather?";
    private static final int SYNC_INTERVAL = 60 * 60 * 3;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final int PERMISSION_NOTIF_ID = 333;
    private static final String TAG = WeatherSyncAdapter.class.getSimpleName();
    private PendingIntent locationIntent;
    private GoogleApiClient googleApiClient;

    public WeatherSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .build();
    }

    public static Account getSyncAccount(Context context)
    {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount))
        {
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context)
    {
        /*
         * Since we've created an account
         */
        WeatherSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void syncImmediately(Context context)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime)
    {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }
        else
        {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context)
    {
        getSyncAccount(context);
    }

    @Override
    @DebugLog
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            showPermissionRequiredNotification();
            return;
        }
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new WeatherUpdateFinisedReceiver(),
                new IntentFilter(Utilities.WEATHER_ITEM_UPDATED_BROADCAST));

        logd("Permissions ok");
        //We are already in background, no point in playing it async
        ConnectionResult connectionResult = googleApiClient.blockingConnect(20, TimeUnit.SECONDS);
        if (connectionResult.isSuccess())
        {
            logd("Connection successful");
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setExpirationDuration(30 * 1000)
                    .setFastestInterval(1000);
            if(locationIntent == null)
            {

                Intent intent = new Intent(getContext(), WeatherUpdateService.class);
                intent.setAction(WeatherUpdateService.CHECK_WEATHER_FOR_LOCATION);
                locationIntent = PendingIntent.getService(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationIntent);
/*            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            log("User location => " + userLocation);
            if(userLocation == null)
                return;

            WeatherResponse response = requestTodayForecast(userLocation);
            processWeatherResponse(response);*/
        }
        //TODO  maybe add exponential backoff at some later date?
    }

    @DebugLog
    private void showPermissionRequiredNotification()
    {
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(getContext())
                        .setColor(ContextCompat.getColor(getContext(), R.color.primary_light))
                        //TODO change to some more meaningful icons
                        .setSmallIcon(R.drawable.ic_confirm)
                        .setContentTitle("Location permission required")
                        .setContentText("Location permission is required to download weather data.");

        Intent resultIntent = new Intent(getContext(), PermisionActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notifBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(PERMISSION_NOTIF_ID, notifBuilder.build());
    }
/*

    @DebugLog
    private WeatherResponse requestTodayForecast(@NonNull Location userLocation)
    {
        final int timeout = 1000 * 10;
        final String latKey = "lat";
        final String lonKey = "lon";
        final String appidKey = "APPID";
        Map<String, Object> paramMapping = new HashMap<>();
        paramMapping.put(latKey, userLocation.getLatitude());
        paramMapping.put(lonKey, userLocation.getLongitude());
        paramMapping.put(appidKey, BuildConfig.OPEN_WEATHER_MAP_API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        try
        {
            return restTemplate.getForObject(FORECAST_BASE_URL_TEMPLATE, WeatherResponse.class, paramMapping);
        }
        catch (RestClientException e)
        {
            log("Exception while retrieving weather information", e);
        }
        return null;
    }
*/

/*
    @DebugLog
    private void processWeatherResponse(WeatherResponse response)
    {
        if (isValidResponse(response))
            processValidWeatherResponse(response);
        else
            log("Response was invalid => " + response);
    }

    @DebugLog
    private void processValidWeatherResponse(WeatherResponse response)
    {
        WeatherItem weatherItem = WeatherItem.builder()
                .weatherType(WeatherItem.translateWeatherIdToWeatherType(response.getWeather().get(0).getId()))
                .windSpeed(Math.round(response.getWind().getSpeed()))
                .temperature(Math.round(response.getMain().getTemp()))
                .rainPrecipitate(response.getRain().getPrecipitationPer3H())
                .build();

        WeatherPreferenceModule preferenceModule = new WeatherPreferenceModule(getContext());
        preferenceModule.setWeatherItem(weatherItem);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Utilities.WEATHER_ITEM_UPDATED_BROADCAST));
    }

    @DebugLog
    private boolean isValidResponse(WeatherResponse response)
    {
        return response.getMain() != null && response.getWind() != null && response.getRain() != null
                && response.getWeather() != null && !response.getWeather().isEmpty();
    }
*/

    private void logd(String message)
    {
        Log.d(TAG, message);
    }

    private void log(String message)
    {
        Log.e(TAG, message);
    }

    private void log(String message, Exception exception)
    {
        Log.e(TAG, message, exception);
    }
    private class WeatherUpdateFinisedReceiver extends BroadcastReceiver
    {

        public WeatherUpdateFinisedReceiver()
        {
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(Utilities.equals(intent.getAction(), Utilities.WEATHER_ITEM_UPDATED_BROADCAST))
            {
                if(googleApiClient.isConnected())
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationIntent);
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);
            }
        }
    }
}
