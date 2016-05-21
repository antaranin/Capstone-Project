package example.com.capstoneproject.data_layer;

import android.content.Context;
import android.support.annotation.Nullable;

import net.grandcentrix.tray.TrayModulePreferences;

/**
 * Created by Arin on 20/05/16.
 */
public class WeatherPreferenceModule extends TrayModulePreferences
{
    private static final String LOG_TAG = WeatherPreferenceModule.class.getSimpleName();
    private static final int VERSION = 1;
    private static final String MODULE_NAME = "WeatherModule";

    private static final String WEATHER_TYPE = "weather_type";
    private static final String WIND_SPEED = "wind_speed";
    private static final String TEMPERATURE = "temperature";
    private static final String PRECIPITATION = "precipitation";

    public WeatherPreferenceModule(Context context)
    {
        super(context, MODULE_NAME, VERSION);
    }

    public void setWeatherType(@WeatherItem.WeatherType int weatherType)
    {
        put(WEATHER_TYPE, weatherType);
    }

    public void setWindSpeed(int windSpeed)
    {
        put(WIND_SPEED, windSpeed);
    }

    public void setTemperature(int temperature)
    {
        put(TEMPERATURE, temperature);
    }

    public void setPrecipitation(float precipitation)
    {
        put(PRECIPITATION, precipitation);
    }


    public void setWeatherItem(@Nullable WeatherItem weatherItem)
    {
        if(weatherItem == null)
        {
            remove(WEATHER_TYPE);
            remove(WIND_SPEED);
            remove(TEMPERATURE);
            remove(PRECIPITATION);
        }
        else
        {
            setWeatherType(weatherItem.getWeatherType());
            setWindSpeed(weatherItem.getWindSpeed());
            setTemperature(weatherItem.getTemperature());
            setPrecipitation(weatherItem.getRainPrecipitate());
        }
    }

    @Nullable
    public WeatherItem extractWeatherItem()
    {
        final int noValue = -1;
        int weatherType = getInt(WEATHER_TYPE, noValue);
        int windSpeed = getInt(WIND_SPEED, noValue);
        int temperature = getInt(TEMPERATURE, noValue);
        float precipitation = getFloat(PRECIPITATION, noValue);
        if(weatherType == noValue || windSpeed == noValue || temperature == noValue || precipitation == noValue)
            return null;

        return WeatherItem.builder()
                .rainPrecipitate(precipitation)
                .windSpeed(windSpeed)
                .weatherType(weatherType)
                .temperature(temperature)
                .build();
    }

    @Override
    protected void onCreate(int initialVersion)
    {

    }

    @Override
    protected void onUpgrade(int oldVersion, int newVersion)
    {

    }
}
