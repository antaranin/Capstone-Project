package example.com.capstoneproject;

import android.content.Context;
import android.support.annotation.DrawableRes;

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

    @DrawableRes
    public static int getClothingTypeDrawableRes(@ClothingItem.ClothingType int type)
    {
        switch (type)
        {
            case ClothingItem.T_SHIRT:
                return R.drawable.ic_t_shirt;
            case ClothingItem.HAT:
                throw new UnsupportedOperationException("Unsupported type => " + type);
            case ClothingItem.JACKET:
                throw new UnsupportedOperationException("Unsupported type => " + type);
            case ClothingItem.SHIRT:
                throw new UnsupportedOperationException("Unsupported type => " + type);
            case ClothingItem.SHOES:
                throw new UnsupportedOperationException("Unsupported type => " + type);
            case ClothingItem.TROUSERS:
                throw new UnsupportedOperationException("Unsupported type => " + type);
            default:
                throw new UnsupportedOperationException("Unsupported type => " + type);
        }
    }
}
