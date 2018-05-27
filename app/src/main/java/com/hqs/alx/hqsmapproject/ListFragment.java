package com.hqs.alx.hqsmapproject;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.listRecyclerView);

        (view.findViewById(R.id.floatingAddButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FragmentManager fragmentManager = getFragmentManager();

        ArrayList<MyPlaces> favPlaces = (ArrayList<MyPlaces>) MyPlaces.listAll(MyPlaces.class);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        MyPlacesRecyclerAdapter myPlacesRecyclerAdapter = new MyPlacesRecyclerAdapter(favPlaces, getActivity(), fragmentManager);
        recyclerView.setAdapter(myPlacesRecyclerAdapter);

        return view;
    }



}
