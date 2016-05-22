package example.com.capstoneproject.gui_layer.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.DataContract;
import example.com.capstoneproject.gui_layer.ClothingAdapter;
import example.com.capstoneproject.gui_layer.FillableView;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClothingListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ClothingAdapter.OnDataSetListener
{
    private static final int CLOTHING_LOADER = 0;

    @BindView(R.id.item_preview_layout)
    LinearLayout previewLayout;

    @BindView(R.id.item_photo_iv)
    ImageView photoPreviewIv;

    @BindView(R.id.clothing_type_btn)
    ImageView clothingTypeIv;

    @BindView(R.id.water_res_btn)
    FillableView waterResFv;

    @BindView(R.id.wind_res_btn)
    FillableView windResFv;

    @BindView(R.id.cold_res_btn)
    FillableView coldResFv;

    @BindView(R.id.item_rv)
    RecyclerView recycler;

    @BindView(R.id.no_item_tv)
    TextView noItemTv;

    @BindView(R.id.add_item_fab_clf)
    FloatingActionButton addItemFab;

    private ClothingAdapter clothingAdapter;

    @Setter
    private OnListInteractionListener listener;

    private boolean isTabletLand;

    public ClothingListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        clothingAdapter = new ClothingAdapter();
        clothingAdapter.setListener(this);
        if(savedInstanceState != null)
            restoreInstance(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clothing_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        isTabletLand = getResources().getBoolean(R.bool.is_tablet_land);
        ButterKnife.bind(this, view);
        recycler.setAdapter(clothingAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        if(savedInstanceState != null)
            restoreInstance(savedInstanceState);
    }

    private void restoreInstance(Bundle savedInstanceState)
    {
        clothingAdapter.restoreInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        clothingAdapter.onSaveInstance(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!isTabletLand)
            //noinspection ConstantConditions
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.item_list);
    }

    @Optional
    @OnClick(R.id.add_item_fab_clf)
    void onAddItemPressed()
    {
        if (listener != null)
            listener.onAddItemRequested();
    }

    private void log(String mesage)
    {
        Log.d(this.getClass().getSimpleName(), mesage);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(CLOTHING_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    @DebugLog
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final String[] columns = {DataContract.ClothingEntry._ID,
                DataContract.ClothingEntry.NAME,
                DataContract.ClothingEntry.COLD_RES,
                DataContract.ClothingEntry.WIND_RES,
                DataContract.ClothingEntry.WATER_RES,
                DataContract.ClothingEntry.TYPE,
                DataContract.ClothingEntry.IMAGE_URI
        };

        return new CursorLoader(
                getActivity(),
                DataContract.ClothingEntry.CONTENT_URI,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    @DebugLog
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data)
    {
        log("Data count => " + data.getCount());
        clothingAdapter.setData(data);
        if (data.getCount() == 0)
        {
            previewLayout.setVisibility(View.GONE);
            noItemTv.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams fabParams = (RelativeLayout.LayoutParams) addItemFab.getLayoutParams();
            fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            fabParams.removeRule(RelativeLayout.BELOW);
            return;
        }
        else if(noItemTv.getVisibility() != View.GONE)
        {
            previewLayout.setVisibility(View.VISIBLE);
            noItemTv.setVisibility(View.GONE);
            RelativeLayout.LayoutParams fabParams = (RelativeLayout.LayoutParams) addItemFab.getLayoutParams();
            fabParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            fabParams.addRule(RelativeLayout.BELOW, R.id.item_preview_layout);
        }

        recycler.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                if (recycler.getChildCount() > 0)
                {
                    recycler.getViewTreeObserver().removeOnPreDrawListener(this);
                    clothingAdapter.restoreItemPosition(true);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    @DebugLog
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader)
    {

        clothingAdapter.setData(null);
    }

    @Override
    public void onDataAvailabilityChanged(boolean available)
    {

    }

    @Override
    public void onItemSelected(ClothingItem selectedItem)
    {
        setPreviewItem(selectedItem);
    }

    @Override
    public void onItemEditRequested(ClothingItem item)
    {
        if(listener != null)
            listener.onEditItemRequested(item);
    }

    @Override
    public void onItemDeleteRequested(ClothingItem item)
    {
        String whereClause = String.format("%s = %s", DataContract.ClothingEntry._ID, item.getId());
        getContext().getContentResolver()
                .delete(DataContract.ClothingEntry.CONTENT_URI, whereClause, null);
    }

    private void setPreviewItem(ClothingItem item)
    {
        if (item == null)
        {
            previewLayout.setVisibility(View.GONE);
            return;
        }


        if(previewLayout.getVisibility() == View.GONE)
            previewLayout.setVisibility(View.VISIBLE);

        final int coldResistance = item.getColdResistance();
        coldResFv.setCurrentFill(coldResistance);
        coldResFv.setContentDescription(Utilities.getColdResistanceContentDesc(getContext(), coldResistance));

        final int waterResistance = item.getWaterResistance();
        waterResFv.setCurrentFill(waterResistance);
        waterResFv.setContentDescription(Utilities.getWaterResistanceContentDesc(getContext(), waterResistance));

        final int windResistance = item.getWindResistance();
        windResFv.setCurrentFill(windResistance);
        windResFv.setContentDescription(Utilities.getWindResistanceContentDesc(getContext(), windResistance));

        final int type = item.getType();
        clothingTypeIv.setImageResource(Utilities.getClothingTypeDrawableRes(type));
        clothingTypeIv.setContentDescription(Utilities.getClothingDesc(getContext(), type));

        log("Loaded uri => " + item.getImageUri());
        photoPreviewIv.setContentDescription(item.getName());
        Picasso.with(getContext())
                .load(item.getImageUri().toString())
                .resize(photoPreviewIv.getWidth(), photoPreviewIv.getHeight())
                .centerCrop()
                .into(photoPreviewIv);

    }

    public interface OnListInteractionListener
    {
        void onAddItemRequested();

        void onEditItemRequested(ClothingItem item);

    }
}
