package com.josephcostlow.jotme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Costlow on 24-Aug-17.
 */

public class FilteredAdapter extends RecyclerView.Adapter<JotViewHolder> {

    List<Jot> jotsData;
    Context context;
    ItemClickListener mListener;
    JotAdapter.ClickListener mInterfaceListener;
    ListFragment listFragment;

    public FilteredAdapter(Context context, List<Jot> jotsData, ListFragment listFragment) {

        this.context = context;
        this.jotsData = jotsData;
        this.listFragment = listFragment;
    }

    @Override
    public JotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new JotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JotViewHolder holder, int position) {

        holder.title.setText(jotsData.get(position).getTitle());
        holder.tagOne.setText(jotsData.get(position).getTagOne());
        holder.tagTwo.setText(jotsData.get(position).getTagTwo());
        holder.tagThree.setText(jotsData.get(position).getTagThree());

//        If the value of tags are the default value, their view is hidden.
        if (jotsData.get(position).getTagOne()
                .equals(context.getResources().getString(R.string.empty_tag_edit))) {
            holder.tagOne.setVisibility(View.GONE);
        }

        if (jotsData.get(position).getTagTwo()
                .equals(context.getResources().getString(R.string.empty_tag_edit))) {
            holder.tagTwo.setVisibility(View.GONE);
        }

        if (jotsData.get(position).getTagThree()
                .equals(context.getResources().getString(R.string.empty_tag_edit))) {
            holder.tagThree.setVisibility(View.GONE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position) {
                listFragment.publicOnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return jotsData.size();
    }

    /**
     * Method used for the search feature to replace the original list with a filtered list.
     *
     * @param newList
     */
    public void setFilter(List<Jot> newList) {

        jotsData = new ArrayList<>();
        jotsData.addAll(newList);
        notifyDataSetChanged();
    }
}
