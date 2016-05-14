package example.com.capstoneproject.gui_layer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.data_layer.ClothingCursor;

/**
 * Created by Arin on 14/05/16.
 */
public class SuggestedApparelAdapter extends RecyclerView.Adapter<SuggestedApparelAdapter.ApparelViewHolder>
{
    private ClothingCursor cursor;

    @Override
    public ApparelViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (!(parent instanceof RecyclerView))
            throw new RuntimeException("Not bound to RecyclerView");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apparel_card, parent, false);
        view.setFocusable(true);
        return new ApparelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApparelViewHolder holder, int position)
    {
        //TODO

    }

    @Override
    public int getItemCount()
    {
        if(cursor == null)
            return 0;
        return cursor.getCount();
    }

    public class ApparelViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
    {
        @BindView(R.id.card_photo_iv)
        ImageView photoView;

        @BindView(R.id.item_name_tv)
        TextView nameView;

        public ApparelViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v)
        {
            //TODO
            return false;
        }
    }
}
