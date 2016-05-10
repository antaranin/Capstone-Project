package example.com.capstoneproject.model_layer;

import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Arin on 09/05/16.
 */

@Data
public class ClothingItem
{
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_RES, LIGHT_RES, MEDIUM_RES, HIGH_RES, VERY_HIGH_RES})
    public @interface Resistance {}
    public final static int NO_RES = 0;
    public final static int LIGHT_RES = 1;
    public final static int MEDIUM_RES = 2;
    public final static int HIGH_RES = 3;
    public final static int VERY_HIGH_RES = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({T_SHIRT, SHIRT, TROUSERS, JACKET, SHOES, HAT})
    public @interface ClothingType {}
    private final static int T_SHIRT = 1;
    private final static int SHIRT = 2;
    private final static int TROUSERS = 3;
    private final static int JACKET = 4;
    private final static int SHOES = 5;
    private final static int HAT = 6;

    private String name;
    private Uri imageUri;

    @Resistance
    private int waterResistance;
    @Resistance
    private int windResistance;
    @Resistance
    private int coldResistance;
    @ClothingType
    private int type;

    public ClothingItem()
    {
    }

    @Builder
    public ClothingItem(String name, Uri imageUri, int waterResistance, int windResistance, int coldResistance, int type)
    {
        this.name = name;
        this.imageUri = imageUri;
        this.waterResistance = waterResistance;
        this.windResistance = windResistance;
        this.coldResistance = coldResistance;
        this.type = type;
    }
}
