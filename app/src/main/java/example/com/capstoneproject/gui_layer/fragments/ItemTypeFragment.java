package example.com.capstoneproject.gui_layer.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import icepick.State;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemTypeFragment extends Fragment
{
    private static final String TAG = ItemTypeFragment.class.getSimpleName();

    @BindView(R.id.type_imge_flipper)
    ViewFlipper imageFlipper;

    @State
    int currentTypePos = -1;

    private static Integer[] types = {ClothingItem.T_SHIRT, ClothingItem.SHIRT, ClothingItem.BLOUSE,
            ClothingItem.JACKET, ClothingItem.SKIRT, ClothingItem.TROUSERS};


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
        final GestureDetector detector = new GestureDetector(getContext(), new SlideListener());
        createFlipperViews();
        imageFlipper.setOnTouchListener((v, event) -> detector.onTouchEvent(event));

        if(savedInstanceState != null)
            restoreInstance(savedInstanceState);
        else
            createNewInstance();
    }

    private void createFlipperViews()
    {
        int textMargins = getResources().getDimensionPixelSize(R.dimen.large_margin_padding);
        int imageTopMargin = getResources().getDimensionPixelSize(R.dimen.medium_margin_padding);
        float textSize = getResources().getDimension(R.dimen.large_text_size);
        for (int i = 0; i < types.length; i++)
        {
            @ClothingItem.ClothingType
            int type = types[i];

            RelativeLayout rl = new RelativeLayout(getContext());
            rl.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            final String clothingDesc = Utilities.getClothingDesc(getContext(), type);
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(Utilities.getClothingTypeDrawableRes(type));
            iv.setContentDescription(clothingDesc);
            iv.setId(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                iv.setTransitionName(clothingDesc);
            TextView tv = new TextView(getContext());
            tv.setText(clothingDesc);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv.setGravity(Gravity.CENTER);
            tv.setId(i + types.length);
            rl.addView(iv);
            rl.addView(tv);

            RelativeLayout.LayoutParams ivLayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            ivLayParams.addRule(RelativeLayout.ABOVE, i + types.length);
            ivLayParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            ivLayParams.topMargin = imageTopMargin;
            iv.setLayoutParams(ivLayParams);

            RelativeLayout.LayoutParams tvLayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLayParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            tvLayParams.setMargins(textMargins, textMargins, textMargins, textMargins);
            tv.setLayoutParams(tvLayParams);
            imageFlipper.addView(rl);
        }
    }

    private void createNewInstance()
    {
        if(currentTypePos == -1)
            currentTypePos = 0;
        if(imageFlipper.getDisplayedChild() != currentTypePos)
            moveTo(currentTypePos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void restoreInstance(Bundle savedState)
    {
        int preSetCurrentTypePos = currentTypePos;
        Icepick.restoreInstanceState(this, savedState);
        //this is here to avoid overwriting value provided in setItem, if it was called before instance restoration
        if(preSetCurrentTypePos != -1)
            currentTypePos = preSetCurrentTypePos;
        moveTo(currentTypePos);
    }

    @OnClick(R.id.confirm_fab)
    void onConfirmBtnPressed()
    {
        if (listener != null)
            listener.onTypeSelected(types[currentTypePos]);
    }

    @OnClick(R.id.cancel_fab)
    void onCancelBtnPressed()
    {
        if (listener != null)
            listener.onTypeSelectionCancelled();
    }

    public void setItem(@Nullable ClothingItem item)
    {
        if (item == null)
            currentTypePos = 0;
        else
            currentTypePos = Arrays.asList(types).indexOf(item.getType());
        if(imageFlipper != null)
            moveTo(currentTypePos);
    }

    public static ItemTypeFragment createFragment(@Nullable ClothingItem item)
    {
        ItemTypeFragment fragment = new ItemTypeFragment();
        if (item == null)
            fragment.currentTypePos = 0;
        else
            fragment.currentTypePos = Arrays.asList(types).indexOf(item.getType());
        return fragment;
    }

    private void moveToNextItem()
    {
        currentTypePos ++;
        currentTypePos %= types.length;
        imageFlipper.setInAnimation(getActivity(), R.anim.right_in);
        imageFlipper.setOutAnimation(getActivity(), R.anim.left_out);
        imageFlipper.showNext();
    }

    private void moveToPreviousItem()
    {
        currentTypePos += types.length - 1; //We remove one and then add a whole length to keep modulo positive
        currentTypePos %= types.length;
        imageFlipper.setInAnimation(getActivity(), R.anim.left_in);
        imageFlipper.setOutAnimation(getActivity(), R.anim.right_out);
        imageFlipper.showPrevious();
    }

    private void moveTo(int position)
    {
        currentTypePos = position;
        imageFlipper.setDisplayedChild(position);
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
            if (velocityX > 100)
                moveToPreviousItem();
            else if (velocityX < -100)
                moveToNextItem();
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
