package example.com.capstoneproject.data_layer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Arin on 20/05/16.
 */
@Data
public class WeatherItem implements Parcelable
{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STORM, LIGHT_RAIN, RAIN, SNOW, FOG, CLEAR_SKY, LIGHT_CLOUDS, CLOUDS})
    public @interface WeatherType { }

    public static final int STORM = 1;
    public static final int LIGHT_RAIN = 2;
    public static final int RAIN = 3;
    public static final int SNOW = 4;
    public static final int FOG = 5;
    public static final int CLEAR_SKY = 6;
    public static final int LIGHT_CLOUDS = 7;
    public static final int CLOUDS = 8;


    @WeatherType
    private int weatherType;
    private int windSpeed;
    private float rainPrecipitate;
    private int temperature;

    protected WeatherItem(Parcel in)
    {
        //noinspection WrongConstant
        weatherType = in.readInt();
        windSpeed = in.readInt();
        rainPrecipitate = in.readFloat();
        temperature = in.readInt();
    }

    public WeatherItem()
    {
    }

    @Builder
    public WeatherItem(int weatherType, int windSpeed, float rainPrecipitate, int temperature)
    {
        this.weatherType = weatherType;
        this.windSpeed = windSpeed;
        this.rainPrecipitate = rainPrecipitate;
        this.temperature = temperature;
    }

    public static final Creator<WeatherItem> CREATOR = new Creator<WeatherItem>()
    {
        @Override
        public WeatherItem createFromParcel(Parcel in)
        {
            return new WeatherItem(in);
        }

        @Override
        public WeatherItem[] newArray(int size)
        {
            return new WeatherItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(weatherType);
        dest.writeInt(windSpeed);
        dest.writeFloat(rainPrecipitate);
        dest.writeInt(temperature);
    }

    @WeatherType
    public int getWeatherType()
    {
        return weatherType;
    }

    public void setWeatherType(@WeatherType  int weatherType)
    {
        this.weatherType = weatherType;
    }

    @WeatherType
    public static int translateWeatherIdToWeatherType(int weatherId)
    {
        if (weatherId >= 200 && weatherId <= 232)
            return STORM;
        else if (weatherId >= 300 && weatherId <= 321)
            return LIGHT_RAIN;
        else if (weatherId >= 500 && weatherId <= 504)
            return RAIN;
        else if (weatherId == 511)
            return SNOW;
        else if (weatherId >= 520 && weatherId <= 531)
            return RAIN;
        else if (weatherId >= 600 && weatherId <= 622)
            return SNOW;
        else if (weatherId >= 701 && weatherId <= 761)
            return FOG;
        else if (weatherId == 761 || weatherId == 781)
            return STORM;
        else if (weatherId == 800)
            return CLEAR_SKY;
        else if (weatherId == 801)
            return LIGHT_CLOUDS;
        else if (weatherId >= 802 && weatherId <= 804)
            return CLOUDS;

        return CLEAR_SKY;
    }
}
