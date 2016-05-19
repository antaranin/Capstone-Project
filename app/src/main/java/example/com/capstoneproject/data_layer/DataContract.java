package example.com.capstoneproject.data_layer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Arin on 16/05/16.
 */
public class DataContract
{
    public static final String CONTENT_AUTHORITY = "example.com.capstoneproject";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLOTHING = "clothing";

    public static class ClothingEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLOTHING).build();

        public static final String CONTENT_TYPE = String.format("%s/%s/%s",
                ContentResolver.CURSOR_DIR_BASE_TYPE, CONTENT_AUTHORITY, PATH_CLOTHING);
        public static final String CONTENT_ITEM_TYPE = String.format("%s/%s/%s",
                ContentResolver.CURSOR_ITEM_BASE_TYPE, CONTENT_AUTHORITY, PATH_CLOTHING);

        public static final String TABLE_NAME = "clothing";
        public static final String NAME = "clothing_name";
        public static final String TYPE = "clothing_type";
        public static final String WATER_RES = "clothing_water_resistance";
        public static final String WIND_RES = "clothing_wind_resistance";
        public static final String COLD_RES = "clothing_cold_resistance";


        public static Uri buildClothingUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long extractIdFromUri(Uri uri)
        {
            return  ContentUris.parseId(uri);
        }
    }
}
