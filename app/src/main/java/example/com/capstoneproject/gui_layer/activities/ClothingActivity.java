package example.com.capstoneproject.gui_layer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import example.com.capstoneproject.R;
import example.com.capstoneproject.gui_layer.fragments.AddEditClothingFragment;
import example.com.capstoneproject.gui_layer.fragments.ClothingListFragment;
import example.com.capstoneproject.gui_layer.fragments.ItemResParamFragment;
import example.com.capstoneproject.gui_layer.fragments.ItemTypeFragment;
import example.com.capstoneproject.model_layer.ClothingItem;

public class ClothingActivity extends AppCompatActivity implements
        ClothingListFragment.OnListInteractionListener, AddEditClothingFragment.OnAddEditClothingInteractionListener,
        ItemResParamFragment.OnResParamInteractionListener, ItemTypeFragment.OnTypeInteractionListener
{
    private ClothingListFragment listFragment;
    private AddEditClothingFragment addEditClothingFragment;
    private ItemResParamFragment itemResParamFragment;
    private ItemTypeFragment itemTypeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Item list");

        listFragment = new ClothingListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder_layout_ca, listFragment)
                .addToBackStack(listFragment.getClass().getSimpleName())
                .commit();
        listFragment.setListener(this);
    }

    @Override
    public void onAddItemRequested()
    {
        if(addEditClothingFragment == null)
        {
            addEditClothingFragment = new AddEditClothingFragment();
            addEditClothingFragment.setListener(this);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, addEditClothingFragment)
                .addToBackStack(addEditClothingFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onEditItemRequested(ClothingItem item)
    {

    }

    @Override
    public void onRequestParamPick(@ItemResParamFragment.ParamType int type, ClothingItem item)
    {
        if(itemResParamFragment == null)
        {
            itemResParamFragment = ItemResParamFragment.createFragment(type, item);
            itemResParamFragment.setListener(this);
        }
        else
        {
            itemResParamFragment.setParamType(type);
            itemResParamFragment.setCurrentItem(item);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, itemResParamFragment)
                .addToBackStack(itemResParamFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onRequestTypePick(ClothingItem item)
    {
        if(itemTypeFragment == null)
        {
            itemTypeFragment = ItemTypeFragment.createFragment(item);
            itemTypeFragment.setListener(this);
        }
        else
        {
            itemTypeFragment.setItem(item);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, itemTypeFragment)
                .addToBackStack(itemTypeFragment.getClass().getSimpleName())
                .commit();

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
}
