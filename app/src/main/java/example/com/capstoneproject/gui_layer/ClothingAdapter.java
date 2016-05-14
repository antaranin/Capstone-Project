package example.com.capstoneproject.gui_layer;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.SingleSelectionAdapter;
import example.com.capstoneproject.data_layer.ClothingCursor;
import example.com.capstoneproject.model_layer.ClothingItem;

/**
 * Created by Arin on 12/05/16.
 */
public class ClothingAdapter extends SingleSelectionAdapter<ClothingAdapter.ClothingViewHolder>
{
    private ClothingCursor cursor;
    private OnDataSetListener listener;

    @Override
    public ClothingViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (!(parent instanceof RecyclerView))
            throw new RuntimeException("Not bound to RecyclerView");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview, parent, false);
        view.setFocusable(true);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClothingViewHolder holder, int position)
    {
        //TODO

    }

    @Override
    public int getItemCount()
    {
        if(cursor != null)
            return cursor.getCount();
        return 0;
    }

    public void setData(@Nullable  Cursor cursor)
    {
        boolean oldDataAvailable = this.cursor != null && this.cursor.getCount() != 0;
        this.cursor = cursor != null ? new ClothingCursor(cursor) : null;
        boolean newDataAvailable = cursor != null && cursor.getCount() != 0;

        notifyDataSetChanged();
        if(oldDataAvailable != newDataAvailable && listener != null)
            listener.onDataAvailabilityChanged(!newDataAvailable);
    }

    public void selectItem(int position)
    {
        selectItem(position, true);
    }

    private void selectItem(int position, boolean userCalled)
    {
        if(cursor.getCount() >= position)
            throw new AssertionError("The position provided is outside of data range");
        setSelection(position);

        if(listener != null && userCalled)
        {
            cursor.moveToPosition(position);
            listener.onItemSelected(buildItemFromCursor());
        }
    }

    private ClothingItem buildItemFromCursor()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public class ClothingViewHolder extends SingleSelectionAdapter.SingleSelectionHolder implements View.OnCreateContextMenuListener
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
            selectItem(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            menu.add(0, v.getId(), 0, R.string.edit);
            menu.add(0, v.getId(), 0, R.string.delete);
        }
    }

    public interface OnDataSetListener
    {
        void onDataAvailabilityChanged(boolean available);

        void onItemSelected(ClothingItem selectedItem);
    }
}
