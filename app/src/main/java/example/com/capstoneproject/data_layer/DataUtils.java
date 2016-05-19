package example.com.capstoneproject.data_layer;

import android.content.ContentValues;

import example.com.capstoneproject.model_layer.ClothingItem;

/**
 * Created by Arin on 18/05/16.
 */
public class DataUtils
{

    public static ContentValues createValuesFromClothing(ClothingItem clothingItem)
    {
        ContentValues values = new ContentValues();
        values.put(DataContract.ClothingEntry.COLD_RES, clothingItem.getColdResistance());
        values.put(DataContract.ClothingEntry.WATER_RES, clothingItem.getWaterResistance());
        values.put(DataContract.ClothingEntry.WIND_RES, clothingItem.getWindResistance());
        values.put(DataContract.ClothingEntry.NAME, clothingItem.getName());
        values.put(DataContract.ClothingEntry.TYPE, clothingItem.getType());
        return values;
    }
}
