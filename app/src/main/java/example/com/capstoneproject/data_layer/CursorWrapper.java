package example.com.capstoneproject.data_layer;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * Created by Arin on 14/05/16.
 */
public abstract class CursorWrapper<T>
{
    @Getter(AccessLevel.PROTECTED)
    private Cursor cursor;

    public CursorWrapper(@NonNull Cursor cursor)
    {
        this.cursor = cursor;
    }

    public abstract T getItem();


    public int getCount()
    {
        return cursor.getCount();
    }


    public int getPosition()
    {
        return cursor.getPosition();
    }


    public boolean move(int offset)
    {
        return cursor.move(offset);
    }


    public boolean moveToPosition(int position)
    {
        return cursor.moveToPosition(position);
    }


    public boolean moveToFirst()
    {
        return cursor.moveToFirst();
    }


    public boolean moveToLast()
    {
        return cursor.moveToLast();
    }


    public boolean moveToNext()
    {
        return cursor.moveToNext();
    }


    public boolean moveToPrevious()
    {
        return cursor.moveToPrevious();
    }


    public boolean isFirst()
    {
        return cursor.isFirst();
    }


    public boolean isLast()
    {
        return cursor.isLast();
    }


    public boolean isBeforeFirst()
    {
        return cursor.isBeforeFirst();
    }


    public boolean isAfterLast()
    {
        return cursor.isAfterLast();
    }


    public void close()
    {
        cursor.close();
    }


    public boolean isClosed()
    {
        return cursor.isClosed();
    }


    public void registerContentObserver(ContentObserver observer)
    {
        cursor.registerContentObserver(observer);
    }


    public void unregisterContentObserver(ContentObserver observer)
    {
        cursor.unregisterContentObserver(observer);
    }


    public void registerDataSetObserver(DataSetObserver observer)
    {
        cursor.registerDataSetObserver(observer);
    }


    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        cursor.unregisterDataSetObserver(observer);
    }


    public void setNotificationUri(ContentResolver cr, Uri uri)
    {
        cursor.setNotificationUri(cr, uri);
    }


    public Uri getNotificationUri()
    {
        return cursor.getNotificationUri();
    }


    public boolean getWantsAllOnMoveCalls()
    {
        return cursor.getWantsAllOnMoveCalls();
    }

    @TargetApi(Build.VERSION_CODES.M)

    public void setExtras(Bundle extras)
    {
        cursor.setExtras(extras);
    }


    public Bundle getExtras()
    {
        return cursor.getExtras();
    }


    public Bundle respond(Bundle extras)
    {
        return cursor.respond(extras);
    }
}
