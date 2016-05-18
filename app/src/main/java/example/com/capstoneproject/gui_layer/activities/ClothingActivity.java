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
        ClothingListFragment.OnListInteractionListener, AddEditClothingFragment.OnAddEditClothingInteractionListener
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
    public void onCallParam(@ItemResParamFragment.ParamType int type, ClothingItem item)
    {
        if(itemResParamFragment == null)
            itemResParamFragment = ItemResParamFragment.createFragment(type, item);
        else
            itemResParamFragment.setParamType(type);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder_layout_ca, itemResParamFragment)
                .addToBackStack(itemResParamFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onCallType(ClothingItem item)
    {
        if(itemTypeFragment == null)
            itemTypeFragment = ItemTypeFragment.createFragment(item);
        else
            itemTypeFragment.setItem(item);

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
}
