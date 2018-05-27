package com.hqs.alx.hqsmapproject;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alex on 04/01/2018.
 */

public class MyPlacesRecyclerAdapter extends RecyclerView.Adapter<MyPlacesRecyclerAdapter.MyViewHolder> {

    ArrayList<MyPlaces> placesList;
    Context context;
    FragmentManager fragmentManager;

    public MyPlacesRecyclerAdapter(ArrayList<MyPlaces> placesList, Context context, FragmentManager fragmentManager) {
        this.placesList = placesList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyPlacesRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View viewFromXML= LayoutInflater.from(context).inflate(R.layout.single_item, null);
        MyViewHolder singleItem= new MyViewHolder(viewFromXML);

        return singleItem;
    }

    @Override
    public void onBindViewHolder(MyPlacesRecyclerAdapter.MyViewHolder holder, int position) {

        MyPlaces currentPlace= placesList.get(position);

        holder.bindMyCityData(currentPlace);
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
        public void bindMyCityData(final MyPlaces currentPlace)
        {
            TextView textView= itemView.findViewById(R.id.placeName);
            textView.setText(currentPlace.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent("com.hqs.alx.hqsmapproject.CHOSEN_PLACE");
                    intent.putExtra("myPlacesObject", currentPlace);
                    context.sendBroadcast(intent);

                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    /*
                    PlaceChanger placeChanger= (PlaceChanger) context;
                    placeChanger.changeFragments(currentPlace);
                    */
                }
            });




        }
    }
}
