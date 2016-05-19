package example.com.capstoneproject.data_layer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arin on 16/05/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    static final String DATABASE_NAME = "capstone.db";

    private static final String CREATE_CLOTHING_TABLE = String.format(
            "CREATE TABLE %s(" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL, %s INTEGER NOT NULL)",
            DataContract.ClothingEntry.TABLE_NAME,
            DataContract.ClothingEntry._ID,
            DataContract.ClothingEntry.NAME, DataContract.ClothingEntry.TYPE, DataContract.ClothingEntry.WATER_RES,
            DataContract.ClothingEntry.WIND_RES, DataContract.ClothingEntry.COLD_RES);

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_CLOTHING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
