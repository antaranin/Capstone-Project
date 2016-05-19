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
    public final static long NO_ID = -1L;

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

    public final static int T_SHIRT = 0;
    public final static int SHIRT = 1;
    public final static int TROUSERS = 2;
    public final static int JACKET = 3;
    public final static int SHOES = 4;
    public final static int HAT = 5;

    private long id = NO_ID;
    private String name;
    private Uri imageUri;

    @Resistance
    private int waterResistance = NO_RES;
    @Resistance
    private int windResistance = NO_RES;
    @Resistance
    private int coldResistance = NO_RES;
    @ClothingType
    private int type = T_SHIRT;

    public ClothingItem()
    {
    }

    @Builder
    public ClothingItem(Long id, String name, int waterResistance, int windResistance, int coldResistance, int type, Uri imageUri)
    {
        this.id = id != null ? id : NO_ID;
        this.name = name;
        this.waterResistance = waterResistance;
        this.imageUri = imageUri;
        this.windResistance = windResistance;
        this.coldResistance = coldResistance;
        this.type = type;
    }

    @SuppressWarnings("WrongConstant")
    protected ClothingItem(Parcel in)
    {
        id = in.readLong();
        name = in.readString();
        imageUri = Uri.parse(in.readString());
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(imageUri.toString());
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

    @ClothingItem.ClothingType
    public int getType()
    {
        return type;
    }

    public ClothingItem copy()
    {
        return ClothingItem.builder()
                .id(id)
                .name(name)
                .type(type)
                .imageUri(imageUri)
                .coldResistance(coldResistance)
                .windResistance(windResistance)
                .waterResistance(waterResistance)
                .build();
    }
}
