package example.com.capstoneproject.management_layer.web_services;

import java.util.List;

import lombok.Data;

/**
 * Created by Arin on 20/05/16.
 */
@Data
public class WeatherResponse
{
    private List<WeatherHolder> weather;
    private MainWeatherDataHolder main;
    private WindHolder wind;
    private RainHolder rain;
}
