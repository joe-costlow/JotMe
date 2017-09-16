package com.josephcostlow.jotme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment implements JotAdapter.ClickListener {
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ACTION_DATA_UPDATED = "android.appwidget.action.ACTION_DATA_UPDATED";
    private final SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
    List<Jot> jotsData;
    Context context;
    JotAdapter mAdapter;
    RecyclerView recyclerView;
    CardView emptyRecyclerCard;
    TextView emptyView;
    int emptyClickedPosition = 0;
    ItemClickListener itemClickListener;
    SearchView searchView;
    MenuItem menuSearch;
    private boolean autoSelector;
    private int clickedPosition;
    private boolean recyclerIsEmpty;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private boolean mDualPane;
    private ItemTouchHelper itemTouchHelper;
    private boolean mSearchMode;
    private String mUserID;
    private FilteredAdapter mFilteredAdapter;
    private List<Jot> originalJotList;
    private boolean widgetIntent;
    //    Shared Preferences
    private String SHARED_PREFS_FILENAME = MainActivity.SHARED_PREFS_FILENAME;
    private String SHARED_PREFS_AUTO_SELECT_KEY = MainActivity.SHARED_PREFS_AUTO_SELECT_KEY;
    private String SHARED_PREFS_CLICKED_POSITION_KEY = MainActivity.SHARED_PREFS_CLICKED_POSITION_KEY;
    private String SHARED_PREFS_EMPTY_RECYCLER_KEY = MainActivity.SHARED_PREFS_EMPTY_RECYCLER_KEY;
    private String SHARED_PREFS_ORIGINAL_LIST_SIZE = MainActivity.SHARED_PREFS_ORIGINAL_LIST_SIZE;
    private String SHARED_PREFS_WIDGET_INTENT = MainActivity.SHARED_PREFS_WIDGET_INTENT;
    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mJotsDatabase;
    private DatabaseReference mJotsDatabaseReference;
    private DatabaseReference mJotsDatabaseUsersReference;
    private DatabaseReference mJotsDatabaseSpecificUserReference;
    private ChildEventListener mChildEventListener;
    private String mUsername;

    //    Interfaces
    private OnItemClick mOnClickListener;
    private OnFABHide mFABHide;
    private OnToolbarTitleTextEdit mEditTitle;
    private OnDataUpdate mDataUpdate;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setRetainInstance(false);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuSearch = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menuItem.getActionView();

//        If the list of Jots is not empty, make search feature icon visible. If the list is empty,
//        hide the search feature icon.
        if (jotsData.size() != 0) {

            menuSearch.setVisible(true);
            searchView.setVisibility(View.VISIBLE);

        } else {

            menuSearch.setVisible(false);
            searchView.setVisibility(View.GONE);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Create a new list to serve as the filtered list of Jots
                List<Jot> newList = new ArrayList<>();
//                Get the values of the tags for each Jot of the original list. If the second or
//                third tags are the default value, set their value to empty, so they are not included.
                for (Jot jot : originalJotList) {

                    String searchTagOne = jot.getTagOne();
                    String searchTagTwo = jot.getTagTwo();
                    String searchTagThree = jot.getTagThree();

                    if (jot.getTagTwo().equals(getResources().getString(R.string.empty_tag_edit))) {

                        searchTagTwo = "";
                    }

                    if (jot.getTagThree().equals(getResources().getString(R.string.empty_tag_edit))) {

                        searchTagThree = "";
                    }
//                    If the tags contain the value of the search, add the Jot to the filtered list
                    if (searchTagOne.contains(newText)
                            || searchTagTwo.contains(newText)
                            || searchTagThree.contains(newText)) {
                        newList.add(jot);
                    }
                }

                clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, mAdapter.getItemCount() - 1);
//                Iterate through each Jot in the filtered list, find the corresponding position of
//                the currently selected Jot in the original list. When a list is filtered, each item
//                in the filtered list is given an adapter position of the filtered list. This is used
//                to find the adapter position of the currently selected list item within the filtered
//                list.
                int position = -1;

                for (Jot filteredJot : originalJotList) {

                    position++;

                    if (!jotsData.isEmpty()) {

                        if (filteredJot.getUniqueID().equals(jotsData.get(clickedPosition).getUniqueID())) {

                            emptyClickedPosition = position;
                        }
                    }
                }

//                If there is a value in the search feature edittext, set a new adapter to the
//                recycler, to allow for the filtering of the list.
                if (!newText.isEmpty()) {

                    jotsData = newList;
                    mFilteredAdapter = new FilteredAdapter(context, jotsData, ListFragment.this);
                    recyclerView.setAdapter(mFilteredAdapter);
                    mFilteredAdapter.setFilter(jotsData);

                    if (!jotsData.isEmpty() && mDualPane) {
                        publicOnClick(mFilteredAdapter.getItemCount() - 1);
                    }

                } else {
//                    If the search feature is activated, but the search edittext is empty, set a new
//                    adapter to the recycler, using the original list. Use the adjusted position
//                    to display the details of the currently selected Jot.
                    jotsData = originalJotList;
                    mFilteredAdapter = new FilteredAdapter(context, originalJotList, ListFragment.this);
                    recyclerView.setAdapter(mFilteredAdapter);

                    if (!jotsData.isEmpty() && mDualPane) {
                        publicOnClick(emptyClickedPosition);
                    }
                }

                mDataUpdate.UIUpdate();

                mFABHide.SearchMode();

                return true;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        emptyRecyclerCard = (CardView) rootView.findViewById(R.id.empty_recycler_card);
        emptyView = (TextView) rootView.findViewById(R.id.empty_recycler_textview);

        context = getContext();
        mDualPane = context.getResources().getBoolean(R.bool.dual_pane);

        mEditTitle.EditToolbarText(getResources().getString(R.string.app_name));

        mUsername = ANONYMOUS;

        mSearchMode = MainActivity.mSearchMode;

        jotsData = new ArrayList<>();

        setupFirebase();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        HideRecycler();

        setupAdapter();

        mJotsDatabaseSpecificUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                On data change, send a broadcase to the widget to update
                Intent intentToWidget = new Intent(ACTION_DATA_UPDATED);
                context.sendBroadcast(intentToWidget);
//                Populate the list used to populate the recycler, using the snapshot data
                populateLocalList(dataSnapshot);

                mDataUpdate.UIUpdate();

                if (mDualPane) {

                    setupAdapter();
                }
//                If the snapshot of the user's node has children, make the search feature icon visible,
//                hide if there are no children.
                if (dataSnapshot.hasChildren()) {

                    if (searchView != null) {

                        menuSearch.setVisible(true);
                        searchView.setVisibility(View.VISIBLE);
                    }

                } else {

                    if (searchView != null) {

                        menuSearch.setVisible(false);
                        searchView.setVisibility(View.GONE);
                    }
                }

                attachItemTouchHelper();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mEditTitle = (OnToolbarTitleTextEdit) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnToolbarTitleTextEdit");
        }

        try {
            mOnClickListener = (OnItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnItemClick");
        }

        try {
            mFABHide = (OnFABHide) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnFABHide");
        }

        try {
            mDataUpdate = (OnDataUpdate) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnDataUpdate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Method is used to set up Firebase Database with a reference to a specific authenticated user's
     * node.
     */
    public void setupFirebase() {

        mJotsDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        mUserID = mUser.getUid();

        mJotsDatabaseReference = mJotsDatabase.getReference();
        mJotsDatabaseUsersReference = mJotsDatabaseReference.child("users");
        mJotsDatabaseSpecificUserReference = mJotsDatabaseUsersReference.child(mUserID);
        mJotsDatabaseSpecificUserReference.keepSynced(true);
    }

    public void UpdateUIList() {

        mFABHide.EnterHideFABList();

        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);
        widgetIntent = sharedPreferences.getBoolean(SHARED_PREFS_WIDGET_INTENT, false);
//        If the current adapeter of the recycler is empty, hide the recycler and show the empty view
        if (recyclerView.getAdapter().getItemCount() == 0) {

            recyclerIsEmpty = true;
            HideRecycler();

        } else {
//            If the current adapter of the recycler is not empty, show the recycler.
            recyclerIsEmpty = false;
            ShowRecycler();

            clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, mAdapter.getItemCount() - 1);

            if (mDualPane) {
//                Adjust the selected position if the selected position is equal to or greater than the
//                size of the list.
                if (clickedPosition > recyclerView.getAdapter().getItemCount() - 1) {
                    clickedPosition = recyclerView.getAdapter().getItemCount() - 1;
                }
//                Try to smooth scroll the recycler to the selected position.
                try {
                    recyclerView.smoothScrollToPosition(clickedPosition);
                } catch (Exception e) {
                    Log.v("UPDATE ERROR", e.toString());
                }
//                Display the details of the selected Jot in the DetailFragment instance.
                if (!jotsData.isEmpty()) {
                    publicOnClick(clickedPosition);
                }

            } else {
//                If the intent to the MainActivity was from the widget, and single pane is used,
//                details of the selected item will be displayed.
                if (widgetIntent) {
                    widgetIntent = false;
                    publicOnClick(clickedPosition);
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition);
        editor.putBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, recyclerIsEmpty);
        editor.putBoolean(SHARED_PREFS_WIDGET_INTENT, widgetIntent);
        editor.apply();
    }

    /**
     * Method is used to populate two Lists with data from Firebase Database.
     *
     * @param dataSnapshot snapshot of data from the user's node
     */
    public void populateLocalList(DataSnapshot dataSnapshot) {

        originalJotList = new ArrayList<>();

        jotsData = new ArrayList<>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Jot jot = new Jot();

            jot.setTitle(ds.getValue(Jot.class).getTitle());
            jot.setTagOne(ds.getValue(Jot.class).getTagOne());
            jot.setTagTwo(ds.getValue(Jot.class).getTagTwo());
            jot.setTagThree(ds.getValue(Jot.class).getTagThree());
            jot.setMessage(ds.getValue(Jot.class).getMessage());
            jot.setUniqueID(ds.getValue(Jot.class).getUniqueID());

            jotsData.add(jot);

            originalJotList.add(jot);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_ORIGINAL_LIST_SIZE, originalJotList.size());
        editor.apply();
    }

    public void setupAdapter() {

        mAdapter = new JotAdapter(Jot.class, R.layout.list_item, JotViewHolder.class, mJotsDatabaseSpecificUserReference, ListFragment.this);

        recyclerView.setAdapter(mAdapter);
    }

    public void setupFilterAdapter() {

        mFilteredAdapter = new FilteredAdapter(context, jotsData, ListFragment.this);

        recyclerView.setAdapter(mFilteredAdapter);
    }

    /**
     * Method is used to attach an ItemTouchHelper to list items, allowing for swipe to delete function.
     * <p>
     * If the user is not in search mode and not editing an existing Jot or adding a new Jot, swipe
     * to delete function is available. If editing, adding, or searching, swipe to delete function
     * is unavailable.
     */
    public void attachItemTouchHelper() {

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                mSearchMode = MainActivity.mSearchMode;

                if (mDualPane) {

                    Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

                    if ((testFragment instanceof EditFragment) || mSearchMode) {

                        return 0;
                    }

                } else {

                    if (mSearchMode) {

                        return 0;
                    }
                }

                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (mDualPane) {

                    Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

                    if (!(testFragment instanceof EditFragment)) {

                        int id = viewHolder.getAdapterPosition();
                        deleteJot(id);
                    }

                } else {

                    int id = viewHolder.getAdapterPosition();
                    deleteJot(id);
                }
            }
        };

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void ShowRecycler() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyRecyclerCard.setVisibility(View.GONE);
    }

    public void HideRecycler() {

        recyclerView.setVisibility(View.GONE);
        emptyRecyclerCard.setVisibility(View.VISIBLE);
        SetEmptyRecyclerText();
    }

    private void SetEmptyRecyclerText() {
        emptyView.setText(R.string.empty_recycler_text);
    }

    /**
     * Method is executed when Save FAB is clicked, if the EditFragment instance has set the title
     * textview of the toolbar to reflect a new Jot.
     *
     * @param title title of the added Jot
     * @param tagOne first tag of the added Jot
     * @param tagTwo second tag of the added Jot
     * @param tagThree third tag of the added Jot
     * @param message message of the added Jot
     */
    public void addJot(String title, String tagOne, String tagTwo, String tagThree, String message) {
//        Unique push ID of the data within the database
        String id = mJotsDatabaseReference.push().getKey();

        Jot jotToSave = new Jot(title, tagOne, tagTwo, tagThree, message, id);
//        Add the Jot to the list in the recycler
        jotsData.add(jotToSave);
//        Set the value of the unique ID of the Jot to the unique push ID within the database
        mJotsDatabaseSpecificUserReference.child(id).setValue(jotToSave);

        clickedPosition = jotsData.size();

        recyclerIsEmpty = false;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition);
        editor.putBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, recyclerIsEmpty);
        editor.apply();
    }

    /**
     * Method is executed when the Save FAB is clicked, if the EditFragment instance has set the title
     * textview of the toolbar to reflect an edited Jot.
     *
     * @param title title of the edited Jot
     * @param tagOne first tag of the edited Jot
     * @param tagTwo second tag of the edited Jot
     * @param tagThree third tag of the edited Jot
     * @param message message of the edited Jot
     */
    public void editJot(String title, String tagOne, String tagTwo, String tagThree, String message) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);
//        Get the unique ID of the list item currently selected and edited
        String uniqueID = jotsData.get(clickedPosition).getUniqueID();
//        Set data of Jot to respective values
        Jot jot = new Jot();

        jot.setTitle(title);
        jot.setTagOne(tagOne);
        jot.setTagTwo(tagTwo);
        jot.setTagThree(tagThree);
        jot.setMessage(message);
        jot.setUniqueID(uniqueID);
//        Set the values to the specific push ID (Jot) of the user's node
        mJotsDatabaseSpecificUserReference.child(uniqueID).setValue(jot);
    }

    /**
     * Method is executed when list item is swiped or when Delete FAB is clicked.
     *
     * @param position adapter position of list item to be deleted
     */
    public void deleteJot(int position) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);
//        If the swiped position is higher in the list than the currently selected position, adjust
//        the selected position to one position higher in the list
        if (position < clickedPosition) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition - 1);
            editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, false);
            editor.apply();
        }

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);
//        Remove the Jot from the database if there is not an instance of EditFragment visible or not
//        using the search feature
        if (mDualPane) {

            Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

            if (!(testFragment instanceof EditFragment) && !mSearchMode) {

                String uniqueID = jotsData.get(position).getUniqueID();

                mJotsDatabaseSpecificUserReference.child(uniqueID).removeValue();
            }

        } else {

            if (!mSearchMode) {

                String uniqueID = jotsData.get(position).getUniqueID();

                mJotsDatabaseSpecificUserReference.child(uniqueID).removeValue();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

        mFABHide.ExitHideFABList();

        mChildEventListener = null;

        if (!mDualPane) {
            mSearchMode = false;
        }
    }

    /**
     * Method is executed to display the details of a selected Jot.
     *
     * @param position adapter position of selected Jot from main list or widget list
     */
    @Override
    public void publicOnClick(int position) {

        if (mDualPane) {
//            If an instance of EditFragment is visible, disable clicking and detail viewing of Jot
            Fragment testFragment = ((MainActivity) context).getSupportFragmentManager().findFragmentById(R.id.frame_right);

            if (!(testFragment instanceof EditFragment)) {

                clickedPosition = position;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, autoSelector);
                editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, position);
                editor.apply();

                String title = jotsData.get(clickedPosition).getTitle();
                String tagOne = jotsData.get(clickedPosition).getTagOne();
                String tagTwo = jotsData.get(clickedPosition).getTagTwo();
                String tagThree = jotsData.get(clickedPosition).getTagThree();
                String message = jotsData.get(clickedPosition).getMessage();
                mOnClickListener.OnListItemClick(title, tagOne, tagTwo, tagThree, message);
            }

        } else {

            clickedPosition = position;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, autoSelector);
            editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, position);
            editor.apply();

            String title = jotsData.get(position).getTitle();
            String tagOne = jotsData.get(position).getTagOne();
            String tagTwo = jotsData.get(position).getTagTwo();
            String tagThree = jotsData.get(position).getTagThree();
            String message = jotsData.get(position).getMessage();
            mOnClickListener.OnListItemClick(title, tagOne, tagTwo, tagThree, message);
        }
    }

    public interface OnItemClick {
        void OnListItemClick(String title, String tagOne, String tagTwo, String tagThree, String message);
    }

    public interface OnFABHide {
        void EnterHideFABList();
        void ExitHideFABList();

        void SearchMode();
    }

    public interface OnToolbarTitleTextEdit {
        void EditToolbarText(String Title);
    }

    public interface OnDataUpdate {
        void UIUpdate();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
