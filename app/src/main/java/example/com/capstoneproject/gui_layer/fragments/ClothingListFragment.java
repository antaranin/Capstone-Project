package example.com.capstoneproject.gui_layer.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import example.com.capstoneproject.R;
import example.com.capstoneproject.model_layer.ClothingItem;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClothingListFragment extends Fragment
{
    @Setter
    private OnListInteractionListener listener;

    public ClothingListFragment()
    {
        // Required empty public constructor
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
        ButterKnife.bind(this, view);
    }

    @Optional
    @OnClick(R.id.add_item_fab_clf)
    void onAddItemPressed()
    {
        if(listener != null)
            listener.onAddItemRequested();
    }

    private void log(String mesage)
    {
        Log.d(this.getClass().getSimpleName(), mesage);
    }

    public interface OnListInteractionListener
    {
        void onAddItemRequested();

        void onEditItemRequested(ClothingItem item);

    }
}
