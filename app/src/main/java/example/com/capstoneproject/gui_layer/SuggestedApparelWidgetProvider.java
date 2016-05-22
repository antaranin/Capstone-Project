package example.com.capstoneproject.gui_layer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import example.com.capstoneproject.R;
import example.com.capstoneproject.gui_layer.activities.ClothingActivity;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.management_layer.WidgetRemoteViewsService;
import hugo.weaving.DebugLog;

/**
 * Implementation of App Widget functionality.
 */
public class SuggestedApparelWidgetProvider extends AppWidgetProvider
{
    @DebugLog
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId)
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.suggested_apparel_widget);

        Intent intent = new Intent(context, ClothingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetMainLayout, pendingIntent);
        // Instruct the widget manager to update the widget
        views.setRemoteAdapter(R.id.widget_grid,
                new Intent(context, WidgetRemoteViewsService.class));
        views.setEmptyView(R.id.widget_grid, R.id.widget_empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    @DebugLog
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        if(!Utilities.WEATHER_ITEM_UPDATED_BROADCAST.equals(intent.getAction())
                && !Utilities.CLOTHING_ITEMS_CHANGED_BROADCAST.equals(intent.getAction()))
            return;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

