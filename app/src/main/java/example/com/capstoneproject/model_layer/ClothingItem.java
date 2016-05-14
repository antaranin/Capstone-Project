package example.com.capstoneproject.model_layer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Arin on 09/05/16.
 */

@Data
public class ClothingItem implements Parcelable
{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({T_SHIRT, SHIRT, TROUSERS, JACKET, SHOES, HAT})
    public @interface ClothingType { }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_RES, LIGHT_RES, MEDIUM_RES, HIGH_RES, VERY_HIGH_RES})
    public @interface Resistance { }

    public final static int NO_RES = 0;
    public final static int LIGHT_RES = 1;
    public final static int MEDIUM_RES = 2;
    public final static int HIGH_RES = 3;
    public final static int VERY_HIGH_RES = 4;

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

    @SuppressWarnings("WrongConstant")
    protected ClothingItem(Parcel in)
    {
        name = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        waterResistance = in.readInt();
        windResistance = in.readInt();
        coldResistance = in.readInt();
        type = in.readInt();
    }

    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>()
    {
        @Override
        public ClothingItem createFromParcel(Parcel in)
        {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size)
        {
            return new ClothingItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeParcelable(imageUri, flags);
        dest.writeInt(waterResistance);
        dest.writeInt(windResistance);
        dest.writeInt(coldResistance);
        dest.writeInt(type);
    }

    @Resistance
    public int getColdResistance()
    {
        return coldResistance;
    }

    @Resistance
    public int getWindResistance()
    {
        return windResistance;
    }

    @Resistance
    public int getWaterResistance()
    {
        return waterResistance;
    }
}
