package example.com.capstoneproject.management_layer.web_services;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by Arin on 20/05/16.
 */
@Data
public class RainHolder
{

    @SerializedName("3h")
    private float precipitationPer3H;
}
