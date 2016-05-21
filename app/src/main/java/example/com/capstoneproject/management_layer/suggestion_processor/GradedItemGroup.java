package example.com.capstoneproject.management_layer.suggestion_processor;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Data;

/**
 * Created by Arin on 21/05/16.
 */
@Data
class GradedItemGroup
{
    private final ArrayList<GradedItem> items;
    private final float coldResGrade;
    private final float windResGrade;
    private final float waterResGrade;
    private final float totalGrade;

    private static final int WIND_RES_IMPORTANCE = 1;
    private static final int COLD_RES_IMPORTANCE = 4;
    private static final int WATER_RES_IMPORTANCE = 3;

    public GradedItemGroup(ArrayList<GradedItem> items)
    {
        this.items = items;
        coldResGrade = calcColdResGrade(items);
        windResGrade = calcWindResGrade(items);
        waterResGrade = calcWaterResGrade(items);
        totalGrade = calcTotalGrade();
    }

    public GradedItemGroup(GradedItem... items)
    {
        this(new ArrayList<>(Arrays.asList(items)));
    }

    private float calcWindResGrade(ArrayList<GradedItem> items)
    {
        //Good enough for now
        return Math.abs(items.get(items.size() - 1).getWindResGrade());
    }

    private float calcWaterResGrade(ArrayList<GradedItem> items)
    {
        return Math.abs(items.get(items.size() - 1).getWaterResGrade());
    }

    private float calcColdResGrade(ArrayList<GradedItem> items)
    {
        float grade = 0;
        for (GradedItem item : items)
        {
            grade += Math.pow(2, item.getColdResGrade());
        }
        if(grade != 0)
            grade = log2(grade);
        return Math.abs(grade);
    }

    private float calcTotalGrade()
    {
        return coldResGrade * COLD_RES_IMPORTANCE + WIND_RES_IMPORTANCE * windResGrade + waterResGrade * WATER_RES_IMPORTANCE;
    }

    private float log2(float num)
    {
        return (float) (Math.log(num) / Math.log(2));
    }


}
