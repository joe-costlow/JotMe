package com.josephcostlow.jotme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        ListFragment.OnToolbarTitleTextEdit,
        ListFragment.OnItemClick,
        ListFragment.OnFABHide,
        DetailFragment.OnToolbarTitleTextEdit,
        DetailFragment.OnFABHide,
        EditFragment.OnToolbarTitleTextEdit,
        EditFragment.OnFABHide {

//    constants for fragment tags
    public static final String RETAINED_LIST_FRAGMENT = "retainedListFragment";
    public static final String RETAINED_DETAIL_FRAGMENT = "retainedDetailFragment";
    public static final String RETAINED_EDIT_FRAGMENT = "retainedEditFragment";
    public static final String INITIAL_LIST_FRAGMENT = "initialListFragment";
    public static final String INITIAL_DETAIL_FRAGMENT = "initialDetailFragment";
    public static final String INITIAL_EDIT_FRAGMENT = "initialEditFragment";

//    constants for saving state
public static final String TOOLBAR_TITLE = "toolbarTitleKey";
    public static final String TITLE_KEY = "title";
    public static final String TAG_ONE_KEY = "tagOne";
    public static final String TAG_TWO_KEY = "tagTwo";
    public static final String TAG_THREE_KEY = "tagThree";
    public static final String MESSAGE_KEY = "message";

//    constants for shared preferences
    public static final String SHARED_PREFS_FILENAME = "com.josephcostlow.jotme.shared";
    public static final String SHARED_PREFS_AUTO_SELECT_KEY = "autoSelector";
    public static final String SHARED_PREFS_CLICKED_POSITION_KEY = "clickedPosition";
    public static final String SHARED_PREFS_EMPTY_RECYCLER_KEY = "emptyRecycler";
    public static final String SHARED_PREFS_SAVE_FAB_VISIBLE = "saveFABVisible";

//    constants for auto-select and clicked positions for List Fragment and List Adapter
    public ListFragment listFragment;
    public DetailFragment detailFragment;
    public EditFragment editFragment;

    //    misc
    Toolbar mainToolbar;
    TextView mainToolbarTitle;
    boolean mDualPane;
    FloatingActionButton addFAB, cancelFAB, saveFAB, editFAB, deleteFAB;
    SharedPreferences sharedPreferences;

//    TODO create new mock Jot and save - shows on list, no autoselect, rotate - disappear from list, stays shown in details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILENAME, 0);

        mainToolbarTitle = (TextView) findViewById(R.id.main_toolbar_title);

        mDualPane = getResources().getBoolean(R.bool.dual_pane);

        addFAB = (FloatingActionButton) findViewById(R.id.fab_add);
        addFAB.setOnClickListener(this);

        editFAB = (FloatingActionButton) findViewById(R.id.fab_edit);
        editFAB.setOnClickListener(this);

        cancelFAB = (FloatingActionButton) findViewById(R.id.fab_cancel);
        cancelFAB.setOnClickListener(this);

        saveFAB = (FloatingActionButton) findViewById(R.id.fab_save);
        saveFAB.setOnClickListener(this);

        deleteFAB = (FloatingActionButton) findViewById(R.id.fab_delete);
        deleteFAB.setOnClickListener(this);

        HideAllFABs();
        ResetFABs();

        if (mDualPane) {

            if (savedInstanceState == null) {

                listFragment = new ListFragment();
                detailFragment = new DetailFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_left, listFragment, INITIAL_LIST_FRAGMENT)
                        .add(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .commit();

            } else {

                listFragment = (ListFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_DETAIL_FRAGMENT);

                editFragment = (EditFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_EDIT_FRAGMENT);
            }

        } else {

            if (savedInstanceState == null) {

                listFragment = new ListFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
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
    public void onClick(View v) {
//        TODO real data will have unique post ID
        String toolbarTitle, title, tagOne, tagTwo, tagThree, message;

        int fabID = v.getId();
        switch (fabID) {
//    ADD FAB
            case R.id.fab_add:
                listFragment = (ListFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

                toolbarTitle = getResources().getString(R.string.main_toolbar_title_add);

                title = "";
                tagOne = "";
                tagTwo = "";
                tagThree = "";
                message = "";

                editFragment = EditFragment.newInstance(toolbarTitle, title, tagOne, tagTwo, tagThree, message);

                if (mDualPane) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_right, editFragment, INITIAL_EDIT_FRAGMENT)
                            .addToBackStack(null)
                            .commit();

                } else {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_full, editFragment, INITIAL_EDIT_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
                }

                break;
//    EDIT FAB
            case R.id.fab_edit:
                listFragment = (ListFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

                toolbarTitle = getResources().getString(R.string.main_toolbar_title_edit);
                mainToolbarTitle.setText(toolbarTitle);

                String[] currentJot = detailFragment.DataForEdit();
//        TODO real data will have unique post ID
                title = currentJot[0];
                tagOne = currentJot[1];
                tagTwo = currentJot[2];
                tagThree = currentJot[3];
                message = currentJot[4];

                editFragment = EditFragment.newInstance(toolbarTitle, title, tagOne, tagTwo, tagThree, message);

                if (mDualPane) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_right, editFragment, INITIAL_EDIT_FRAGMENT)
                            .addToBackStack(null)
                            .commit();

                } else {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_full, editFragment, INITIAL_EDIT_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
                }

                break;
//    CANCEL FAB
            case R.id.fab_cancel:
                listFragment = (ListFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

                editFragment = (EditFragment) getSupportFragmentManager()
                        .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

                if (mDualPane) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                            .commit();

                } else {

                    onBackPressed();
                }

                break;
//    SAVE FAB
            case R.id.fab_save:
//        TODO real data will have unique post ID - WORKS FOR ONLY NEW JOTS
                String[] dataToSave = editFragment.dataToSave();

                title = dataToSave[0];
                tagOne = dataToSave[1];
                tagTwo = dataToSave[2];
                tagThree = dataToSave[3];
                message = dataToSave[4];

                listFragment.addJot(title, tagOne, tagTwo, tagThree, message);

                if (mDualPane) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                            .commit();

                } else {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
                            .commit();
                }

                break;
//    DELETE FAB
            case R.id.fab_delete:

                break;
        }

    }

    @Override
    protected void onRestart() {

        ResetFABs();

        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onBackPressed() {

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

        if (mDualPane) {

            if (editFragment != null && editFragment.isVisible()) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .commit();
            }

            if (detailFragment != null && detailFragment.isVisible()) {
                finish();
            }

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void OnListItemClick(String title, String tagOne, String tagTwo, String tagThree, String message) {

        RecyclerItemClick(title, tagOne, tagTwo, tagThree, message);

    }

    public void RecyclerItemClick(String title, String tagOne, String tagTwo, String tagThree, String message) {

        detailFragment = DetailFragment.newInstance(title, tagOne, tagTwo, tagThree, message);

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

    @Override
    public void EditToolbarText(String title) {
        mainToolbarTitle.setText(title);
    }

    @Override
    public void EnterHideFABList() {
        HideAllFABs();

        ShowAddFAB();

        if (mDualPane) {
            ShowEditFAB();
        }

        boolean recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty) {
            HideEditFAB();
        }
    }

    @Override
    public void ExitHideFABList() {
        HideAllFABs();
    }

    @Override
    public void EnterHideFABDetail() {
        HideAllFABs();

        ShowEditFAB();

        if (mDualPane) {

            ShowAddFAB();

        } else {

            ShowDeleteFAB();
        }

        boolean recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty) {
            HideEditFAB();
        }
    }

    @Override
    public void ExitHideFABDetail() {
        HideAllFABs();
    }

    @Override
    public void EnterHideFABEdit() {
        HideAllFABs();

        ShowSaveFAB();
        ShowCancelFAB();
    }

    @Override
    public void ExitHideFABEdit() {

        boolean recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty) {
            HideEditFAB();
        }

        ResetFABs();
    }

    @Override
    public void HideSaveFABEdit() {
        HideSaveFAB();
    }

    @Override
    public void ShowSaveFABEdit() {
        ShowSaveFAB();
    }

    public void HideAllFABs() {
        HideCancelFAB();
        HideSaveFAB();
        HideAddFAB();
        HideDeleteFAB();
        HideEditFAB();
    }

    public void ShowAddFAB() {
        addFAB.setVisibility(View.VISIBLE);
    }

    public void HideAddFAB() {
        addFAB.setVisibility(View.GONE);
    }

    public void ShowEditFAB() {
        editFAB.setVisibility(View.VISIBLE);
    }

    public void HideEditFAB() {
        editFAB.setVisibility(View.GONE);
    }

    public void ShowDeleteFAB() {
        deleteFAB.setVisibility(View.VISIBLE);
    }

    public void HideDeleteFAB() {
        deleteFAB.setVisibility(View.GONE);
    }

    public void ShowCancelFAB() {
        cancelFAB.setVisibility(View.VISIBLE);
    }

    public void HideCancelFAB() {
        cancelFAB.setVisibility(View.GONE);
    }

    public void ShowSaveFAB() {
        saveFAB.setVisibility(View.VISIBLE);
    }

    public void HideSaveFAB() {
        saveFAB.setVisibility(View.GONE);
    }

    public void ResetFABs() {

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

        boolean recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (mDualPane) {

            if (listFragment != null && listFragment.isVisible()) {

                HideAllFABs();
                ShowAddFAB();
                ShowEditFAB();

                if (recyclerIsEmpty) {
                    HideAllFABs();
                    ShowAddFAB();
                }
            }

            if (editFragment != null && editFragment.isVisible()) {
                HideAllFABs();
                ShowCancelFAB();
            }

        } else {

            if (listFragment != null && listFragment.isVisible()) {
                HideAllFABs();
                ShowAddFAB();
            }

            if (detailFragment != null && detailFragment.isVisible()) {
                HideAllFABs();
                ShowEditFAB();
                ShowDeleteFAB();
            }

            if (editFragment != null && editFragment.isVisible()) {
                HideAllFABs();
                ShowCancelFAB();
            }
        }
    }
}
