package com.josephcostlow.jotme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemClick {

//    constants for fragment tags
    public static final String RETAINED_LIST_FRAGMENT = "retainedListFragment";
    public static final String RETAINED_DETAIL_FRAGMENT = "retainedDetailFragment";
    public static final String RETAINED_EDIT_FRAGMENT = "retainedEditFragment";
    public static final String INITIAL_LIST_FRAGMENT = "initialListFragment";
    public static final String INITIAL_DETAIL_FRAGMENT = "initialDetailFragment";
    public static final String INITIAL_EDIT_FRAGMENT = "initialEditFragment";

//    constants for bundle from adapter
    public static final String BUNDLE_TITLE = "title";
    public static final String BUNDLE_TAG_ONE = "tagOne";
    public static final String BUNDLE_TAG_TWO = "tagTwo";
    public static final String BUNDLE_TAG_THREE = "tagThree";
    public static final String BUNDLE_MESSAGE = "message";

//    constants for saving state
    public static final String TITLE_KEY = "title";
    public static final String TAG_ONE_KEY = "tagOne";
    public static final String TAG_TWO_KEY = "tagTwo";
    public static final String TAG_THREE_KEY = "tagThree";
    public static final String MESSAGE_KEY = "message";

//    private ArrayList<Jot> jots;
//    private String BUNDLE_POSITION = "position";
//    private Jot jot = new Jot();

    Toolbar mainToolbar;
    TextView mainToolbarTitle;

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
                editFragment = new EditFragment();
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

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

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

        RecyclerItemClick(title, tagOne, tagTwo, tagThree, message);

    }

    public void RecyclerItemClick(String title, String tagOne, String tagTwo, String tagThree, String message) {

        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(INITIAL_DETAIL_FRAGMENT);
//        editFragment = (EditFragment) getSupportFragmentManager().findFragmentByTag(INITIAL_EDIT_FRAGMENT);

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

//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frame_right, editFragment, INITIAL_EDIT_FRAGMENT)
//                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_full, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        }

//        if (editFragment != null && editFragment.isVisible()) {
//
//            editFragment.setEditText(title, tagOne, tagTwo, tagThree, message);
//
//        } else {
//
//            editFragment = new EditFragment();
//
//            Bundle bundle = new Bundle();
//            bundle.putString(BUNDLE_TITLE, title);
//            bundle.putString(BUNDLE_TAG_ONE, tagOne);
//            bundle.putString(BUNDLE_TAG_TWO, tagTwo);
//            bundle.putString(BUNDLE_TAG_THREE, tagThree);
//            bundle.putString(BUNDLE_MESSAGE, message);
//
//            editFragment.setArguments(bundle);
//
//            if (mDualPane) {
////                getSupportFragmentManager().beginTransaction()
////                        .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
////                        .commit();
//
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frame_right, editFragment, INITIAL_EDIT_FRAGMENT)
//                        .commit();
//            } else {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frame_full, editFragment, INITIAL_DETAIL_FRAGMENT)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        }
    }
}
