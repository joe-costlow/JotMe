package com.josephcostlow.jotme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joseph Costlow on 29-Jul-17.
 */

public class JotAdapter extends RecyclerView.Adapter<JotAdapter.ViewHolder> {

    private ArrayList<Jot> jotsData;

    public JotAdapter(ArrayList<Jot> jotsData) {
        this.jotsData = jotsData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(jotsData.get(position).getTitle());
        holder.tagOne.setText(jotsData.get(position).getTagOne());
        holder.tagTwo.setText(jotsData.get(position).getTagTwo());
        holder.tagThree.setText(jotsData.get(position).getTagThree());

    }

    @Override
    public int getItemCount() {
        return jotsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, tagOne, tagTwo, tagThree;


        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.list_item_title);
            tagOne = (TextView) itemView.findViewById(R.id.list_item_tag_one);
            tagTwo = (TextView) itemView.findViewById(R.id.list_item_tag_two);
            tagThree = (TextView) itemView.findViewById(R.id.list_item_tag_three);
        }
    }
}
