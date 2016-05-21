package example.com.capstoneproject.gui_layer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;
import example.com.capstoneproject.model_layer.ClothingItem;
import lombok.Setter;

/**
 * Created by Arin on 14/05/16.
 */
public class SuggestedApparelAdapter extends RecyclerView.Adapter<SuggestedApparelAdapter.ApparelViewHolder>
{
    private ArrayList<ClothingItem> data;

    @Setter
    private OnItemLongClickedListener listener;

    public void setData(@Nullable  ArrayList<ClothingItem> data)
    {
        this.data = data;
        notifyDataSetChanged();
    }

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
        ClothingItem dataItem = data.get(position);
        holder.nameView.setText(dataItem.getName());
        Context context = holder.photoView.getContext();
        Picasso.with(context)
                .load(dataItem.getImageUri())
                .into(holder.photoView);

    }

    @Override
    public int getItemCount()
    {
        if(data == null)
            return 0;
        return data.size();
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
            if(listener != null)
                listener.onItemLongClicked(data.get(getAdapterPosition()));
            return true;
        }
    }

    public interface OnItemLongClickedListener
    {
        void onItemLongClicked(ClothingItem item);
    }
}
