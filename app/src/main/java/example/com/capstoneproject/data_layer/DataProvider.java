package example.com.capstoneproject.data_layer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Arin on 16/05/16.
 */
public class DataProvider extends ContentProvider
{
    private DatabaseHelper dbHelper;
    private UriMatcher uriMatcher = createMatcher();

    public static final int CLOTHING = 100;

    @Override
    public boolean onCreate()
    {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final int match = uriMatcher.match(uri);
        Cursor cursor;

        switch (match)
        {
            case CLOTHING:
                cursor = getClothing(projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Uri => " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getClothing(String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        return dbHelper
                .getReadableDatabase()
                .query(DataContract.ClothingEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = uriMatcher.match(uri);
        switch (match)
        {
            case CLOTHING:
                return DataContract.ClothingEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Uri not supported => " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)
    {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (match)
        {
            case CLOTHING:
                long _id = db.insert(DataContract.ClothingEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri =  DataContract.ClothingEntry.buildClothingUri(_id);
                else
                    throw new SQLiteException("Failed to insert row with uri => " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri => " + uri);
        }
        notifyResolverOfChange(uri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
    {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (match)
        {
            case CLOTHING:
                rowsDeleted = db.delete(DataContract.ClothingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri => " + uri);
        }

        if(rowsDeleted > 0)
            notifyResolverOfChange(uri);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final int match = uriMatcher.match(uri);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsUpdated;
        switch (match)
        {
            case CLOTHING:
                rowsUpdated = db.update(DataContract.ClothingEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri => " + uri);

        }
        if(rowsUpdated > 0)
            notifyResolverOfChange(uri);

        return rowsUpdated;
    }

    private void notifyResolverOfChange(Uri uri)
    {
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private static UriMatcher createMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DataContract.PATH_CLOTHING, CLOTHING);
        return matcher;
    }
}
