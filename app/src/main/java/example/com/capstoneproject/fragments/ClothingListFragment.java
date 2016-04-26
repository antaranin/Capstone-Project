package example.com.capstoneproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.capstoneproject.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClothingListFragment extends Fragment
{


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

}
