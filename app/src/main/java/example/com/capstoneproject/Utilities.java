package example.com.capstoneproject;

import android.content.Context;

import example.com.capstoneproject.model_layer.ClothingItem;

/**
 * Created by Arin on 10/05/16.
 */
public class Utilities
{
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

    public static String getTemperatureResistanceDesc(Context context, @ClothingItem.Resistance int resistance)
    {
        switch (resistance)
        {
            case ClothingItem.NO_RES:
                return context.getString(R.string.no_temp_res);
            case ClothingItem.LIGHT_RES:
                return context.getString(R.string.light_temp_res);
            case ClothingItem.MEDIUM_RES:
                return context.getString(R.string.medium_temp_res);
            case ClothingItem.HIGH_RES:
                return context.getString(R.string.high_temp_res);
            case ClothingItem.VERY_HIGH_RES:
                return context.getString(R.string.very_high_temp_res);
            default:
                throw new AssertionError("Unsupported resistance level");
        }
    }

    public static String getClothingDesc(Context context, @ClothingItem.ClothingType int type)
    {

        //TODO  implement
        throw new UnsupportedOperationException("Not implemented");
/*        switch (type)
        {
            case ClothingItem.NO_RES:
                return context.getString(R.string.no_temp_res);
            case ClothingItem.LIGHT_RES:
                return context.getString(R.string.light_temp_res);
            case ClothingItem.MEDIUM_RES:
                return context.getString(R.string.medium_temp_res);
            case ClothingItem.HIGH_RES:
                return context.getString(R.string.high_temp_res);
            case ClothingItem.VERY_HIGH_RES:
                return context.getString(R.string.very_high_temp_res);
            default:
                throw new AssertionError("Unsupported resistance level");
        }*/
    }
}
