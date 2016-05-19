package example.com.capstoneproject.data_layer;

import android.database.Cursor;
import android.net.Uri;

import example.com.capstoneproject.model_layer.ClothingItem;
import lombok.NonNull;

/**
 * Created by Arin on 14/05/16.
 */
public class ClothingCursor extends CursorWrapper<ClothingItem>
{
    public ClothingCursor(@NonNull Cursor cursor)
    {
        super(cursor);
    }

    @Override
    public ClothingItem getItem()
    {
        Cursor c = getCursor();
        return ClothingItem.builder()
                .id(c.getLong(c.getColumnIndex(DataContract.ClothingEntry._ID)))
                .imageUri(Uri.parse(c.getString(c.getColumnIndex(DataContract.ClothingEntry.IMAGE_URI))))
                .coldResistance(c.getInt(c.getColumnIndex(DataContract.ClothingEntry.COLD_RES)))
                .waterResistance(c.getInt(c.getColumnIndex(DataContract.ClothingEntry.WATER_RES)))
                .windResistance(c.getInt(c.getColumnIndex(DataContract.ClothingEntry.WIND_RES)))
                .type(c.getInt(c.getColumnIndex(DataContract.ClothingEntry.TYPE)))
                .name(c.getString(c.getColumnIndex(DataContract.ClothingEntry.NAME)))
                .build();
    }
}
