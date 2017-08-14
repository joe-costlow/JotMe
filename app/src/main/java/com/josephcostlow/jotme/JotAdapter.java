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


/**
 * Created by Joseph Costlow on 29-Jul-17.
 */

public class JotAdapter extends RecyclerView.Adapter<JotAdapter.ViewHolder> {

    private final SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
    OnItemClickListener mListener;
    DetailFragment detailFragment;
    ListFragment listFragment;
    private ArrayList<Jot> jotsData;
    private Context context;
    private boolean mDualPane;
    private String INITIAL_DETAIL_FRAGMENT = MainActivity.INITIAL_DETAIL_FRAGMENT;
    private boolean autoSelector;
    private int clickedPosition = MainActivity.clickedPosition;

    public JotAdapter(Context context, ArrayList<Jot> jotsData, ListFragment listFragment) {
        this.jotsData = jotsData;
        this.context = context;
        mDualPane = context.getResources().getBoolean(R.bool.dual_pane);
        this.listFragment = listFragment;
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

        autoSelector = false;
        clickedPosition = 0;

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

                        if (clickedPosition > getItemCount() - 1) {

                            bundleBuild(getItemCount() - 1);
                        }
                    }
                }
            }
        }
    }

    private void bundleBuild(int position) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, position);
        editor.apply();

        String title = jotsData.get(position).getTitle();
        String tagOne = jotsData.get(position).getTagOne();
        String tagTwo = jotsData.get(position).getTagTwo();
        String tagThree = jotsData.get(position).getTagThree();
        String message = jotsData.get(position).getMessage();

        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();

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

        int position = getItemCount() - 1;

        mListener.publicOnClick(position);

        Toast.makeText(context, "DATA SAVED : " + String.valueOf(getItemCount()), Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, position);
        editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, true);
        editor.apply();
    }

    public void editJot(String title, String tagOne, String tagTwo, String tagThree, String message) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

        jotsData.get(clickedPosition).setTitle(title);
        jotsData.get(clickedPosition).setTagOne(tagOne);
        jotsData.get(clickedPosition).setTagTwo(tagTwo);
        jotsData.get(clickedPosition).setTagThree(tagThree);
        jotsData.get(clickedPosition).setMessage(message);

        Toast.makeText(context, "DATA UPDATED : " + String.valueOf(clickedPosition), Toast.LENGTH_SHORT).show();

        bundleBuild(clickedPosition);
    }

    public void deleteJot(int id) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

        if (id < clickedPosition) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition - 1);
            editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, false);
            editor.apply();
        }

        Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

        if (!(testFragment instanceof EditFragment)) {
            jotsData.remove(id);
        }

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);
    }

    @Override
    public int getItemCount() {
        return jotsData.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {this.mListener = clickListener;}

    public interface OnItemClickListener {
        void onClick(View view, int position);

        void publicOnClick(int position);
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

            Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

            if (testFragment instanceof EditFragment && mDualPane) {

                mListener = null;
            }

            if (mListener != null) {

                mListener.onClick(view, getAdapterPosition());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, autoSelector);
                editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, getAdapterPosition());
                editor.apply();
            }
        }
    }
}
