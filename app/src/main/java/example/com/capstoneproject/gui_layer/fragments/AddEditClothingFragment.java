package example.com.capstoneproject.gui_layer.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.DataContract;
import example.com.capstoneproject.data_layer.DataUtils;
import example.com.capstoneproject.gui_layer.FillableView;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import icepick.State;
import lombok.NonNull;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditClothingFragment extends Fragment
{
    private final static String PHOTO_FILE_TEMPLATE = "item_photo_%s.jpg";

    @BindView(R.id.no_photo_tv)
    TextView noPhotoTv;

    @BindView(R.id.item_name_et)
    EditText nameEt;

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

    @State
    ClothingItem currentItem;
    @State
    ClothingItem draftItem;

    @Setter
    private OnAddEditClothingInteractionListener listener;

    private NameChangeWatcher textWatcher;
    private boolean isLandTablet;


    public AddEditClothingFragment()
    {
        // Required empty public constructor
    }

    @Override
    @DebugLog
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    @DebugLog
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    @DebugLog
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_clothing, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    @DebugLog
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
/*        ClothingItem preSetCurrentItem = currentItem;
        ClothingItem preSetDraftItem = draftItem;*/
        textWatcher = new NameChangeWatcher();
        isLandTablet = getContext().getResources().getBoolean(R.bool.is_tablet_land);

/*        //This is necessary for when the items are set before restoration is called.
        if (preSetCurrentItem != null)
            currentItem = preSetCurrentItem;

        if (preSetDraftItem != null)
            draftItem = preSetDraftItem;*/

        ButterKnife.bind(this, view);
        if (currentItem == null && draftItem == null)
            draftItem = new ClothingItem();
        resetViews();
        setFabsToAddEditMode(isAdding() || isEditing());
        setEnableInput(isAdding() || isEditing());

    }

    @Override
    @DebugLog
    public void onResume()
    {
        super.onResume();
        //This was moved here from onViewsCreated because EditText is having problems with changing text at that point…
        ClothingItem displayedItem = draftItem == null ? currentItem : draftItem;
        changeDisplayedName(displayedItem.getName());
        nameEt.addTextChangedListener(textWatcher);
        setCurrentTitle();
    }

    private void setCurrentTitle()
    {
        if(isLandTablet)
            return;
        String title;
        if(isAdding())
            title = getContext().getString(R.string.add_item);
        else if(isEditing())
            title = getContext().getString(R.string.edit_item);
        else
            title = getContext().getString(R.string.view_item);
        //noinspection ConstantConditions
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        nameEt.removeTextChangedListener(textWatcher);
    }

    @OnClick(R.id.add_confirm_fab)
    void onAddConfirmBtnPressed()
    {
        if (isEditing() || isAdding())
            trySavingChanges();
        else
            startAddingNewItem();
    }

    @OnLongClick(R.id.item_photo_iv)
    boolean onPhotoIvPressed()
    {
        Crop.pickImage(getActivity(), this);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK)
        {
            //The id may either be actuall id or NO_ID for new items. This should later be reassigned
            File file = createEmptyFile(getActivity(), draftItem.getId());
            if (file != null)
            {
                Crop.of(data.getData(), Uri.fromFile(file))
                        .withAspect(itemPhotoIv.getWidth(), itemPhotoIv.getHeight())
                        .withMaxSize(itemPhotoIv.getWidth(), itemPhotoIv.getHeight())
                        .start(getActivity(), this);
            }
        }

        if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK)
        {
            File file = extractFileIfExists(getActivity(), draftItem.getId());
            draftItem.setImageUri(Uri.fromFile(file));
            log(String.format("File is file => %s, exists => %s can read => %s", file.isFile(), file.exists(), file.canRead()));
            resetViews();
        }
    }

    public File createEmptyFile(Context context, long id)
    {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getFilesDir(), String.format(PHOTO_FILE_TEMPLATE, id));
        try
        {
            boolean fileCreated = file.createNewFile();
            log("Created new file => " + fileCreated);
        }
        catch (IOException e)
        {
            log("IO exception while creating empty file => " + e);
        }
        return file;
    }

    public File extractFileIfExists(Context context, long id)
    {
        File file = new File(context.getFilesDir(), String.format(PHOTO_FILE_TEMPLATE, id));
        //log(String.format("File => %s, exists => %s, is file => %s", file.getAbsolutePath(), file.exists(), file.isFile()));
        if (file.exists() && file.isFile())
            return file;

        return null;
    }

    @DebugLog
    public void setItemForEdit(@NonNull ClothingItem item)
    {
        currentItem = item;
        draftItem = item.copy();
        if (getView() == null)
            return;

        resetViews();
        startEditingItem();
    }

    public void setItemForView(ClothingItem item)
    {
        currentItem = item;
        draftItem = null;
        if (getView() == null)
            return;

        resetViews();
        setFabsToAddEditMode(false);
        setEnableInput(false);
    }

    private void startAddingNewItem()
    {
        currentItem = null;
        draftItem = new ClothingItem();
        resetViews();
        setFabsToAddEditMode(true);
        setEnableInput(true);
        setCurrentTitle();
    }

    private void resetViews()
    {
        ClothingItem displayedItem = draftItem == null ? currentItem : draftItem;
        if (displayedItem == null)
            throw new AssertionError("Either draft item or current item cannot be null");

        waterResView.setCurrentFill(displayedItem.getWaterResistance());
        waterResView.setContentDescription(Utilities.getWaterResistanceContentDesc(getContext(), displayedItem.getWaterResistance()));
        windResView.setCurrentFill(displayedItem.getWindResistance());
        windResView.setContentDescription(Utilities.getWindResistanceContentDesc(getContext(), displayedItem.getWindResistance()));
        coldResView.setCurrentFill(displayedItem.getColdResistance());
        coldResView.setContentDescription(Utilities.getColdResistanceContentDesc(getContext(), displayedItem.getColdResistance()));
        clothingTypeIv.setImageResource(Utilities.getClothingTypeDrawableRes(displayedItem.getType()));
        clothingTypeIv.setContentDescription(Utilities.getClothingDesc(getContext(), displayedItem.getType()));
        File imageFile = extractFileIfExists(getContext(), displayedItem.getId());
        if (imageFile == null)
            noPhotoTv.setVisibility(View.VISIBLE);
        else
            noPhotoTv.setVisibility(View.GONE);
        Picasso.with(getContext())
                .load(imageFile)
                .into(itemPhotoIv);
        processConfirmationBtnColor();
        changeDisplayedName(displayedItem.getName());
    }

    @DebugLog
    private void changeDisplayedName(String name)
    {
        nameEt.setText(name != null ? name : "");
    }

    private void trySavingChanges()
    {
        log("Saving changes, draft item => " + draftItem);
        if (draftItem == null)
            throw new AssertionError("Draft item cannot be null when attempting to save changes");

        if (isSaveAcceptable(draftItem))
        {
            if (isAdding())
            {
                Uri resultUri = getContext().getContentResolver().insert(
                        DataContract.ClothingEntry.CONTENT_URI,
                        DataUtils.createValuesFromClothing(draftItem));

                draftItem.setId(DataContract.ClothingEntry.extractIdFromUri(resultUri));
                remapImageFileToId(draftItem);
            }
            else if (isEditing())
            {
                if (draftItem.getId() == ClothingItem.NO_ID)
                    throw new AssertionError("No id set in the draft item when it is updated. Item => " + draftItem);
                getContext().getContentResolver().update(
                        DataContract.ClothingEntry.CONTENT_URI,
                        DataUtils.createValuesFromClothing(draftItem),
                        String.format("%s = %s", DataContract.ClothingEntry._ID, draftItem.getId()),
                        null
                );
            }


            currentItem = draftItem;
            draftItem = null;
            resetViews();
            setFabsToAddEditMode(false);
            setEnableInput(false);
        }
        else
        {
            Toast.makeText(getContext(), "Not all data has been provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void remapImageFileToId(ClothingItem item)
    {
        File imageFile = extractFileIfExists(getContext(), ClothingItem.NO_ID);
        boolean reanameSuccess = imageFile.renameTo(new File(getContext().getFilesDir(), String.format(PHOTO_FILE_TEMPLATE, item.getId())));
        log("Successful rename => " + reanameSuccess);
        item.setImageUri(Uri.fromFile(extractFileIfExists(getContext(), item.getId())));
        getContext().getContentResolver().update(
                DataContract.ClothingEntry.CONTENT_URI,
                DataUtils.createValuesFromClothing(item),
                String.format("%s = %s", DataContract.ClothingEntry._ID, item.getId()),
                null
        );

    }

    private void setFabsToAddEditMode(boolean addEditMode)
    {
        if (addEditMode)
        {
            VectorDrawableCompat confirmVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_confirm, getContext().getTheme());
            addConfirmFab.setImageDrawable(confirmVector);
            addConfirmFab.setContentDescription(getString(R.string.confirm));

            VectorDrawableCompat cancelVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_clear, getContext().getTheme());
            editCancelFab.setImageDrawable(cancelVector);
            editCancelFab.setContentDescription(getString(R.string.cancel));
        }
        else
        {
            VectorDrawableCompat confirmVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_add, getContext().getTheme());
            addConfirmFab.setImageDrawable(confirmVector);
            addConfirmFab.setContentDescription(getString(R.string.add_item));

            VectorDrawableCompat cancelVector
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_edit, getContext().getTheme());
            editCancelFab.setImageDrawable(cancelVector);
            editCancelFab.setContentDescription(getString(R.string.edit_item));
        }
    }

    private void processConfirmationBtnColor()
    {
        if ((!isEditing() && !isAdding()) || isSaveAcceptable(draftItem))
        {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getContext().getTheme();
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            int color = typedValue.data;
            addConfirmFab.setBackgroundTintList(ColorStateList.valueOf(color));
            setCurrentTitle();
        }
        else
        {
            addConfirmFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.grayed_out_color)));
        }
    }

    private boolean isSaveAcceptable(@NonNull ClothingItem item)
    {
        return (currentItem == null || item.getId() == currentItem.getId()) && item.getName() != null
                && !item.getName().isEmpty() && item.getImageUri() != null;
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
        setEnableInput(true);
        setCurrentTitle();
    }

    private void cancelChanges()
    {
        if (isAdding())
        {
            File imageFile = extractFileIfExists(getContext(), draftItem.getId());
            if (imageFile != null)
                imageFile.delete();
        }

        if (currentItem == null)
        {
            listener.onAddingCanceled();
            return;
        }

        draftItem = null;
        resetViews();
        setFabsToAddEditMode(false);
        setEnableInput(false);
        setCurrentTitle();
    }

    private void setEnableInput(boolean enableInput)
    {
        if (enableInput)
        {
            waterResView.setClickable(true);
            windResView.setClickable(true);
            coldResView.setClickable(true);
            clothingTypeIv.setClickable(true);
            nameEt.setEnabled(true);
            itemPhotoIv.setLongClickable(true);
        }
        else
        {
            waterResView.setClickable(false);
            windResView.setClickable(false);
            coldResView.setClickable(false);
            clothingTypeIv.setClickable(false);
            nameEt.setEnabled(false);
            itemPhotoIv.setLongClickable(false);

        }
    }

    @DebugLog
    @OnClick(R.id.clothing_type_btn)
    void onClothingTypeBtnPressd()
    {
        if (listener != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listener.onRequestTypePick(draftItem, clothingTypeIv, Utilities.getClothingDesc(getContext(), draftItem.getType()));
            else
                listener.onRequestTypePick(draftItem);
        }
    }

    @DebugLog
    @OnClick(R.id.water_res_btn)
    void onWaterResBtnPressed()
    {
        if (listener != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listener.onRequestParamPick(ItemResParamFragment.WATER_RES, draftItem, waterResView, getString(R.string.main_item_transition));
            else
                listener.onRequestParamPick(ItemResParamFragment.WATER_RES, draftItem);
        }
    }

    @DebugLog
    @OnClick(R.id.wind_res_btn)
    void onWindResBtnPressed()
    {
        if (listener != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listener.onRequestParamPick(ItemResParamFragment.WIND_RES, draftItem, windResView, getString(R.string.main_item_transition));
            else
                listener.onRequestParamPick(ItemResParamFragment.WIND_RES, draftItem);
        }
    }

    @DebugLog
    @OnClick(R.id.cold_res_btn)
    void onColdResBtnPressed()
    {
        if (listener != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                listener.onRequestParamPick(ItemResParamFragment.COLD_RES, draftItem, coldResView, getString(R.string.main_item_transition));
            else
                listener.onRequestParamPick(ItemResParamFragment.COLD_RES, draftItem);
        }
    }

    private boolean isEditing()
    {
        return currentItem != null && draftItem != null;
    }

    private boolean isAdding()
    {
        return currentItem == null;
    }

    public void setItemColdRes(@ClothingItem.Resistance int resistance)
    {
        log("Cold res before setting => " + draftItem.getColdResistance());
        draftItem.setColdResistance(resistance);
        log("Cold res after setting => " + draftItem.getColdResistance());
        if(getView() != null)
            resetViews();
    }

    public void setItemWindRes(@ClothingItem.Resistance int resistance)
    {
        draftItem.setWindResistance(resistance);
        if(getView() != null)
            resetViews();
    }

    public void setItemWaterRes(@ClothingItem.Resistance int resistance)
    {
        draftItem.setWaterResistance(resistance);
        if(getView() != null)
            resetViews();
    }

    public void setItemType(@ClothingItem.ClothingType int type)
    {
        draftItem.setType(type);
        if(getView() != null)
            resetViews();
    }

    private void log(String message)
    {
        Log.d(this.getClass().getSimpleName(), message);
    }

    public void requestAddItem()
    {
        startAddingNewItem();
    }

    public interface OnAddEditClothingInteractionListener
    {
        void onRequestParamPick(@ItemResParamFragment.ParamType int paramType, ClothingItem item);

        void onRequestTypePick(ClothingItem item);

        void onRequestTypePick(ClothingItem item, View sharedView, String transition);

        void onAddingCanceled();

        void onRequestParamPick(int waterRes, ClothingItem draftItem, FillableView waterResView, String transitionName);
    }

    private class NameChangeWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        @DebugLog
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (draftItem != null)
            {
                draftItem.setName(s.toString());
                processConfirmationBtnColor();
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }
}
