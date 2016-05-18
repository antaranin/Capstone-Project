package example.com.capstoneproject.gui_layer.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.Utilities;
import example.com.capstoneproject.data_layer.DataContract;
import example.com.capstoneproject.data_layer.DataUtils;
import example.com.capstoneproject.gui_layer.FillableView;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import lombok.NonNull;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditClothingFragment extends Fragment
{
    private final static String TAG = AddEditClothingFragment.class.getSimpleName();

    @BindView(R.id.add_confirm_fab)
    FloatingActionButton addConfirmFab;

    @BindView(R.id.edit_cancel_fab)
    FloatingActionButton editCancelFab;

    @BindView(R.id.clothing_type_btn)
    ImageView clothingTypeIv;

    @BindView(R.id.water_res_btn)
    FillableView waterResView;

    @BindView(R.id.wind_res_btn)
    FillableView windResView;

    @BindView(R.id.cold_res_btn)
    FillableView coldResView;

    @BindView(R.id.item_photo_iv)
    ImageView itemPhotoIv;

    private ClothingItem currentItem;
    private ClothingItem draftItem;

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
    @DebugLog
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if(currentItem == null && draftItem == null)
            draftItem = new ClothingItem();
        resetViews();
        setFabsToAddEditMode(isAdding() || isEditing());
    }

    @OnClick(R.id.add_confirm_fab)
    void onAddConfirmBtnPressed()
    {
        if (isEditing() || isAdding())
            trySavingChanges();
        else
            startCreatingNewItem();
    }

    public void setItem(@Nullable  ClothingItem item)
    {
        if(item != null)
        {
            currentItem = item;
            draftItem = null;
        }
        else
        {
            currentItem = null;
            draftItem = new ClothingItem();
        }
        if(getView() != null)
        {
            resetViews();
            setFabsToAddEditMode(isAdding() || isEditing());
        }
    }

    private void startCreatingNewItem()
    {
        currentItem = null;
        draftItem = new ClothingItem();
        resetViews();
        setFabsToAddEditMode(true);
    }

    private void resetViews()
    {
        ClothingItem displayedItem = draftItem == null ? currentItem : draftItem;
        if (displayedItem == null)
            throw new AssertionError("Either draft item or current item cannot be null");

        waterResView.setCurrentFill(displayedItem.getWaterResistance());
        windResView.setCurrentFill(displayedItem.getWaterResistance());
        coldResView.setCurrentFill(displayedItem.getColdResistance());
        clothingTypeIv.setImageResource(Utilities.getClothingTypeDrawableRes(displayedItem.getType()));
        itemPhotoIv.setImageURI(displayedItem.getImageUri());
        processConfirmationBtnColor();
    }

    private void trySavingChanges()
    {
        if (draftItem == null)
            throw new AssertionError("Draft item cannot be null when attempting to save changes");

        if (isSaveAcceptable(draftItem))
        {
            getContext().getContentResolver().insert(
                    DataContract.ClothingEntry.CONTENT_URI,
                    DataUtils.createValuesFromClothing(draftItem));

            currentItem = draftItem;
            draftItem = null;
            resetViews();
            setFabsToAddEditMode(false);
        }
        else
        {
            Toast.makeText(getContext(), "Not all data has been provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFabsToAddEditMode(boolean addEditMode)
    {
        if (addEditMode)
        {
            VectorDrawableCompat confirmVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_confirm, getContext().getTheme());
            addConfirmFab.setImageDrawable(confirmVector);

            VectorDrawableCompat cancelVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_clear, getContext().getTheme());
            editCancelFab.setImageDrawable(cancelVector);
        }
        else
        {
            VectorDrawableCompat confirmVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_add, getContext().getTheme());
            addConfirmFab.setImageDrawable(confirmVector);

            VectorDrawableCompat cancelVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_edit, getContext().getTheme());
            editCancelFab.setImageDrawable(cancelVector);        }
    }

    private void processConfirmationBtnColor()
    {
        if (!isEditing() || !isAdding())
            return;

        if (isSaveAcceptable(draftItem))
        {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getContext().getTheme();
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            int color = typedValue.data;
            addConfirmFab.setBackgroundColor(color);

        }
        else
        {
            addConfirmFab.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grayed_out_color));
        }
    }

    private boolean isSaveAcceptable(@NonNull ClothingItem item)
    {
        return (currentItem == null || item.getId() == currentItem.getId()) && item.getName() != null && item.getImageUri() != null;
    }

    @OnClick(R.id.edit_cancel_fab)
    void onEditCancelBtnPressed()
    {
        if (isEditing() || isAdding())
            cancelChanges();
        else
            startEditingItem();
    }

    private void startEditingItem()
    {
        if (currentItem == null)
            throw new AssertionError("Trying to edit non - existing item");

        draftItem = currentItem.copy();
        setFabsToAddEditMode(true);
    }

    private void cancelChanges()
    {
        if (currentItem == null)
        {
            listener.onAddingCanceled();
            return;
        }

        draftItem = null;
        resetViews();
        setFabsToAddEditMode(false);
    }

    @DebugLog
    @OnClick(R.id.clothing_type_btn)
    void onClothingTypeBtnPressd()
    {
        if (listener != null)
            listener.onCallType(draftItem);
    }

    @DebugLog
    @OnClick(R.id.water_res_btn)
    void onWaterResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.WATER_RES, draftItem);
    }

    @DebugLog
    @OnClick(R.id.wind_res_btn)
    void onWindResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.WIND_RES, draftItem);
    }

    @DebugLog
    @OnClick(R.id.cold_res_btn)
    void onColdResBtnPressed()
    {
        if (listener != null)
            listener.onCallParam(ItemResParamFragment.COLD_RES, draftItem);
    }

    private boolean isEditing()
    {
        return currentItem != null && draftItem != null;
    }

    private boolean isAdding()
    {
        return currentItem == null;
    }

    public interface OnAddEditClothingInteractionListener
    {
        void onCallParam(@ItemResParamFragment.ParamType int paramType, ClothingItem item);

        void onCallType(ClothingItem item);

        void onAddingCanceled();
    }
}
