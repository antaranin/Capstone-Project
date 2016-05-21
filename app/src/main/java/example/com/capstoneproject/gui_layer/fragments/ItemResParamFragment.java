package example.com.capstoneproject.gui_layer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.gui_layer.FillableView;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import lombok.Setter;

public class ItemResParamFragment extends Fragment
{
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WATER_RES, WIND_RES, COLD_RES})
    public @interface ParamType {}
    public static final int WATER_RES = 1;
    public static final int WIND_RES = 2;
    public static final int COLD_RES = 3;

    @BindView(R.id.param_description_tv)
    TextView paramDescTv;

    @BindView(R.id.fillableView)
    FillableView fillableView;

    @ItemResParamFragment.ParamType
    private int currentParamType;

    @ClothingItem.Resistance
    private int resistance = ClothingItem.NO_RES;

    @Setter
    private OnResParamInteractionListener listener;

    public ItemResParamFragment()
    {
        // Required empty public constructor
    }

    public static ItemResParamFragment createFragment(@ParamType int initParamType, @Nullable  ClothingItem currentItem)
    {
        ItemResParamFragment fragment = new ItemResParamFragment();
        fragment.currentParamType = initParamType;
        if(currentItem != null)
        {
            switch (initParamType)
            {
                case WIND_RES:
                    fragment.resistance = currentItem.getWindResistance();
                    break;
                case WATER_RES:
                    fragment.resistance = currentItem.getWaterResistance();
                    break;
                case COLD_RES:
                    fragment.resistance = currentItem.getColdResistance();
                    break;
            }
        }
        return fragment;
    }

    @OnClick(R.id.confirm_fab)
    void onConfirmPressed()
    {
        if(listener != null)
            listener.onParamChosen(resistance, currentParamType);
    }

    @OnClick(R.id.cancel_fab)
    void onCancelPressed()
    {
        if(listener != null)
            listener.onParamEditCanceled();
    }



    @DebugLog
    public void setParamType(@ParamType int type)
    {
        currentParamType = type;
        if(getView() != null)
            resetTypeViews();
    }

    public void setCurrentItem(@Nullable  ClothingItem currentItem)
    {
        if(currentItem != null)
        {
            switch (currentParamType)
            {
                case WIND_RES:
                    resistance = currentItem.getWindResistance();
                    break;
                case WATER_RES:
                    resistance = currentItem.getWaterResistance();
                    break;
                case COLD_RES:
                    resistance = currentItem.getColdResistance();
                    break;
                default:
                    throw new RuntimeException("Unsupported currentParamType => " + currentParamType);
            }
        }
        else
        {
            resistance = ClothingItem.NO_RES;
        }
        if(getContext() != null)
            processResistance(resistance);
    }

    @DebugLog
    private void resetTypeViews()
    {
        fillableView.setMainImage(getMainIcon(currentParamType));
        fillableView.setFillerImage(getFillIcon(currentParamType));
        fillableView.setMaxFill(4);
        fillableView.setAnimateTouch(true);
        fillableView.setCurrentFill(resistanceToFill(resistance));
        fillableView.setOnFillChangedListener((currentFill, userInteraction) ->{
            if(userInteraction ) processResistance(fillToResistance(currentFill));
        });

        processResistance(resistance);

    }

    private void processResistance(@ClothingItem.Resistance int resistance)
    {
        this.resistance = resistance;
        paramDescTv.setText(getParamTypeResText(currentParamType, resistance));
    }

    private String getParamTypeResText(@ParamType  int paramType, @ClothingItem.Resistance int resistance)
    {
        switch (paramType)
        {
            case WIND_RES:
                return Utilities.getWindResistanceDesc(getContext(), resistance);
            case COLD_RES:
                return Utilities.getColdResistanceDesc(getContext(), resistance);
            case WATER_RES:
                return Utilities.getWaterResistanceDesc(getContext(), resistance);
            default:
                throw new RuntimeException("Unsupported param type => " + paramType);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_param, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        processResistance(resistance);
        resetTypeViews();
    }

    @DebugLog
    @DrawableRes
    private int getMainIcon(@ParamType int paramType)
    {
        switch (paramType)
        {
            case COLD_RES:
                return R.drawable.ic_temperature;
            case WATER_RES:
                return R.drawable.ic_drop;
            case WIND_RES:
                return R.drawable.ic_wind;
            default:
                throw new RuntimeException("Unsupported type => " + paramType);
        }
    }

    @DebugLog
    @DrawableRes
    private int getFillIcon(@ParamType int paramType)
    {
        switch (paramType)
        {
            case COLD_RES:
                return R.drawable.ic_temperature_fill;
            case WATER_RES:
                return R.drawable.ic_drop_filled;
            case WIND_RES:
                return R.drawable.ic_wind_fill;
            default:
                throw new RuntimeException("Unsupported type => " + paramType);
        }
    }

    private int resistanceToFill(@ClothingItem.Resistance int res)
    {
        return res;
    }

    @ClothingItem.Resistance
    private int fillToResistance(int fill)
    {
        return fill;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public interface OnResParamInteractionListener
    {
        void onParamChosen(@ClothingItem.Resistance int resistance, @ParamType int paramType);

        void onParamEditCanceled();
    }
}
