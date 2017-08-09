package com.josephcostlow.jotme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.josephcostlow.jotme.MainActivity.SHARED_PREFS_AUTO_SELECT_KEY;
import static com.josephcostlow.jotme.MainActivity.SHARED_PREFS_CLICKED_POSITION_KEY;
import static com.josephcostlow.jotme.MainActivity.SHARED_PREFS_FILENAME;


/**
 * Created by Joseph Costlow on 29-Jul-17.
 */

public class JotAdapter extends RecyclerView.Adapter<JotAdapter.ViewHolder> {

    OnItemClickListener mListener;
    DetailFragment detailFragment;
    SharedPreferences sharedPreferences;
    private ArrayList<Jot> jotsData;
    private Context context;
    private boolean mDualPane;
    private String INITIAL_DETAIL_FRAGMENT = MainActivity.INITIAL_DETAIL_FRAGMENT;
    private boolean autoSelector;
    private int clickedPosition;

    public JotAdapter(Context context, ArrayList<Jot> jotsData) {
        this.jotsData = jotsData;
        this.context = context;
        mDualPane = context.getResources().getBoolean(R.bool.dual_pane);
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

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        autoSelector = sharedPreferences.getBoolean(SHARED_PREFS_AUTO_SELECT_KEY, true);
        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

        if (mDualPane) {

            if (!jotsData.isEmpty()) {

                if (autoSelector) {

                    if (position == jotsData.size() - 1) {

                        bundleBuild(position);
                    }

                } else {

                    Fragment fragment = ((MainActivity) context)
                            .getSupportFragmentManager().findFragmentById(R.id.frame_right);

                    if (fragment instanceof DetailFragment) {

                        if (clickedPosition == position) {

                            bundleBuild(position);
                        }
                    }

                }
            }
        }
    }

    private void bundleBuild(int position) {

        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();

        String title = jotsData.get(position).getTitle();
        String tagOne = jotsData.get(position).getTagOne();
        String tagTwo = jotsData.get(position).getTagTwo();
        String tagThree = jotsData.get(position).getTagThree();
        String message = jotsData.get(position).getMessage();

        detailFragment = DetailFragment.newInstance(title, tagOne, tagTwo, tagThree, message);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                .commit();
    }

    public void addJot(String title, String tagOne, String tagTwo, String tagThree, String message) {
//    TODO This will change with real data
        Jot jot = new Jot();
        jot.setTitle(title);
        jot.setTagOne(tagOne);
        jot.setTagTwo(tagTwo);
        jot.setTagThree(tagThree);
        jot.setMessage(message);

        jotsData.add(jot);

        Toast.makeText(context, "DATA SAVED", Toast.LENGTH_SHORT).show();

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, getItemCount());
        editor.apply();

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return jotsData.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {this.mListener = clickListener;}

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

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
