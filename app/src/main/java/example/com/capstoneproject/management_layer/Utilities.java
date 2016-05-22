package example.com.capstoneproject.management_layer;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.util.Pair;

import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.model_layer.ClothingItem;

/**
 * Created by Arin on 10/05/16.
 */
public class Utilities
{
    public static final String WEATHER_ITEM_UPDATED_BROADCAST = "weather_item_updated_broadcast";

    public static String getWaterResistanceDesc(Context context, @ClothingItem.Resistance int resistance)
    {
        switch (resistance)
        {
            case ClothingItem.NO_RES:
                return context.getString(R.string.no_water_res);
            case ClothingItem.LIGHT_RES:
                return context.getString(R.string.light_water_res);
            case ClothingItem.MEDIUM_RES:
                return context.getString(R.string.medium_water_res);
            case ClothingItem.HIGH_RES:
                return context.getString(R.string.high_water_res);
            case ClothingItem.VERY_HIGH_RES:
                return context.getString(R.string.very_high_water_res);
            default:
                throw new AssertionError("Unsupported resistance level");
        }
    }


    public static String getWindResistanceDesc(Context context, @ClothingItem.Resistance int resistance)
    {
        switch (resistance)
        {
            case ClothingItem.NO_RES:
                return context.getString(R.string.no_wind_res);
            case ClothingItem.LIGHT_RES:
                return context.getString(R.string.light_wind_res);
            case ClothingItem.MEDIUM_RES:
                return context.getString(R.string.medium_wind_res);
            case ClothingItem.HIGH_RES:
                return context.getString(R.string.high_wind_res);
            case ClothingItem.VERY_HIGH_RES:
                return context.getString(R.string.very_high_wind_res);
            default:
                throw new AssertionError("Unsupported resistance level");
        }
    }

    public static String getColdResistanceDesc(Context context, @ClothingItem.Resistance int resistance)
    {
        switch (resistance)
        {
            case ClothingItem.NO_RES:
                return context.getString(R.string.no_temp_res, 20);
            case ClothingItem.LIGHT_RES:
                return context.getString(R.string.light_temp_res, 15, 20);
            case ClothingItem.MEDIUM_RES:
                return context.getString(R.string.medium_temp_res, 5, 15);
            case ClothingItem.HIGH_RES:
                return context.getString(R.string.high_temp_res, 0, 5);
            case ClothingItem.VERY_HIGH_RES:
                return context.getString(R.string.very_high_temp_res, 0);
            default:
                throw new AssertionError("Unsupported resistance level");
        }
    }

    public static String getClothingDesc(Context context, @ClothingItem.ClothingType int type)
    {
        switch (type)
        {
            case ClothingItem.BLOUSE:
                return context.getString(R.string.blouse);
            case ClothingItem.JACKET:
                return context.getString(R.string.jacket);
            case ClothingItem.SHIRT:
                return context.getString(R.string.shirt);
            case ClothingItem.SKIRT:
                return context.getString(R.string.skirt);
            case ClothingItem.TROUSERS:
                return context.getString(R.string.trousers);
            case ClothingItem.T_SHIRT:
                return context.getString(R.string.t_shirt);
            default:
                throw new AssertionError("Unsupported type => " + type);
        }
    }

    @DrawableRes
    public static int getClothingTypeDrawableRes(@ClothingItem.ClothingType int type)
    {
        switch (type)
        {
            case ClothingItem.T_SHIRT:
                return R.drawable.ic_t_shirt;
            case ClothingItem.JACKET:
                return R.drawable.ic_jacket;
            case ClothingItem.SHIRT:
                return R.drawable.ic_shirt;
            case ClothingItem.TROUSERS:
                return R.drawable.ic_trousers;
            case ClothingItem.BLOUSE:
                return R.drawable.ic_blouse;
            case ClothingItem.SKIRT:
                return R.drawable.ic_skirt;
            default:
                throw new UnsupportedOperationException("Unsupported type => " + type);
        }
    }

    @DrawableRes
    public static int getWeatherIconDrawableRes(@WeatherItem.WeatherType int weatherType)
    {
        switch (weatherType)
        {
            case WeatherItem.CLEAR_SKY:
                return R.drawable.ic_clear_sky;
            case WeatherItem.CLOUDS:
                return R.drawable.ic_cloudy;
            case WeatherItem.FOG:
                return R.drawable.ic_fog;
            case WeatherItem.LIGHT_CLOUDS:
                return R.drawable.ic_light_clouds;
            case WeatherItem.LIGHT_RAIN:
                return R.drawable.ic_light_rain;
            case WeatherItem.RAIN:
                return R.drawable.ic_rain;
            case WeatherItem.SNOW:
                return R.drawable.ic_snow;
            case WeatherItem.STORM:
                return R.drawable.ic_storm;
            default:
                throw new AssertionError("Unexpected value => " + weatherType);
        }
    }

    public static String formatWindSpeed(Context context, float speed, boolean isMetric)
    {
        int windFormat;
        if (isMetric)
        {
            windFormat = R.string.format_wind_kmh;
        }
        else
        {
            windFormat = R.string.format_wind_mph;
            speed = 0.621371192237334f * speed;
        }
        return context.getString(windFormat, speed);
    }

    public static String formatTemperature(Context context, double temperature, boolean isMetric)
    {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        if (!isMetric)
            temperature = (temperature * 1.8) + 32;

        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean equals(Object object1, Object object2)
    {
        //It's more readable in multiple lines and overhead is minimal
        if(object1 == object2)
            return true;

        if(object1 == null || object2 == null)
            return false;

        if(object1.getClass() != object2.getClass())
            return false;

        return object1.equals(object2);
    }

    public static Pair<Integer, Integer> getColdResTemperatureFork(@ClothingItem.Resistance int coldRes)
    {
        switch (coldRes)
        {
            case ClothingItem.HIGH_RES:
                return new Pair<>(0, 10);
            case ClothingItem.LIGHT_RES:
                return new Pair<>(15, 20);
            case ClothingItem.MEDIUM_RES:
                return new Pair<>(10, 15);
            case ClothingItem.NO_RES:
                return new Pair<>(20, null);
            case ClothingItem.VERY_HIGH_RES:
                return new Pair<>(null, 0);
            default:
                throw new UnsupportedOperationException("Unsupported resistance level => " + coldRes);
        }
    }

    @ClothingItem.Resistance
    public static int getOptimalColdResFromTemperature(int temperature)
    {
        if(temperature < 0)
            return ClothingItem.VERY_HIGH_RES;
        else if(temperature < 10)
            return ClothingItem.HIGH_RES;
        else if(temperature < 15)
            return ClothingItem.MEDIUM_RES;
        else if(temperature < 20)
            return ClothingItem.LIGHT_RES;
        else
            return ClothingItem.NO_RES;
    }

    public static Pair<Integer, Integer> getWaterResMMFork(@ClothingItem.Resistance int waterRes)
    {
        switch (waterRes)
        {
            case ClothingItem.HIGH_RES:
                return new Pair<>(10, 40);
            case ClothingItem.LIGHT_RES:
                new Pair<>(2, 4);
            case ClothingItem.MEDIUM_RES:
                return new Pair<>(5, 9);
            case ClothingItem.NO_RES:
                return new Pair<>(0, 2);
            case ClothingItem.VERY_HIGH_RES:
                return new Pair<>(40, null);
            default:
                throw new UnsupportedOperationException("Unsupported resistance level => " + waterRes);
        }
    }

    @ClothingItem.Resistance
    public static int getOptimalWaterResForMM(int milimiterRainfall)
    {
        if(milimiterRainfall < 2)
            return ClothingItem.NO_RES;
        else if(milimiterRainfall < 4)
            return ClothingItem.LIGHT_RES;
        else if(milimiterRainfall < 9)
            return ClothingItem.MEDIUM_RES;
        else if(milimiterRainfall < 40)
            return ClothingItem.HIGH_RES;
        else
            return ClothingItem.VERY_HIGH_RES;
    }

    public static Pair<Integer, Integer> getWindResSpeedFork(@ClothingItem.Resistance int windRes)
    {
        switch (windRes)
        {
            case ClothingItem.HIGH_RES:
                return new Pair<>(39, 61);
            case ClothingItem.LIGHT_RES:
                return new Pair<>(6, 11);
            case ClothingItem.MEDIUM_RES:
                return new Pair<>(12, 28);
            case ClothingItem.NO_RES:
                return new Pair<>(0, 5);
            case ClothingItem.VERY_HIGH_RES:
                return new Pair<>(62, null);
            default:
                throw new UnsupportedOperationException("Unsupported resistance level => " + windRes);
        }
    }

    @ClothingItem.Resistance
    public static int getOptimalWindResForSpeed(int speed)
    {
        if(speed < 6)
            return ClothingItem.NO_RES;
        else if(speed < 12)
            return ClothingItem.LIGHT_RES;
        else if(speed < 39)
            return ClothingItem.MEDIUM_RES;
        else if(speed < 61)
            return ClothingItem.HIGH_RES;
        else
            return ClothingItem.VERY_HIGH_RES;
    }
}
