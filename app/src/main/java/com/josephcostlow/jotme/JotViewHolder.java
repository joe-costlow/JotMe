package com.josephcostlow.jotme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Joseph Costlow on 21-Aug-17.
 */

public class JotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title, tagOne, tagTwo, tagThree;
    Context context;
    ItemClickListener mListener;

    public JotViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.list_item_title);
        tagOne = (TextView) itemView.findViewById(R.id.list_item_tag_one);
        tagTwo = (TextView) itemView.findViewById(R.id.list_item_tag_two);
        tagThree = (TextView) itemView.findViewById(R.id.list_item_tag_three);

        context = itemView.getContext();

        itemView.setOnClickListener(this);
    }

    public void setTitle(String title1) {

        title.setText(title1);
    }

    public void setTagOne(String tagOne1) {

        tagOne.setText(tagOne1);
    }

    public void setTagTwo(String tagTwo1) {

        if (!tagTwo1.equals(context.getResources().getString(R.string.empty_tag_edit))) {

            tagTwo.setText(tagTwo1);

        } else {

            tagTwo.setVisibility(View.GONE);
        }
    }

    public void setTagThree(String tagThree1) {

        if (!tagThree1.equals(context.getResources().getString(R.string.empty_tag_edit))) {

            tagThree.setText(tagThree1);

        } else {

            tagThree.setVisibility(View.GONE);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        this.mListener.onClick(this.getAdapterPosition());
    }
}
