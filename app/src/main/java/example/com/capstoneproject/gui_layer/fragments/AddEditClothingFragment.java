package example.com.capstoneproject.gui_layer.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.model_layer.ClothingItem;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditClothingFragment extends Fragment
{
    private ClothingItem currentItem;
    @Setter
    private OnAddEditClothingInteractionListener listener;


    public AddEditClothingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_clothing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.water_res_btn)
    void onWaterResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.WATER_RES, currentItem);
    }

    @OnClick(R.id.wind_res_btn)
    void onWindResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.WIND_RES, currentItem);
    }

    @OnClick(R.id.cold_res_btn)
    void onColdResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.COLD_RES, currentItem);
    }

    public interface OnAddEditClothingInteractionListener
    {
        void onCallParam(@ItemResParamFragment.ParamType int paramType, ClothingItem item);
    }

}
