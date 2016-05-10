package example.com.capstoneproject.gui_layer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.Utilities;
import example.com.capstoneproject.gui_layer.FillableView;

public class ItemParamFragment extends Fragment
{
    @Bind(R.id.param_description_tv)
    TextView paramDescTv;

    public ItemParamFragment()
    {
        // Required empty public constructor
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
        FillableView fillableView = ButterKnife.findById(view, R.id.fillableView);

        fillableView.setMainImage(R.drawable.ic_drop);
        fillableView.setFillerImage(R.drawable.ic_drop_filled);
        fillableView.setMaxFill(4);
        fillableView.setAnimateTouch(true);
        fillableView.setCurrentFill(1);
        //noinspection WrongConstant
        fillableView.setOnFillChangedListener((currentFill, userInteraction) ->{
            if(userInteraction ) paramDescTv.setText(Utilities.getWaterResistanceDesc(getContext(), currentFill));
        });
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
}
