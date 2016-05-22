package example.com.capstoneproject.management_layer.suggestion_processor;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;
import android.util.SparseArray;

import com.googlecode.totallylazy.Sequence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import example.com.capstoneproject.data_layer.ClothingCursor;
import example.com.capstoneproject.data_layer.WeatherItem;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.model_layer.ClothingItem;
import example.com.capstoneproject.model_layer.methods.Func2;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import icepick.State;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import static com.googlecode.totallylazy.Sequences.sequence;

/**
 * Created by Arin on 21/05/16.
 */
public class SuggestionProcessor
{
    private static final String TAG = SuggestionProcessor.class.getSimpleName();
    @Setter
    @Getter
    private boolean workInBackground = true;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TORSO, LEGS})
    private @interface BodyPartCovered
    {
    }

    private static final int TORSO = 1;
    private static final int LEGS = 2;
    //private static final int HEAD = 3;
    //private static final int FEET = 4;

    @State
    ArrayList<ClothingItem> allItems;
    @State
    @Getter
    ArrayList<ClothingItem> suggestedItems = new ArrayList<>();
    @State
    WeatherItem currentWeather;

    @Setter
    private OnSuggestionMadeListener listener;

    public SuggestionProcessor()
    {
    }

    @DebugLog
    public void extractDataFromCursor(@NonNull  Cursor cursor)
    {
        ClothingCursor clothingCursor = new ClothingCursor(cursor);
        allItems = new ArrayList<>();
        while (clothingCursor.moveToNext())
        {
            allItems.add(clothingCursor.getItem());
        }

        onDataChanged();
    }

    public void saveInstanceState(Bundle outState)
    {
        Icepick.saveInstanceState(this, outState);
    }

    public void restoreInstanceState(Bundle savedState)
    {
        Icepick.restoreInstanceState(this, savedState);
        if (allItems != null && !allItems.isEmpty() && currentWeather != null && suggestedItems.isEmpty())
            onDataChanged();
    }

    @DebugLog
    public void setWeatherData(WeatherItem weatherData)
    {
        currentWeather = weatherData;
        onDataChanged();
    }

    private void onDataChanged()
    {
        suggestedItems.clear();

        if (currentWeather == null || allItems == null)
            return;

        //the closer the grade is to 0 the better, this means it is just perfect (not to little (-), not too much(+))
        final int[] coldResGrading = gradeItemsByCold(allItems, currentWeather.getTemperature());
        final int[] windResGrading = gradeItemsByWind(allItems, currentWeather.getWindSpeed());
        final int[] waterResGrading = gradeItemsByWater(allItems, currentWeather.getRainPrecipitate());

        if(workInBackground)
        {
            new AsyncTask<Void, Void, ArrayList<ClothingItem>>()
            {

                @Override
                protected ArrayList<ClothingItem> doInBackground(Void... params)
                {
                    ArrayList<GradedItem> gradedItems = createGradedItems(allItems, coldResGrading, windResGrading, waterResGrading);
                    return createSuggestion(gradedItems);
                }

                @Override
                protected void onPostExecute(ArrayList<ClothingItem> clothingItems)
                {
                    suggestedItems = clothingItems;
                    if (listener != null)
                        listener.onSuggestionMade(clothingItems);
                }
            }.execute();
        }
        else
        {
            ArrayList<GradedItem> gradedItems = createGradedItems(allItems, coldResGrading, windResGrading, waterResGrading);
            suggestedItems = createSuggestion(gradedItems);
            if (listener != null)
                listener.onSuggestionMade(suggestedItems);
        }
    }

    private ArrayList<GradedItem> createGradedItems(ArrayList<ClothingItem> items,
                                                    int[] coldResGrading, int[] windResGrading, int[] waterResGrading)
    {
        if (coldResGrading.length != windResGrading.length || windResGrading.length != waterResGrading.length
                || waterResGrading.length != items.size())
            throw new AssertionError("Provided data does not match in size");

        ArrayList<GradedItem> gradedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++)
        {
            GradedItem item = GradedItem.builder()
                    .item(items.get(i))
                    .coldResGrade(coldResGrading[i])
                    .windResGrade(windResGrading[i])
                    .waterResGrade(waterResGrading[i])
                    .build();
            gradedItems.add(item);
        }
        return gradedItems;
    }


    private int[] gradeItemsByWater(ArrayList<ClothingItem> items, float rainPrecipitate)
    {
        return gradeItems(items, rainPrecipitate, (item, rainMillis) -> {
            int optimalResistance = Utilities.getOptimalWaterResForMM(Math.round(rainPrecipitate));
            return Math.min(optimalResistance - item.getWaterResistance(), 0);
        });
    }

    private int[] gradeItemsByWind(ArrayList<ClothingItem> items, int windSpeed)
    {
        return gradeItems(items, windSpeed, (item, speed) -> {
            int optimalResistance = Utilities.getOptimalWindResForSpeed(Math.round(speed));
            return Math.min(optimalResistance - item.getWindResistance(), 0);
        });
    }

    private int[] gradeItemsByCold(ArrayList<ClothingItem> items, float temperature)
    {
        return gradeItems(items, temperature, (item, temp) -> {
            int optimalResistance = Utilities.getOptimalColdResFromTemperature(Math.round(temp));
            return item.getColdResistance() - optimalResistance;
        });
    }

    private int[] gradeItems(ArrayList<ClothingItem> items, float valueGradedAgainst, Func2<Integer, ClothingItem, Float> grader)
    {
        int[] grades = new int[items.size()];
        for (int i = 0; i < grades.length; i++)
        {
            grades[i] = grader.exec(items.get(i), valueGradedAgainst);
        }
        return grades;
    }

    private ArrayList<ClothingItem> createSuggestion(ArrayList<GradedItem> items)
    {
        Func2<Sequence<GradedItem>, ArrayList<GradedItem>, Integer> itemSeparator =
                (allItems, bodyCoveredPart) -> sequence(allItems).filter(i -> getBodyPartCovered(i.getItem().getType()) == bodyCoveredPart);
        Sequence<GradedItem> torsoItems = itemSeparator.exec(items, TORSO);
        Sequence<GradedItem> legItems = itemSeparator.exec(items, LEGS);

        ArrayList<ClothingItem> suggestions = new ArrayList<>();
        suggestions.addAll(createSubGroupSuggestion(torsoItems));
        suggestions.addAll(createSubGroupSuggestion(legItems));
        return suggestions;
    }

    private ArrayList<ClothingItem> createSubGroupSuggestion(Sequence<GradedItem> items)
    {
        SparseArray<List<GradedItem>> layeredItems = new SparseArray<>();
        for (GradedItem item : items)
        {
            int layer = getClothingLayer(item.getItem().getType());
            List<GradedItem> layerList = layeredItems.get(layer);
            if (layerList == null)
            {
                layerList = new ArrayList<>();
                layeredItems.put(layer, layerList);
            }
            layerList.add(item);
        }

        int[] layers = new int[layeredItems.size()];
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = layeredItems.keyAt(i);
        }

        List<GradedItemGroup> allItemsGrouped = layers.length > 0 ?
                groupCollect(layers, 0, layeredItems, new ArrayList<>()) : new ArrayList<>();

        log("All items grouped => " + allItemsGrouped);

        GradedItemGroup bestGroup = null;
        for (GradedItemGroup group : allItemsGrouped)
        {
            if (bestGroup == null)
                bestGroup = group;
            else if (bestGroup.getTotalGrade() > group.getTotalGrade())
                bestGroup = group;
        }
        if (bestGroup == null)
            return new ArrayList<>();
        return new ArrayList<>(sequence(bestGroup.getItems()).map(GradedItem::getItem));
    }

    private List<GradedItemGroup> groupCollect(int[] layers, int level,
                                               SparseArray<List<GradedItem>> layeredItems, List<GradedItem> gatheredItems)
    {
        List<GradedItemGroup> groups = new ArrayList<>();
        for (GradedItem item : layeredItems.get(layers[level]))
        {
            ArrayList<GradedItem> items = new ArrayList<>(gatheredItems);
            items.add(item);
            if (level == layers.length - 1)
                groups.add(new GradedItemGroup(items));
            else
                groups.addAll(groupCollect(layers, level + 1, layeredItems, items));
        }
        if (level != 0)
        {
            ArrayList<GradedItem> items = new ArrayList<>(gatheredItems);
            if (level == layers.length - 1)
                groups.add(new GradedItemGroup(items));
            else
                groups.addAll(groupCollect(layers, level + 1, layeredItems, items));

        }
        return groups;
    }


    @BodyPartCovered
    private int getBodyPartCovered(@ClothingItem.ClothingType int clothingType)
    {
        switch (clothingType)
        {
            case ClothingItem.JACKET:
                return TORSO;
            case ClothingItem.SHIRT:
                return TORSO;
            case ClothingItem.TROUSERS:
                return LEGS;
            case ClothingItem.T_SHIRT:
                return TORSO;
            case ClothingItem.BLOUSE:
                return TORSO;
            case ClothingItem.SKIRT:
                return LEGS;
            default:
                throw new UnsupportedOperationException("Unsupported type => " + clothingType);
        }
    }

    /**
     * Returns the layer at which the clothing is typically worn, with 0 being the most inner one.
     * Each higher layer can be worn above any lower one, but none of the lower layers can be worn over higher one.
     * This means that a Jacket can be worn over a Blouse or a T-shirt, Blouse can only be worn over T-shirt and not Jacket,
     * wheras T-shirt cannot be worn over anything.
     *
     * @param clothingType The type of clothing we are interested in.
     * @return The layer corresponding to provided clothing type.
     */
    private int getClothingLayer(@ClothingItem.ClothingType int clothingType)
    {
        switch (clothingType)
        {
            case ClothingItem.BLOUSE:
                return 1;
            case ClothingItem.JACKET:
                return 2;
            case ClothingItem.SHIRT:
                return 0;
            case ClothingItem.SKIRT:
                return 0;
            case ClothingItem.TROUSERS:
                return 0;
            case ClothingItem.T_SHIRT:
                return 0;
            default:
                throw new UnsupportedOperationException("Unsupported type => " + clothingType);
        }
    }

    private void log(String message)
    {
        Log.d(TAG, message);
    }

    public interface OnSuggestionMadeListener
    {
        void onSuggestionMade(ArrayList<ClothingItem> suggestedItems);
    }
}
