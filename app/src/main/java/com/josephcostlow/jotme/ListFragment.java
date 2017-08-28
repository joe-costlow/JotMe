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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
    //    private FirebaseRecyclerAdapter<Jot, JotViewHolder> mFirebaseAdapter;
    ItemClickListener itemClickListener;
    SearchView searchView;
    MenuItem menuSearch;
    private boolean autoSelector;
    private int clickedPosition;
    private boolean recyclerIsEmpty;
    private OnItemClick mOnClickListener;
    private OnFABHide mFABHide;
    private OnToolbarTitleTextEdit mEditTitle;
    private OnDataUpdate mDataUpdate;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private boolean mDualPane;
    private String SHARED_PREFS_FILENAME = MainActivity.SHARED_PREFS_FILENAME;
    private String SHARED_PREFS_AUTO_SELECT_KEY = MainActivity.SHARED_PREFS_AUTO_SELECT_KEY;
    private String SHARED_PREFS_CLICKED_POSITION_KEY = MainActivity.SHARED_PREFS_CLICKED_POSITION_KEY;
    private String SHARED_PREFS_EMPTY_RECYCLER_KEY = MainActivity.SHARED_PREFS_EMPTY_RECYCLER_KEY;
    private ItemTouchHelper itemTouchHelper;
    private boolean mSearchMode;
    private FirebaseDatabase mJotsDatabase;
    private DatabaseReference mJotsDatabaseReference;
    private DatabaseReference mJotsDatabaseUsersReference;
    private DatabaseReference mJotsDatabaseSpecificUserReference;
    private ChildEventListener mChildEventListener;
    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private String mUserID;
    private FilteredAdapter mFilteredAdapter;
    private List<Jot> originalJotList;

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
    // TODO: Rename and change types and number of parameters
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

        setRetainInstance(true);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuSearch = menu.findItem(R.id.menu_search);
        searchView = (SearchView) menuItem.getActionView();

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

                List<Jot> newList = new ArrayList<>(); //TODO ArrayList to List

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

                    if (searchTagOne.contains(newText)
                            || searchTagTwo.contains(newText)
                            || searchTagThree.contains(newText)) {
                        newList.add(jot);
                    }
                }

                clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, mAdapter.getItemCount() - 1);

                int position = -1;

                for (Jot filteredJot : originalJotList) {

                    position++;

                    if (!jotsData.isEmpty()) {

                        if (filteredJot.getUniqueID().equals(jotsData.get(clickedPosition).getUniqueID())) {

                            emptyClickedPosition = position;
                        }
                    }
                }

                if (!newText.isEmpty()) {

                    jotsData = newList;
                    mFilteredAdapter = new FilteredAdapter(context, jotsData, ListFragment.this);
                    recyclerView.setAdapter(mFilteredAdapter);
                    mFilteredAdapter.setFilter(jotsData);

                    if (!jotsData.isEmpty() && mDualPane) {
                        publicOnClick(mFilteredAdapter.getItemCount() - 1);
                    }

                } else {

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

        mJotsDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        mUserID = mUser.getUid();

        mJotsDatabaseReference = mJotsDatabase.getReference();
        mJotsDatabaseUsersReference = mJotsDatabaseReference.child("users");
        mJotsDatabaseSpecificUserReference = mJotsDatabaseUsersReference.child(mUserID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        HideRecycler();

        setupAdapter();

        mJotsDatabaseSpecificUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Intent intentToWidget = new Intent(ACTION_DATA_UPDATED);
                context.sendBroadcast(intentToWidget);

                populateLocalList(dataSnapshot);

                mDataUpdate.UIUpdate();

                if (mDualPane) {

                    setupAdapter();
                }

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }

        try {
            mEditTitle = (OnToolbarTitleTextEdit) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnToolbarTitleTextEdit"); //TODO make string
        }

        try {
            mOnClickListener = (OnItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnItemClick");    //TODO make string
        }

        try {
            mFABHide = (OnFABHide) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnFABHide");  //TODO make string
        }

        try {
            mDataUpdate = (OnDataUpdate) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnDataUpdate");  //TODO make string
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void UpdateUIList() {

        mFABHide.EnterHideFABList();

        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerView.getAdapter().getItemCount() == 0) {

            recyclerIsEmpty = true;
            HideRecycler();

        } else {

            recyclerIsEmpty = false;
            ShowRecycler();

            if (mDualPane) {

                clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, mAdapter.getItemCount() - 1);

                if (clickedPosition > recyclerView.getAdapter().getItemCount() - 1) {
                    clickedPosition = recyclerView.getAdapter().getItemCount() - 1;
                }

                try {
                    recyclerView.smoothScrollToPosition(clickedPosition);
                } catch (Exception e) {
//                    clickedPosition = recyclerView.getAdapter().getItemCount() - 1;
                    Log.v("UPDATE ERROR", e.toString());
                }

                if (!jotsData.isEmpty()) {
                    publicOnClick(clickedPosition);
                }
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition);
        editor.putBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, recyclerIsEmpty);
        editor.apply();
    }

    public void populateLocalList(DataSnapshot dataSnapshot) {

        originalJotList = new ArrayList<>();

        jotsData = new ArrayList<>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Jot jot = new Jot();

//            TODO DON'T CHANGE, WORKS TO POPULATE ARRAYLIST - START

            jot.setTitle(ds.getValue(Jot.class).getTitle());
            jot.setTagOne(ds.getValue(Jot.class).getTagOne());
            jot.setTagTwo(ds.getValue(Jot.class).getTagTwo());
            jot.setTagThree(ds.getValue(Jot.class).getTagThree());
            jot.setMessage(ds.getValue(Jot.class).getMessage());
            jot.setUniqueID(ds.getValue(Jot.class).getUniqueID());

            jotsData.add(jot);
//            TODO DON'T CHANGE, WORKS TO POPULATE ARRAYLIST - END - 10 LINES
            originalJotList.add(jot);
        }
    }

    public void setupAdapter() {

        mAdapter = new JotAdapter(Jot.class, R.layout.list_item, JotViewHolder.class, mJotsDatabaseSpecificUserReference, ListFragment.this);

        recyclerView.setAdapter(mAdapter);
    }

    public void setupFilterAdapter() {

        mFilteredAdapter = new FilteredAdapter(context, jotsData, ListFragment.this);

        recyclerView.setAdapter(mFilteredAdapter);
    }

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

    public void addJot(String title, String tagOne, String tagTwo, String tagThree, String message) {

        //TODO works to add new Jot - start

        String id = mJotsDatabaseReference.push().getKey();

        Jot jotToSave = new Jot(title, tagOne, tagTwo, tagThree, message, id);

        jotsData.add(jotToSave);

        mJotsDatabaseSpecificUserReference.child(id).setValue(jotToSave);

        clickedPosition = jotsData.size();

        recyclerIsEmpty = false;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition);
        editor.putBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, recyclerIsEmpty);
        editor.apply();
        //TODO works to add new Jot - end
    }

    public void editJot(String title, String tagOne, String tagTwo, String tagThree, String message) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

        String uniqueID = jotsData.get(clickedPosition).getUniqueID();

        Jot jot = new Jot();

        jot.setTitle(title);
        jot.setTagOne(tagOne);
        jot.setTagTwo(tagTwo);
        jot.setTagThree(tagThree);
        jot.setMessage(message);
        jot.setUniqueID(uniqueID);

        mJotsDatabaseSpecificUserReference.child(uniqueID).setValue(jot);
    }

    public void deleteJot(int position) {

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

        if (position < clickedPosition) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SHARED_PREFS_CLICKED_POSITION_KEY, clickedPosition - 1);
            editor.putBoolean(SHARED_PREFS_AUTO_SELECT_KEY, false);
            editor.apply();
        }

        clickedPosition = sharedPreferences.getInt(SHARED_PREFS_CLICKED_POSITION_KEY, jotsData.size() - 1);

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

    @Override
    public void publicOnClick(int position) {

        if (mDualPane) {

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
