package example.com.capstoneproject.gui_layer.fragments;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemTypeFragment extends Fragment
{
    private static final String TAG = ItemTypeFragment.class.getSimpleName();
    @BindView(R.id.type_imge_flipper)
    ViewFlipper imageFlipper;

    @ClothingItem.ClothingType
    private int currentType = ClothingItem.T_SHIRT;

    private int[] drawables= { R.drawable.ic_t_shirt, R.drawable.ic_wind, R.drawable.ic_wind_fill};
    private String[] typeDescs;

    @Setter
    private OnTypeInteractionListener listener;

    public ItemTypeFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        typeDescs = getContext().getResources().getStringArray(R.array.clothing_type_descs);

        for (int i = 0; i < drawables.length; i++)
        {
            RelativeLayout rl = new RelativeLayout(getContext());
            rl.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(drawables[i]);
            iv.setId(i);
            TextView tv = new TextView(getContext());
            tv.setText(typeDescs[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setId(i + drawables.length);
            rl.addView(iv);
            rl.addView(tv);

            RelativeLayout.LayoutParams ivLayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            ivLayParams.addRule(RelativeLayout.ABOVE, i + drawables.length);
            ivLayParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            iv.setLayoutParams(ivLayParams);

            RelativeLayout.LayoutParams tvLayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLayParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tv.setLayoutParams(tvLayParams);
            imageFlipper.addView(rl);
        }
        final GestureDetector detector = new GestureDetector(getContext(), new SlideListener());
        imageFlipper.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
/*        imageFlipper.setOnTouchListener((v, event) -> {
            Log.d(TAG, "onViewCreated: touchy touchy");
            return true;
        });*/
    }

    @OnClick(R.id.confirm_fab)
    void onConfirmBtnPressed()
    {
        if(listener != null)
            listener.onTypeSelected(currentType);
    }

    @OnClick(R.id.cancel_fab)
    void onCancelBtnPressed()
    {
        if(listener != null)
            listener.onTypeSelectionCancelled();
    }

    public void setItem(@Nullable  ClothingItem item)
    {
        if(item == null)
            currentType = ClothingItem.T_SHIRT;
        else
            currentType = item.getType();
    }

    public static ItemTypeFragment createFragment(@Nullable  ClothingItem item)
    {
        ItemTypeFragment fragment = new ItemTypeFragment();
        if(item == null)
            fragment.currentType = ClothingItem.T_SHIRT;
        else
            fragment.currentType = item.getType();
        return fragment;
    }

    private class SlideListener extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        @DebugLog
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        @DebugLog
        public boolean onSingleTapUp(MotionEvent e)
        {
            return super.onSingleTapUp(e);
        }

        @DebugLog
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if(velocityX > 100)
                imageFlipper.showPrevious();
            else if(velocityX < -100)
                imageFlipper.showNext();
            else
                return false;
            return true;
        }
    }

    public interface OnTypeInteractionListener
    {
        void onTypeSelected(@ClothingItem.ClothingType int type);

        void onTypeSelectionCancelled();
    }
}
