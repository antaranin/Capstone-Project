package example.com.capstoneproject.gui_layer;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.ClothingCursor;
import example.com.capstoneproject.management_layer.Utilities;
import example.com.capstoneproject.model_layer.ClothingItem;
import hugo.weaving.DebugLog;
import lombok.Setter;

/**
 * Created by Arin on 12/05/16.
 */
public class ClothingAdapter extends SingleSelectionAdapter<ClothingAdapter.ClothingViewHolder>
{
    private static final String SELECTION_KEY = "selection_key";
    private ClothingCursor cursor;
    @Setter
    private OnDataSetListener listener;

    private Integer savedPosition = null;

    @Override
    @DebugLog
    public ClothingViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (!(parent instanceof RecyclerView))
            throw new RuntimeException("Not bound to RecyclerView");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothing_recycler_item, parent, false);
        return new ClothingViewHolder(view);
    }

    public void onSaveInstance(Bundle outState)
    {
        outState.putInt(SELECTION_KEY, savedPosition != null ? savedPosition : getSelection());
    }

    public void restoreInstance(Bundle savedState)
    {
        savedPosition = savedState.getInt(SELECTION_KEY);
        if(getItemCount() > savedPosition)
            restoreItemPosition(true);
    }

    @Override
    @DebugLog
    public void onBindViewHolder(ClothingViewHolder holder, int position)
    {
        super.onBindViewHolder(holder, position);
        cursor.moveToPosition(position);
        ClothingItem clothingItem = cursor.getItem();
        holder.name_tv.setText(clothingItem.getName());
        Context context = holder.type_iv.getContext();
        holder.itemView.setContentDescription(context.getString(R.string.select_item_template, clothingItem.getName()));
        Picasso.with(context)
                .load(Utilities.getClothingTypeDrawableRes(clothingItem.getType()))
                .into(holder.type_iv);
    }

    @Override
    public int getItemCount()
    {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }

    @DebugLog
    public void setData(@Nullable Cursor cursor)
    {
        boolean oldDataAvailable = this.cursor != null && this.cursor.getCount() != 0;
        this.cursor = cursor != null ? new ClothingCursor(cursor) : null;
        boolean newDataAvailable = cursor != null && cursor.getCount() != 0;

        notifyDataSetChanged();
        if (oldDataAvailable != newDataAvailable && listener != null)
            listener.onDataAvailabilityChanged(!newDataAvailable);
        resetSelection();
    }

    public boolean hasSavedPosition()
    {
        return savedPosition != null;
    }

    public void restoreItemPosition(boolean notifyListeners)
    {
        int position = savedPosition == null ? 0 : savedPosition;
        selectItem(position, notifyListeners);
        savedPosition = null;
    }

    public void selectItem(int position, boolean userCalled)
    {
        if (getItemCount() <= position)
            throw new AssertionError("The position provided is outside of data range");
        setSelection(position);

        if (listener != null && userCalled)
        {
            cursor.moveToPosition(position);
            listener.onItemSelected(cursor.getItem());
        }
    }

    private ClothingItem getItemAt(int adapterPosition)
    {
        cursor.moveToPosition(adapterPosition);
        return cursor.getItem();
    }

    public class ClothingViewHolder extends SingleSelectionAdapter.SingleSelectionHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {
        @BindView(R.id.item_name_tv_cri)
        TextView name_tv;

        @BindView(R.id.item_type_iv_cri)
        ImageView type_iv;

        public ClothingViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onHolderPressed()
        {
            selectItem(getAdapterPosition(), true);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            MenuItem editItem = menu.add(0, R.id.edit_menu_btn, Menu.NONE, R.string.edit);
            editItem.setOnMenuItemClickListener(this);
            MenuItem deleteItem = menu.add(0, R.id.delete_menu_btn, Menu.NONE, R.string.delete);
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            if(listener != null)
            {
                int itemId = item.getItemId();
                switch (itemId)
                {
                    case R.id.edit_menu_btn:
                        listener.onItemEditRequested(getItemAt(getAdapterPosition()));
                        break;
                    case R.id.delete_menu_btn:
                        listener.onItemDeleteRequested(getItemAt(getAdapterPosition()));
                        break;
                    default:
                        throw new RuntimeException("Unexpected menu btn pressed with id => " + itemId);
                }
            }
            return true;
        }
    }

    public interface OnDataSetListener
    {
        void onDataAvailabilityChanged(boolean available);

        void onItemSelected(ClothingItem selectedItem);

        void onItemEditRequested(ClothingItem item);

        void onItemDeleteRequested(ClothingItem item);
    }
}
