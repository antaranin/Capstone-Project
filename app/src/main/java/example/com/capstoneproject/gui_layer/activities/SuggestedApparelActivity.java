package example.com.capstoneproject.gui_layer.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.BuildConfig;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.DataContract;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.data_layer.WeatherPreferenceModule;
import example.com.capstoneproject.gui_layer.SuggestedApparelAdapter;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.management_layer.suggestion_processor.SuggestionProcessor;
import example.com.capstoneproject.management_layer.web_services.WeatherSyncAdapter;
import example.com.capstoneproject.model_layer.ClothingItem;
import icepick.Icepick;
import icepick.State;
import lombok.NonNull;


public class SuggestedApparelActivity extends AppCompatActivity
        implements SuggestedApparelAdapter.OnItemLongClickedListener, SuggestionProcessor.OnSuggestionMadeListener
{
    private InterstitialAd interstitialAd;
    private static final int REQUEST_LOCATION_PERMISSIONS = 42;
    @BindView(R.id.current_weather_iv)
    ImageView weatherIv;

    @BindView(R.id.current_wind_tv)
    TextView windTv;

    @BindView(R.id.current_temp_tv)
    TextView temperatureTv;

    @BindView(R.id.no_item_tv)
    TextView noItemTv;

    @State
    WeatherItem currentWeather;

    private SuggestionProcessor suggestionProcessor;
    private SuggestedApparelAdapter adapter;
    private ContentObserver observer = new ContentObserver(new Handler())
    {
        @Override
        public boolean deliverSelfNotifications()
        {
            return false;
        }

        @Override
        public void onChange(boolean selfChange)
        {
            super.onChange(selfChange);
            extractClothingData();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            super.onChange(selfChange, uri);
            extractClothingData();
        }
    };

    private WeatherChangeReceiver receiver = new WeatherChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_apparel);
        if (getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);

        prepareAd();

        suggestionProcessor = new SuggestionProcessor();
        suggestionProcessor.setListener(this);

        ButterKnife.bind(this);
        adapter = new SuggestedApparelAdapter();
        adapter.setListener(this);
        RecyclerView recycler = ButterKnife.findById(this, R.id.recycler_view);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(getResources().getInteger(R.integer.suggested_columns), StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(sglm);
        recycler.setAdapter(adapter);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getContentResolver().registerContentObserver(DataContract.ClothingEntry.CONTENT_URI, false, observer);

        if (savedInstanceState != null)
            restoreInstance(savedInstanceState);
        else
            createNewInstance();

        requestPermissionsIfNeeded(getIntent().getAction());
    }

    private void prepareAd()
    {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.AD_UNIT_ID);
        interstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                requestNewInterstitial();
                goToItemList();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        interstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
        suggestionProcessor.saveInstanceState(outState);
    }

    private void requestPermissionsIfNeeded(String action)
    {
        if (!Utilities.REQUEST_PERMISSION_ACTION.equals(action))
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            //TODO
            new AlertDialog.Builder(this)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> requestLocationPermissions())
                    .setTitle(getString(R.string.loc_perm_title))
                    .setMessage(getString(R.string.loc_perm_message))
                    .create()
                    .show();
        }
        else
        {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions()
    {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSIONS);
    }


    private void createNewInstance()
    {
        extractWeatherData();
        extractClothingData();
    }

    private void restoreInstance(Bundle savedInstanceState)
    {
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (currentWeather != null)
            fillTopWithData(currentWeather);
        suggestionProcessor.restoreInstanceState(savedInstanceState);
        extractClothingData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        suggestionProcessor.setListener(this);
        adapter.setData(suggestionProcessor.getSuggestedItems());
        IntentFilter filter = new IntentFilter(Utilities.WEATHER_ITEM_UPDATED_BROADCAST);
        filter.addAction(Utilities.REQUEST_PERMISSION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        extractWeatherData();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        suggestionProcessor.setListener(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(observer);
    }

    private void extractWeatherData()
    {
        WeatherPreferenceModule weatherPreference = new WeatherPreferenceModule(this);
        currentWeather = weatherPreference.extractWeatherItem();
        suggestionProcessor.setWeatherData(currentWeather);
        if (currentWeather == null)
        {
            noItemTv.setText(R.string.no_weather_data);
            noItemTv.setVisibility(View.VISIBLE);
            WeatherSyncAdapter.syncImmediately(this);
            return;
        }
        if (getString(R.string.no_weather_data).equals(noItemTv.getText().toString()))
            noItemTv.setVisibility(View.GONE);
        fillTopWithData(currentWeather);
    }

    private void extractClothingData()
    {
        Cursor cursor = getContentResolver().query(DataContract.ClothingEntry.CONTENT_URI, null, null, null, null);
        if (cursor == null)
            return;
        suggestionProcessor.extractDataFromCursor(cursor);
        cursor.close();
    }

    private void fillTopWithData(@NonNull WeatherItem weatherItem)
    {
        windTv.setText(Utilities.formatWindSpeed(this, weatherItem.getWindSpeed(), true));
        temperatureTv.setText(Utilities.formatTemperature(this, weatherItem.getTemperature(), true));
        weatherIv.setImageResource(Utilities.getWeatherIconDrawableRes(weatherItem.getWeatherType()));
        weatherIv.setContentDescription(Utilities.getWeatherDescription(weatherItem.getWeatherType(), this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.suggested_apparel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_item_list:
                if (interstitialAd.isLoaded())
                    interstitialAd.show();
                else
                    goToItemList();
                return true;
/*            case R.id.action_settings:
                return true;*/
            default:
                return false;
        }
    }

    private void goToItemList()
    {
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        Intent i = new Intent(this, ClothingActivity.class);
        startActivity(i, bundle);
    }

    @Override
    public void onItemLongClicked(ClothingItem item)
    {
        //TODO
    }

    @Override
    public void onSuggestionMade(ArrayList<ClothingItem> suggestedItems)
    {
        adapter.setData(suggestedItems);
        if (suggestedItems.size() > 0)
        {
            noItemTv.setVisibility(View.GONE);
        }
        else
        {
            noItemTv.setText(R.string.no_items_found_please_add_some);
            noItemTv.setVisibility(View.VISIBLE);
        }
    }

    private class WeatherChangeReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            requestPermissionsIfNeeded(action);

            if (Utilities.equals(action, Utilities.WEATHER_ITEM_UPDATED_BROADCAST))
                extractWeatherData();
        }
    }
}
