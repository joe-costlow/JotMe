package com.josephcostlow.jotme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        ListFragment.OnToolbarTitleTextEdit,
        ListFragment.OnItemClick,
        ListFragment.OnFABHide,
        ListFragment.OnDataUpdate,
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
    public static final String SHARED_PREFS_ORIGINAL_LIST_SIZE = "originalListSize";
    public static final String SHARED_PREFS_WIDGET_INTENT = "widgetIntent";
    public static final int RC_SIGN_IN = 1;
    private static final String ACTION_DATA_UPDATED = "android.appwidget.action.ACTION_DATA_UPDATED";
    public static SharedPreferences sharedPreferences;
    public static boolean mSearchMode;

    //    Fragments
    public ListFragment listFragment;
    public DetailFragment detailFragment;
    public EditFragment editFragment;

    //    misc
    boolean mDualPane;
    //    Toolbar
    Toolbar mainToolbar;
    TextView mainToolbarTitle;
    SearchView searchView;
    MenuItem menuSearch;
    MenuItem menuSignOut;
    //    Floating Action Buttons
    FloatingActionButton addFAB, cancelFAB, saveFAB, editFAB, deleteFAB;
    //    Firebase
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private boolean widgetIntent;
    private int clickedPosition;
    private boolean recyclerIsEmpty;
    private boolean emptyInstance;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseJobDispatcher dispatcher;
    private Job reminderNotificationJob;

    /**
     * Method executed when the toolbar menu is created
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        menuSearch = menu.findItem(R.id.menu_search);
        menuSignOut = menu.findItem(R.id.menu_signout);

        searchView = (SearchView) menuSearch.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.searchview_hint));

        searchView.setVisibility(View.GONE);
        menuSearch.setVisible(false);

        menuSignOut.setVisible(true);

        return true;
    }

    /**
     * This method is executed when the toolbar menu is created and each time it is accessed.
     * <p>
     * The sign out icon is made visible, then based on screen size (mDualPane), the sign out icon
     * visibility is determined. In single pane, the sign out icon is visible only on the list
     * screen. In dual pane, if an instance of EditFragment is visible, sign out is not visible.
     * <p>
     * The search icon has a click and close listener. When the search icon is clicked, the list
     * is populated using another adapter, search mode is set to true, to hide FABs, and the sign
     * out icon is hidden. Also, in single pane, the custom TextView title is hidden, to make room
     * for the search bar. When the search icon is closed, the sign out icon and title TextView are
     * made visible and search mode is made false.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menuSignOut.setVisible(true);

        if (mDualPane) {

            Fragment testLeftFragment = getSupportFragmentManager().findFragmentById(R.id.frame_left);
            Fragment testFragment = getSupportFragmentManager().findFragmentById(R.id.frame_right);

            if (testLeftFragment instanceof ListFragment && testFragment instanceof EditFragment) {
                menu.findItem(R.id.menu_search).setVisible(false);
                searchView.setVisibility(View.GONE);
            }

        } else {

            Fragment testFragment = getSupportFragmentManager().findFragmentById(R.id.frame_full);

            if (testFragment instanceof DetailFragment || testFragment instanceof EditFragment) {
                menu.findItem(R.id.menu_search).setVisible(false);
                searchView.setVisibility(View.GONE);
            }
        }

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menuSignOut.setVisible(false);

                SearchMode();

                mSearchMode = true;

                listFragment.setupFilterAdapter();

                if (!mDualPane) {
                    mainToolbarTitle.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                menuSignOut.setVisible(true);

                mSearchMode = false;

                EnterHideFABList();

                if (!mDualPane) {
                    mainToolbarTitle.setVisibility(View.VISIBLE);
                }

                if (mDualPane) {

                    UIUpdate();
                }

                listFragment.setupAdapter();

                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This method is executed when a menu item is clicked
     *
     * When the home button is pressed, the onBackPressed method is called.
     *
     * When the sign out icon is clicked, the authenticated user (Firebase) is signed out and all
     * shared preferences are cleared.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (item.getItemId() == R.id.menu_signout) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intentToWidget = new Intent(ACTION_DATA_UPDATED);
            sendBroadcast(intentToWidget);

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_signed_out), Toast.LENGTH_SHORT).show();

            AuthUI.getInstance().signOut(this);
//            mFirebaseAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method sets initializes the FABs and toolbar. Any retained fragments are recovered.
     * If in dual pane, an instance of ListFragment is put on the left side and an instance of
     * DetailFragment is put on the right side. In single pane, an instance of ListFragment is put
     * full screen. First, an authentication change listener is used to determine if there is a
     * current authenticated user already signed in (Firebase).
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

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

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        HideAllFABs();

        if (mDualPane) {

            if (savedInstanceState == null) {

                emptyInstance = true;

            } else {

                listFragment = (ListFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_LIST_FRAGMENT);

                detailFragment = (DetailFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_DETAIL_FRAGMENT);

                editFragment = (EditFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, RETAINED_EDIT_FRAGMENT);

                emptyInstance = false;
            }

        } else {

            if (savedInstanceState == null) {

                emptyInstance = true;

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

                    emptyInstance = false;
                }
            }
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    HideAllFABs();
                    ResetFABs();

                    dispatcher.cancelAll();

                    Intent intentToWidget = new Intent(ACTION_DATA_UPDATED);
                    sendBroadcast(intentToWidget);

                    launchFragments();

                } else {

                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().createSignInIntentBuilder().build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /**
     * This method is used to launch the appropriate fragments based on screen size, single or dual
     * pane.
     *
     * Also, the intent to launch this activity is checked to determine if the intent was from the
     * home screen widget. If so, the selected position is adjusted to display the correct list item
     * in the main list.
     *
     */
    public void launchFragments() {

        if (emptyInstance) {

            Intent intentFromWidget = getIntent();

            int listSize = sharedPreferences.getInt(SHARED_PREFS_ORIGINAL_LIST_SIZE, 0);

            if (listSize == 0) {

                clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, 1000);

            } else {

                clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, listSize);
            }

            if (intentFromWidget != null) {

                if (intentFromWidget.hasExtra("position")) {

                    int widgetClickPosition = intentFromWidget.getIntExtra("position", 0);

                    widgetIntent = true;

                    switch (widgetClickPosition) {

                        case 2:
                            clickedPosition = listSize - 1;
                            break;

                        case 1:

                            if (listSize > 2) {

                                clickedPosition = listSize - 2;

                            } else {

                                clickedPosition = listSize - 1;
                            }

                            break;

                        case 0:
                            clickedPosition = listSize - 3;
                            break;
                    }

                    if (clickedPosition < 0) {
                        clickedPosition = 0;
                    }
                }

            } else {

                widgetIntent = false;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition);
            editor.putBoolean(SHARED_PREFS_WIDGET_INTENT, widgetIntent);
            editor.apply();

            if (mDualPane) {

                listFragment = new ListFragment();
                detailFragment = new DetailFragment();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_left, listFragment, INITIAL_LIST_FRAGMENT)
                        .replace(R.id.frame_right, detailFragment, INITIAL_DETAIL_FRAGMENT)
                        .commit();

            } else {

                listFragment = new ListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
                        .commit();
            }
        }
    }

    /**
     * This method creates the Job used to send reminder notifications on the device.
     *
     * @param jobDispatcher initialized in onCreate (@dispatcher)
     * @return
     */
    public Job createReminderNotificationJob(FirebaseJobDispatcher jobDispatcher) {

        Job job = jobDispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(ReminderNotificationJob.class)
                .setTag("reminder-notification-job")
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(60 * 1, 60 * 2))
                .build();

        return job;
    }

    /**
     * This method is executed when the sign in activity is returned.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
//                Sign-in succeeded, set up UI
                Toast.makeText(this, getResources().getString(R.string.toast_signed_in), Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
//                Sign in was canceled by the user, finish the activity
                Toast.makeText(this, getResources().getString(R.string.toast_signed_in_cancelled), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * This method is executed when a FAB is clicked.
     *
     * Add FAB:
     *      The toolbar title is changed. Blank values are sent to a new EditFragment instance and
     *      that instance is put on the screen. In dual pane, it is placed on the right side. In
     *      single pane, it is full screen.
     *
     * Edit FAB:
     *      The toolbar title is changed. Values of the currently displayed Jot in the existing
     *      instance of DetailFragment are aggregated and sent to a new instance of EditFragment.
     *      This instance is then displayed on the screen.
     *
     * Cancel FAB:
     *      If dual pane is being used, the existing instance of DetailFragment replaces EditFragment.
     *      If single pane, onBackPressed method is executed.
     *
     * Save FAB:
     *      Values of the edittexts from the existing EditFragment are aggregated and sent to either
     *      addJot or editJot method in the existing ListFragment instance. A new instance of
     *      DetailFragment replaces EditFragment instance on screen.
     *
     * Delete FAB:
     *      The position of the current Jot is sent to ListFragment.deleteJot method.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        String toolbarTitle, title, tagOne, tagTwo, tagThree, message;

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, 0);

        int fabID = v.getId();
        switch (fabID) {
//    ADD FAB
            case R.id.fab_add:

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

                toolbarTitle = getResources().getString(R.string.main_toolbar_title_edit);
                mainToolbarTitle.setText(toolbarTitle);

                String[] currentJot = detailFragment.DataForEdit();

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

                String[] dataToSave = editFragment.dataToSave();

                title = dataToSave[0];
                tagOne = dataToSave[1];
                tagTwo = dataToSave[2];
                tagThree = dataToSave[3];
                message = dataToSave[4];

                if (mainToolbarTitle.getText().toString()
                        .equals(getResources().getString(R.string.main_toolbar_title_edit))) {
                    listFragment.editJot(title, tagOne, tagTwo, tagThree, message);
                }

                if (mainToolbarTitle.getText().toString()
                        .equals(getResources().getString(R.string.main_toolbar_title_add))) {
                    listFragment.addJot(title, tagOne, tagTwo, tagThree, message);
                }

                if (mDualPane) {

                    detailFragment = DetailFragment.newInstance(title, tagOne, tagTwo, tagThree, message);

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

                listFragment.deleteJot(clickedPosition);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_full, listFragment, INITIAL_LIST_FRAGMENT)
                        .commit();

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
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

        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        if (user != null) {

            dispatcher.cancelAll();
            reminderNotificationJob = createReminderNotificationJob(dispatcher);
            dispatcher.schedule(reminderNotificationJob);
        }
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

    /**
     * Method executed when back button is pressed on device or back arrow in toolbar.
     *
     * In dual pane, if an instance of EditFragment is not null and visible, the existing instance
     * of DetailFragment replaces EditFragment. If an instance of DetailFragment is not null and
     * visible, the app finishes. In single pane, if an instance of ListFragment is not null and
     * visible, the app finishes. In other cases, the super.onBackPressed method is executed.
     *
     */
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

            if (listFragment != null && listFragment.isVisible()) {

                finish();

            } else {

                super.onBackPressed();
            }
        }
    }

    /**
     * Method implemented by interface of ListFragment. Directs to RecyclerItemClick method.
     *
     * @param title title of selected Jot
     * @param tagOne first tag of selected Jot
     * @param tagTwo second tag of selected Jot
     * @param tagThree third tag of selected Jot
     * @param message message of selected Jot
     */
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

            mSearchMode = false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_full, detailFragment, INITIAL_DETAIL_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void EditToolbarText(String title) {
        mainToolbarTitle.setVisibility(View.VISIBLE);
        mainToolbarTitle.setText(title);
    }

    /**
     * Method is executed when a new instance of ListFragment is created.
     *
     * All FABs are initially hidden. If the user is not utilizing the search feature, the Add FAB
     * is made visible. In dual pane, the Edit FAB is also made visible, unless there are no items
     * in the list.
     */
    @Override
    public void EnterHideFABList() {
        HideAllFABs();

        if (!mSearchMode) {

            ShowAddFAB();

            if (mDualPane) {
                ShowEditFAB();
            }
        }

        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty) {
            HideEditFAB();
        }
    }

    @Override
    public void ExitHideFABList() {
        HideAllFABs();
    }

    @Override
    public void SearchMode() {
        HideAddFAB();
        HideEditFAB();
    }

    /**
     * This method is executed when a new instance of DetailFragment is created.
     *
     * All FABs are initially hidden. If the user is not utilizing the search feature, the Edit FAB
     * is made visible, unless there are no items in the list.
     * In dual pane, the Add FAB is also made visible. In single pane, the Delete FAB, is made visible.
     */
    @Override
    public void EnterHideFABDetail() {
        HideAllFABs();

        if (!mSearchMode) {

            ShowEditFAB();

            if (mDualPane) {

                ShowAddFAB();

            } else {

                ShowDeleteFAB();
            }
        }

        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

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

    /**
     * Method is executed when leaving EditFragment instance.
     *
     * If there are no items in the list, Edit FAB is hidden. ResetFABs method is then executed.
     */
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

    /**
     * This method is executed during onRestart method, after authentication of user (Firebase),
     * and after exiting an instance of EditFragment.
     *
     * In dual pane, if an instance of ListFragment is not null and visible, all FABs are initally
     * hidden. Add and Edit FABs are made visible, unless there are no items in the list, then Edit
     * FAB is hidden. If an instance of EditFragment is not null and visible, all FABs are hidden
     * and only Cancel FAB is made visible.
     *
     * In single pane, all FABs are initially hidden. Appropriate FABs are made visible based on
     * which fragment is not null and visible.
     */
    public void ResetFABs() {

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_EDIT_FRAGMENT);

        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

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

    @Override
    public void UIUpdate() {

        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_LIST_FRAGMENT);

        detailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentByTag(INITIAL_DETAIL_FRAGMENT);

        if (listFragment != null && listFragment.isVisible()) {
            listFragment.UpdateUIList();
        }

        if (detailFragment != null && detailFragment.isVisible()) {
            detailFragment.UpdateUIDetail();
        }
    }
}
