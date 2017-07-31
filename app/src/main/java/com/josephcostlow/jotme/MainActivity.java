package com.josephcostlow.jotme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemClick {

    private String RETAINED_LIST_FRAGMENT = "retainedListFragment";
    private String RETAINED_DETAIL_FRAGMENT = "retainedDetailFragment";
    private String RETAINED_EDIT_FRAGMENT = "retainedEditFragment";
    private String INITIAL_LIST_FRAGMENT = "initialListFragment";
    private String INITIAL_DETAIL_FRAGMENT = "initialDetailFragment";
    private String INITIAL_EDIT_FRAGMENT = "initialEditFragment";

    private String BUNDLE_TITLE = "title";
    private String BUNDLE_TAG_ONE = "tagOne";
    private String BUNDLE_TAG_TWO = "tagTwo";
    private String BUNDLE_TAG_THREE = "tagThree";
    private String BUNDLE_MESSAGE = "message";

//    private ArrayList<Jot> jots;
//    private String BUNDLE_POSITION = "position";
//    private Jot jot = new Jot();

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

                listFragment = new ListFragment();
                detailFragment = new DetailFragment();
//                editFragment = new EditFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_left, listFragment, INITIAL_LIST_FRAGMENT)
                        .add(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .commit();

            } else {

                listFragment = (ListFragment) getSupportFragmentManager()
                        .findFragmentByTag(RETAINED_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(RETAINED_DETAIL_FRAGMENT);

//                editFragment = (EditFragment) getSupportFragmentManager()
//                        .findFragmentByTag(RETAINED_EDIT_FRAGMENT);

            }

        } else {

            if (savedInstanceState == null) {

                listFragment = new ListFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
                        .commit();

//                detailFragment = new DetailFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.frame_full, detailFragment, INITIAL_DETAIL_FRAGMENT)
//                        .commit();

//                editFragment = new EditFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.frame_full, editFragment, INITIAL_EDIT_FRAGMENT)
//                        .commit();

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

    @Override
    public void OnListItemClick(String title, String tagOne, String tagTwo, String tagThree, String message) {

        OnClick(title, tagOne, tagTwo, tagThree, message);

    }

    public void OnClick(String title, String tagOne, String tagTwo, String tagThree, String message) {

        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        if (detailFragment != null && detailFragment.isVisible()) {

            detailFragment.setText(title, tagOne, tagTwo, tagThree, message);

        } else {

            detailFragment = new DetailFragment();

            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_TITLE, title);
            bundle.putString(BUNDLE_TAG_ONE, tagOne);
            bundle.putString(BUNDLE_TAG_TWO, tagTwo);
            bundle.putString(BUNDLE_TAG_THREE, tagThree);
            bundle.putString(BUNDLE_MESSAGE, message);

            detailFragment.setArguments(bundle);

            if (mDualPane) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_full, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        }

    }
}
