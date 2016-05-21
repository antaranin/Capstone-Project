package example.com.capstoneproject.management_layer.web_services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import example.com.capstoneproject.BuildConfig;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.data_layer.WeatherPreferenceModule;
import example.com.capstoneproject.management_layer.Utilities;
import hugo.weaving.DebugLog;
import lombok.NonNull;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WeatherUpdateService extends IntentService
{
    private final static String FORECAST_BASE_URL_TEMPLATE = "http://api.openweathermap.org/data/2.5/weather?";
    public final static String CHECK_WEATHER_FOR_LOCATION = "location_broadcast";
    private static final String TAG = WeatherUpdateService.class.getSimpleName();

    public WeatherUpdateService()
    {
        super("WeatherUpdateService");
    }

    @Override
    @DebugLog
    protected void onHandleIntent(Intent intent)
    {
        log("Intent not null => " + (intent != null));
        if (intent != null)
        {
            final String action = intent.getAction();
            log("Action => " + action);
            if (CHECK_WEATHER_FOR_LOCATION.equals(action))
            {
                log("Location has result => " + LocationResult.hasResult(intent));
                if (!LocationResult.hasResult(intent))
                    return;

                LocationResult result = LocationResult.extractResult(intent);
                Location userLocation = result.getLastLocation();
                log("user location => " + userLocation);

                WeatherResponse response = requestTodayForecast(userLocation);
                processWeatherResponse(response);
            }
        }
    }

    @DebugLog
    private WeatherResponse requestTodayForecast(@NonNull Location userLocation)
    {
        final int timeout = 1000 * 10;
        final String latKey = "lat";
        final String lonKey = "lon";
        final String unitKey = "units";
        final String appidKey = "APPID";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FORECAST_BASE_URL_TEMPLATE)
                .queryParam(latKey, userLocation.getLatitude())
                .queryParam(lonKey, userLocation.getLongitude())
                .queryParam(unitKey, "metric")
                .queryParam(appidKey, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
/*        Map<String, Object> paramMapping = new HashMap<>();
        paramMapping.put(latKey, userLocation.getLatitude());
        paramMapping.put(lonKey, userLocation.getLongitude());
        paramMapping.put(appidKey, BuildConfig.OPEN_WEATHER_MAP_API_KEY);*/

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        try
        {
            String uriString= builder.build().encode().toUriString();
            log("uri string => " + uriString);
            return restTemplate.getForObject(uriString, WeatherResponse.class);
        }
        catch (RestClientException e)
        {
            log("Exception while retrieving weather information => " + e);
        }
        return null;
    }

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

        WeatherPreferenceModule preferenceModule = new WeatherPreferenceModule(this);
        preferenceModule.setWeatherItem(weatherItem);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Utilities.WEATHER_ITEM_UPDATED_BROADCAST));
    }

    @DebugLog
    private boolean isValidResponse(WeatherResponse response)
    {
        return response != null && response.getMain() != null && response.getWind() != null && response.getRain() != null
                && response.getWeather() != null && !response.getWeather().isEmpty();
    }

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
}
