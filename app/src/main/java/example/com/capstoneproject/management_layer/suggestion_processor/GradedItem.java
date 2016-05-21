package example.com.capstoneproject.management_layer.suggestion_processor;

import example.com.capstoneproject.model_layer.ClothingItem;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Arin on 21/05/16.
 */

@Data
class GradedItem
{
    private final ClothingItem item;
    private final int coldResGrade;
    private final int windResGrade;
    private final int waterResGrade;

    @Builder
    public GradedItem(ClothingItem item, int coldResGrade, int windResGrade, int waterResGrade)
    {
        this.item = item;
        this.coldResGrade = coldResGrade;
        this.windResGrade = windResGrade;
        this.waterResGrade = waterResGrade;
    }
}

