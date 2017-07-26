package com.josephcostlow.jotme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private String RETAINED_LIST_FRAGMENT = "retainedListFragment";
    private String RETAINED_DETAIL_FRAGMENT = "retainedDetailFragment";
    private String RETAINED_EDIT_FRAGMENT = "retainedEditFragment";
    private String INITIAL_LIST_FRAGMENT = "initialListFragment";
    private String INITIAL_DETAIL_FRAGMENT = "initialDetailFragment";
    private String INITIAL_EDIT_FRAGMENT = "initialEditFragment";

    Toolbar mainToolbar;

    boolean mDualPane;
    FloatingActionButton addFAB;

    public ListFragment listFragment;
    public DetailFragment detailFragment;
    public EditFragment editFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        mDualPane = getResources().getBoolean(R.bool.dual_pane);
        addFAB = (FloatingActionButton) findViewById(R.id.fab_add);

//        listFragment = new ListFragment();
//        detailFragment = new DetailFragment();
//        editFragment = new EditFragment();

        if (mDualPane) {

            if (savedInstanceState == null) {

                detailFragment = new DetailFragment();
                editFragment = new EditFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .add(R.id.frame_left, editFragment, INITIAL_EDIT_FRAGMENT)
                        .commit();

            } else {

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(RETAINED_DETAIL_FRAGMENT);

                editFragment = (EditFragment) getSupportFragmentManager()
                        .findFragmentByTag(RETAINED_EDIT_FRAGMENT);

            }

        } else {

            if (savedInstanceState == null) {

//                listFragment = new ListFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
//                        .commit();

//                detailFragment = new DetailFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.frame_full, detailFragment, INITIAL_DETAIL_FRAGMENT)
//                        .commit();

                editFragment = new EditFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_full, editFragment, INITIAL_EDIT_FRAGMENT)
                        .commit();

            } else {

                if (savedInstanceState.containsKey(RETAINED_LIST_FRAGMENT)) {
                    listFragment = (ListFragment) getSupportFragmentManager()
                            .findFragmentByTag(RETAINED_LIST_FRAGMENT);
                }

                if (savedInstanceState.containsKey(RETAINED_DETAIL_FRAGMENT)) {
                    detailFragment = (DetailFragment) getSupportFragmentManager()
                            .findFragmentByTag(RETAINED_DETAIL_FRAGMENT);
                }

                if (savedInstanceState.containsKey(RETAINED_EDIT_FRAGMENT)) {
                    editFragment = (EditFragment) getSupportFragmentManager()
                            .findFragmentByTag(RETAINED_EDIT_FRAGMENT);
                }

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mDualPane) {

            if (listFragment != null) {
                getSupportFragmentManager().putFragment(outState, RETAINED_LIST_FRAGMENT, listFragment);
            }

            if (detailFragment != null) {
                getSupportFragmentManager().putFragment(outState, RETAINED_DETAIL_FRAGMENT, detailFragment);
            }

            if (editFragment != null) {
                getSupportFragmentManager().putFragment(outState, RETAINED_EDIT_FRAGMENT, editFragment);
            }

        } else {

            if (listFragment != null && listFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, RETAINED_LIST_FRAGMENT, listFragment);
            }

            if (detailFragment != null && detailFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, RETAINED_DETAIL_FRAGMENT, detailFragment);
            }

            if (editFragment != null && editFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, RETAINED_EDIT_FRAGMENT, editFragment);
            }
        }
    }
}
