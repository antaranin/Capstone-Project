package example.com.capstoneproject.data_layer;

import android.database.Cursor;

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
        throw new UnsupportedOperationException("Not implemented");
    }
}
