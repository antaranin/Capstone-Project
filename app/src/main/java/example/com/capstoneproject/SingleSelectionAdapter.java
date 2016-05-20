package example.com.capstoneproject;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import hugo.weaving.DebugLog;

/**
 * Created by Arin on 12/05/16.
 */
public abstract class SingleSelectionAdapter<VH extends SingleSelectionAdapter.SingleSelectionHolder>
        extends RecyclerView.Adapter<VH>
{
    private int selected_item = 0;
    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position)
    {
        // Set selected state; use a state list drawable to style the view
        viewHolder.itemView.setSelected(selected_item == position);
        if(selected_item == position)
            recyclerView.scrollToPosition(position);
    }

    @DebugLog
    public void resetSelection()
    {
        changeSelection(0);
    }

    /**
     * Changes current selected item if needed and reselects the views accordingly.
     * @param item_position New selected item position
     * @return True if selection was changed, False otherwise
     */
    @DebugLog
    private boolean changeSelection(int item_position)
    {
        if(selected_item != item_position)
        {
            notifyItemChanged(selected_item);
            selected_item = item_position;
            notifyItemChanged(selected_item);
            return true;
        }
        return false;
    }

    protected void setSelection(int selection)
    {
        changeSelection(selection);
    }

    public int getSelection()
    {
        return selected_item;
    }

    public abstract class SingleSelectionHolder extends RecyclerView.ViewHolder
    {
        public SingleSelectionHolder(View itemView)
        {
            super(itemView);

            // Handle item click and set the selection
            itemView.setOnClickListener(v -> {
                // Redraw the old selection and the new
                int new_selected_item = recyclerView.getChildAdapterPosition(v);
                if(changeSelection(new_selected_item))
                    onHolderPressed();
            });
        }

        public abstract void onHolderPressed();
    }

    private void log(String message)
    {
        Log.d(this.getClass().getSimpleName(), message);
    }
}

