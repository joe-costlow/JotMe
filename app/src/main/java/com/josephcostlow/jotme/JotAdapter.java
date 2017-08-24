package com.josephcostlow.jotme;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by Joseph Costlow on 29-Jul-17.
 */

public class JotAdapter extends FirebaseRecyclerAdapter<Jot, JotViewHolder> {

    ClickListener mInterfaceListener;
    ListFragment listFragment;
    Context context;
    private boolean mDualPane;

    public JotAdapter(Class<Jot> modelClass, @LayoutRes int modelLayout, Class<JotViewHolder> viewHolderClass, Query query, ListFragment listFragment) {
        super(modelClass, modelLayout, viewHolderClass, query);
        this.listFragment = listFragment;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void populateViewHolder(JotViewHolder viewHolder, Jot model, int position) {

        viewHolder.setTitle(model.getTitle());
        viewHolder.setTagOne(model.getTagOne());
        viewHolder.setTagTwo(model.getTagTwo());
        viewHolder.setTagThree(model.getTagThree());

        this.context = viewHolder.context;

        mDualPane = context.getResources().getBoolean(R.bool.dual_pane);

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position) {

                listFragment.publicOnClick(position);
            }
        });

    }

    public interface ClickListener {

        void publicOnClick(int position);
    }
}
