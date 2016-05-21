package example.com.capstoneproject.gui_layer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.data_layer.WeatherPreferenceModule;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.management_layer.web_services.WeatherSyncAdapter;
import icepick.Icepick;
import icepick.State;
import lombok.NonNull;

public class SuggestedApparelActivity extends AppCompatActivity
{
    @BindView(R.id.current_weather_iv)
    ImageView weatherIv;

    @BindView(R.id.current_wind_tv)
    TextView windTv;

    @BindView(R.id.current_temp_tv)
    TextView temperatureTv;

    @State
    WeatherItem currentWeather;

    private WeatherChangeReceiver receiver = new WeatherChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_apparel);

        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState != null)
            restoreInstance(savedInstanceState);
        else
            extractWeatherData();
    }

    private void restoreInstance(Bundle savedInstanceState)
    {
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (currentWeather != null)
            fillTopWithData(currentWeather);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter(Utilities.WEATHER_ITEM_UPDATED_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        extractWeatherData();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void extractWeatherData()
    {
        WeatherPreferenceModule weatherPreference = new WeatherPreferenceModule(this);
        currentWeather = weatherPreference.extractWeatherItem();
        if (currentWeather == null)
        {
            WeatherSyncAdapter.syncImmediately(this);
            return;
        }

        fillTopWithData(currentWeather);
    }

    private void fillTopWithData(@NonNull WeatherItem weatherItem)
    {
        windTv.setText(Utilities.formatWindSpeed(this, weatherItem.getWindSpeed(), true));
        temperatureTv.setText(Utilities.formatTemperature(this, weatherItem.getTemperature(), true));
        weatherIv.setImageResource(Utilities.getWeatherIconDrawableRes(weatherItem.getWeatherType()));
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
                goToItemList();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }

    private void goToItemList()
    {
        Intent i = new Intent(this, ClothingActivity.class);
        startActivity(i);
    }

    private class WeatherChangeReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(!Utilities.equals(action, Utilities.WEATHER_ITEM_UPDATED_BROADCAST))
                return;

            extractWeatherData();
        }
    }
}
