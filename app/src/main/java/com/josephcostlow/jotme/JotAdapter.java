package com.josephcostlow.jotme;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
    private Context context;
    OnItemClickListener mListener;

    private boolean mDualPane;
    DetailFragment detailFragment;
    EditFragment editFragment;

    private String INITIAL_EDIT_FRAGMENT = MainActivity.INITIAL_EDIT_FRAGMENT;
    private String INITIAL_DETAIL_FRAGMENT = MainActivity.INITIAL_DETAIL_FRAGMENT;
    private String BUNDLE_TITLE = MainActivity.BUNDLE_TITLE;
    private String BUNDLE_TAG_ONE = MainActivity.BUNDLE_TAG_ONE;
    private String BUNDLE_TAG_TWO = MainActivity.BUNDLE_TAG_TWO;
    private String BUNDLE_TAG_THREE = MainActivity.BUNDLE_TAG_THREE;
    private String BUNDLE_MESSAGE = MainActivity.BUNDLE_MESSAGE;

    private boolean autoSelector;
    private int clickedPosition;

    public JotAdapter(Context context, ArrayList<Jot> jotsData) {
        this.jotsData = jotsData;
        this.context = context;
        mDualPane = context.getResources().getBoolean(R.bool.dual_pane);
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(jotsData.get(position).getTitle());
        holder.tagOne.setText(jotsData.get(position).getTagOne());
        holder.tagTwo.setText(jotsData.get(position).getTagTwo());
        holder.tagThree.setText(jotsData.get(position).getTagThree());

        autoSelector = ListFragment.autoSelector;
        clickedPosition = ListFragment.clickedPosition;

        if (mDualPane) {

            if (!jotsData.isEmpty()) {

                if (autoSelector) {

                    if (position == jotsData.size() - 1) {

                        int auto = position;
                        bundleBuild(auto);
                    }

                } else {

                    if (clickedPosition == position) {

                        int auto = position;
                        bundleBuild(auto);
                    }
                }
            }
        }
    }

    private void bundleBuild(int position) {

        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        detailFragment = new DetailFragment();
        editFragment = new EditFragment();

        Bundle bundle = new Bundle();
        String title = jotsData.get(position).getTitle();
        String tagOne = jotsData.get(position).getTagOne();
        String tagTwo = jotsData.get(position).getTagTwo();
        String tagThree = jotsData.get(position).getTagThree();
        String message = jotsData.get(position).getMessage();
        bundle.putString(BUNDLE_TITLE, title);
        bundle.putString(BUNDLE_TAG_ONE, tagOne);
        bundle.putString(BUNDLE_TAG_TWO, tagTwo);
        bundle.putString(BUNDLE_TAG_THREE, tagThree);
        bundle.putString(BUNDLE_MESSAGE, message);
        detailFragment.setArguments(bundle);
        editFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                .commit();

//        fragmentManager.beginTransaction()
//                .replace(R.id.frame_right, editFragment, INITIAL_EDIT_FRAGMENT)
//                .commit();

    }

    @Override
    public int getItemCount() {
        return jotsData.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {this.mListener = clickListener;}

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, tagOne, tagTwo, tagThree;


        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.list_item_title);
            tagOne = (TextView) itemView.findViewById(R.id.list_item_tag_one);
            tagTwo = (TextView) itemView.findViewById(R.id.list_item_tag_two);
            tagThree = (TextView) itemView.findViewById(R.id.list_item_tag_three);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            autoSelector = false;

            if (mListener != null) {
                mListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
