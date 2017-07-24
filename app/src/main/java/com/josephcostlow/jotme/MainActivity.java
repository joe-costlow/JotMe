package com.josephcostlow.jotme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    boolean mDualPane;

    public ListFragment listFragment;
    public DetailFragment detailFragment;
    public EditFragment editFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDualPane = getResources().getBoolean(R.bool.dual_pane);

        listFragment = new ListFragment();
        detailFragment = new DetailFragment();
        editFragment = new EditFragment();


        if (mDualPane) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_right, editFragment)
//                    .replace(R.id.frame_right, detailFragment)
//                    .replace(R.id.frame_left, listFragment)
//                    .addToBackStack(null)
                    .commit();

        } else {

            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_full, detailFragment)
                    .replace(R.id.frame_full, editFragment)
//                    .addToBackStack(null)
                    .commit();

        }
    }
}
