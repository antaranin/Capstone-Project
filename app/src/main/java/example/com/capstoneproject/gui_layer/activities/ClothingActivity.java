package example.com.capstoneproject.gui_layer.activities;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import example.com.capstoneproject.R;
import example.com.capstoneproject.gui_layer.fragments.AddEditClothingFragment;
import example.com.capstoneproject.gui_layer.fragments.ClothingListFragment;
import example.com.capstoneproject.gui_layer.fragments.ItemResParamFragment;
import example.com.capstoneproject.gui_layer.fragments.ItemTypeFragment;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import lombok.NonNull;

public class ClothingActivity extends AppCompatActivity implements
        ClothingListFragment.OnListInteractionListener, AddEditClothingFragment.OnAddEditClothingInteractionListener,
        ItemResParamFragment.OnResParamInteractionListener, ItemTypeFragment.OnTypeInteractionListener
{
    private static final String CURRENT_OPERATION_KEY = "current_operation_key";
    private static final String TAG = ClothingActivity.class.getSimpleName();
    private static final String WAS_LAND_TABLET_KEY = "was_land_tablet_key";
    private ClothingListFragment clothingListFragment;
    private AddEditClothingFragment addEditClothingFragment;
    private ItemResParamFragment itemResParamFragment;
    private ItemTypeFragment itemTypeFragment;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIEWING, ADDING_EDITING, PARAMETER_SETTING, TYPE_SETTING})
    private @interface Operation
    {
    }

    private static final int VIEWING = 1;
    private static final int ADDING_EDITING = 2;
    private static final int PARAMETER_SETTING = 3;
    private static final int TYPE_SETTING = 4;

    @ClothingActivity.Operation

    private boolean isLandTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        isLandTablet = getResources().getBoolean(R.bool.is_tablet_land);
        if (isLandTablet)
            getSupportActionBar().setTitle(getString(R.string.manage_clothing));

        if (savedInstanceState == null)
            createNewInstance();
        else
            restoreInstance(savedInstanceState);
    }

    private void createNewInstance()
    {
        showClothingListFragment();
        if (isLandTablet)
            showAddEditClothingFragment();
    }

    private void restoreInstance(@NonNull Bundle savedInstance)
    {
        Icepick.restoreInstanceState(this, savedInstance);
        @Operation
        int currentOperation = savedInstance.getInt(CURRENT_OPERATION_KEY);

        boolean wasLandTablet = savedInstance.getBoolean(WAS_LAND_TABLET_KEY);

        final FragmentManager supportFragmentManager = getSupportFragmentManager();

        itemResParamFragment =
                (ItemResParamFragment) supportFragmentManager.getFragment(savedInstance, ItemResParamFragment.class.getSimpleName());
        if (itemResParamFragment != null)
            itemResParamFragment.setListener(this);

        itemTypeFragment =
                (ItemTypeFragment) supportFragmentManager.getFragment(savedInstance, ItemTypeFragment.class.getSimpleName());
        if (itemTypeFragment != null)
            itemTypeFragment.setListener(this);

        addEditClothingFragment =
                (AddEditClothingFragment) supportFragmentManager.getFragment(savedInstance, AddEditClothingFragment.class.getSimpleName());
        if (addEditClothingFragment != null)
            addEditClothingFragment.setListener(this);

        clothingListFragment =
                (ClothingListFragment) supportFragmentManager.getFragment(savedInstance, ClothingListFragment.class.getSimpleName());
        clothingListFragment.setListener(this);

        boolean orientationChanged = isLandTablet != wasLandTablet;
        if(orientationChanged)
            processTabletOrientationChange(currentOperation);

/*        if(isLandTablet)
        {
            showAddEditClothingFragment();
            if(currentOperation == PARAMETER_SETTING)
                placeItemResParamFragment(itemResParamFragment);
            else if(currentOperation == TYPE_SETTING)
                placeItemTypeFragment(itemTypeFragment);
        }
        else
        {
            if(currentOperation != VIEWING)
                showAddEditClothingFragment();

            if(currentOperation == PARAMETER_SETTING)
                placeItemResParamFragment(itemResParamFragment);
            else if(currentOperation == TYPE_SETTING)
                placeItemTypeFragment(itemTypeFragment);
        }*/
    }

    private void processTabletOrientationChange(@Operation int currentOperation)
    {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction tran = fm.beginTransaction();
        if(fm.getFragments().contains(addEditClothingFragment))
        {
            tran.remove(addEditClothingFragment);
            log("Removing add edit clothing fragment");
        }
/*        if(fm.getFragments().contains(itemTypeFragment))
        {
            tran.remove(itemTypeFragment);
            log("Removing item type fragment");
        }
        if(fm.getFragments().contains(itemResParamFragment))
        {
            tran.remove(itemResParamFragment);
            log("Removing item res fragment");
        }*/
        tran.commit();

        fm.executePendingTransactions();

        if (currentOperation != VIEWING || isLandTablet)
            showAddEditClothingFragment();

        if(currentOperation == TYPE_SETTING)
            placeItemTypeFragment(itemTypeFragment);
        else if(currentOperation == PARAMETER_SETTING)
            placeItemResParamFragment(itemResParamFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        @Operation
        int currentOperation;

        if (itemResParamFragment != null && itemResParamFragment.isAdded())
            currentOperation = PARAMETER_SETTING;
        else if (itemTypeFragment != null && itemTypeFragment.isAdded())
            currentOperation = TYPE_SETTING;
        else if (addEditClothingFragment != null && addEditClothingFragment.isAdded())
            currentOperation = ADDING_EDITING;
        else
            currentOperation = VIEWING;

        outState.putInt(CURRENT_OPERATION_KEY, currentOperation);

        Icepick.saveInstanceState(this, outState);
        outState.putBoolean(WAS_LAND_TABLET_KEY, isLandTablet);
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (itemResParamFragment != null && itemResParamFragment.isAdded())
        {
            supportFragmentManager.putFragment(outState, ItemResParamFragment.class.getSimpleName(), itemResParamFragment);
            log("Saving item res param fragment");
        }

        if (itemTypeFragment != null && itemTypeFragment.isAdded())
        {
            supportFragmentManager.putFragment(outState, ItemTypeFragment.class.getSimpleName(), itemTypeFragment);
            log("Saving item type fragment");
        }

        if (addEditClothingFragment != null && supportFragmentManager.getFragments().contains(addEditClothingFragment))
        {
            supportFragmentManager.putFragment(outState, AddEditClothingFragment.class.getSimpleName(), addEditClothingFragment);
            log("Saving add edit clothing fragment");
        }
        supportFragmentManager.putFragment(outState, ClothingListFragment.class.getSimpleName(), clothingListFragment);
    }

    @DebugLog
    private void showClothingListFragment()
    {
        if (clothingListFragment == null)
        {
            clothingListFragment = new ClothingListFragment();
            clothingListFragment.setListener(this);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder_layout_ca, clothingListFragment)
                .commit();
    }

    private void showAddEditClothingFragment()
    {
        if (addEditClothingFragment == null)
        {
            addEditClothingFragment = new AddEditClothingFragment();
            addEditClothingFragment.setListener(this);
        }

        if (isLandTablet)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.extra_fragment_holder_layout_ca, addEditClothingFragment)
                    .commit();
        }
        else
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_holder_layout_ca, addEditClothingFragment)
                    .addToBackStack(addEditClothingFragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private void placeItemResParamFragment(@NonNull ItemResParamFragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void placeItemTypeFragment(@NonNull ItemTypeFragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onAddItemRequested()
    {
        if (!isLandTablet)
            showAddEditClothingFragment();
        else
            addEditClothingFragment.requestAddItem();
    }

    @Override
    public void onEditItemRequested(ClothingItem item)
    {
        if (!isLandTablet)
            showAddEditClothingFragment();
        addEditClothingFragment.setItemForEdit(item);
    }

    @Override
    public void onViewItemRequested(ClothingItem item)
    {
        if (!isLandTablet)
            showAddEditClothingFragment();
        addEditClothingFragment.setItemForView(item);
    }

    @Override
    public void onRequestParamPick(@ItemResParamFragment.ParamType int type, ClothingItem item)
    {
        if (itemResParamFragment == null)
        {
            itemResParamFragment = ItemResParamFragment.createFragment(type, item);
            itemResParamFragment.setListener(this);
        }
        else
        {
            itemResParamFragment.setParamType(type);
            itemResParamFragment.setCurrentItem(item);
        }
        placeItemResParamFragment(itemResParamFragment);
    }

    @Override
    public void onRequestTypePick(ClothingItem item)
    {
        if (itemTypeFragment == null)
        {
            itemTypeFragment = ItemTypeFragment.createFragment(item);
            itemTypeFragment.setListener(this);
        }
        else
        {
            itemTypeFragment.setItem(item);
        }
        placeItemTypeFragment(itemTypeFragment);

    }

    @Override
    public void onAddingCanceled()
    {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onParamChosen(@ClothingItem.Resistance int resistance, @ItemResParamFragment.ParamType int paramType)
    {
        getSupportFragmentManager().popBackStack();
        switch (paramType)
        {
            case ItemResParamFragment.COLD_RES:
                addEditClothingFragment.setItemColdRes(resistance);
                break;
            case ItemResParamFragment.WATER_RES:
                addEditClothingFragment.setItemWaterRes(resistance);
                break;
            case ItemResParamFragment.WIND_RES:
                addEditClothingFragment.setItemWindRes(resistance);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported type => " + paramType);
        }
    }

    @Override
    public void onParamEditCanceled()
    {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onTypeSelected(@ClothingItem.ClothingType int type)
    {
        addEditClothingFragment.setItemType(type);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onTypeSelectionCancelled()
    {
        getSupportFragmentManager().popBackStack();
    }

    private void log(String message)
    {
        Log.d(TAG, message);
    }
}
