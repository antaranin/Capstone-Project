package example.com.capstoneproject.management_layer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.DataContract;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.data_layer.WeatherPreferenceModule;
import example.com.capstoneproject.management_layer.suggestion_processor.SuggestionProcessor;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;

public class WidgetRemoteViewsService extends RemoteViewsService
{
    @Override
    @DebugLog
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new ApparelRemoteViewsFactory();
    }

    private class ApparelRemoteViewsFactory implements RemoteViewsFactory
    {
        ArrayList<ClothingItem> data;
        SuggestionProcessor suggestionProcessor;

        @Override
        @DebugLog
        public void onCreate()
        {
            suggestionProcessor = new SuggestionProcessor();
            suggestionProcessor.setWorkInBackground(false);
        }

        @Override
        @DebugLog
        public void onDataSetChanged()
        {
            final long identityToken = Binder.clearCallingIdentity();
            WeatherItem currentWeather = extractCurrentWeather();
            Cursor cursor = extractItemData();
            suggestionProcessor.setWeatherData(currentWeather);
            suggestionProcessor.extractDataFromCursor(cursor);
            cursor.close();
            data = suggestionProcessor.getSuggestedItems();
            Binder.restoreCallingIdentity(identityToken);
        }

        private Cursor extractItemData()
        {
            return getContentResolver().query(DataContract.ClothingEntry.CONTENT_URI, null, null, null, null);
        }

        private WeatherItem extractCurrentWeather()
        {
            WeatherPreferenceModule pref = new WeatherPreferenceModule(WidgetRemoteViewsService.this);
            return pref.extractWeatherItem();
        }


        @Override
        public void onDestroy()
        {
        }

        @Override
        public int getCount()
        {
            if (data == null)
                return 0;
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position)
        {
            if (position == AdapterView.INVALID_POSITION ||
                    data == null || data.size() <= position)
                return null;

            ClothingItem dataPoint = data.get(position);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.card_fill_item);

            Bitmap itemImage = null;
            try
            {
                int prefferedWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());

                itemImage = Picasso.with(WidgetRemoteViewsService.this)
                        .load(dataPoint.getImageUri())
                        .get();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            views.setImageViewBitmap(R.id.card_photo_iv, itemImage);
            views.setTextViewText(R.id.item_name_tv, dataPoint.getName());
            views.setContentDescription(R.id.card_photo_iv, dataPoint.getName());

            return views;
        }

        @Override
        public RemoteViews getLoadingView()
        {
            return new RemoteViews(getPackageName(), R.layout.card_fill_item);
        }

        @Override
        public int getViewTypeCount()
        {
            return 1;
        }

        @Override
        public long getItemId(int position)
        {
            if(data == null || data.size() <= position)
                return 0;
            return data.get(position).getId();
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }
    }
}
