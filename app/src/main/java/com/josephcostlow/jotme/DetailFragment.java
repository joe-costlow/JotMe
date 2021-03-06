package com.josephcostlow.jotme;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    private static final String TITLE_KEY = MainActivity.TITLE_KEY;
    private static final String TAG_ONE_KEY = MainActivity.TAG_ONE_KEY;
    private static final String TAG_TWO_KEY = MainActivity.TAG_TWO_KEY;
    private static final String TAG_THREE_KEY = MainActivity.TAG_THREE_KEY;
    private static final String MESSAGE_KEY = MainActivity.MESSAGE_KEY;
    TextView labelTitle, labelTags, labelMessage;
    TextView textTitle, textTagOne, textTagTwo, textTagThree, textMessage;
    ScrollView messageSV;
    CardView emptyRecyclerCard;
    TextView emptyView;
    SharedPreferences sharedPreferences;
    OnToolbarTitleTextEdit mEditTitle;
    OnFABHide mFABHide;
    private String SHARED_PREFS_FILENAME = MainActivity.SHARED_PREFS_FILENAME;
    private String SHARED_PREFS_EMPTY_RECYCLER_KEY = MainActivity.SHARED_PREFS_EMPTY_RECYCLER_KEY;
    private OnFragmentInteractionListener mListener;
    private String title, tagOne, tagTwo, tagThree, message;
    private boolean recyclerIsEmpty;
    private boolean mDualPane;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * @param title title of the current Jot
     * @param tagOne first tag of the current Jot
     * @param tagTwo second tag of the current Jot
     * @param tagThree third tag of the current Jot
     * @param message message of the current Jot
     * @return Fragment
     */
    public static DetailFragment newInstance(String title, String tagOne, String tagTwo, String tagThree, String message) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();

        args.putString(TITLE_KEY, title);
        args.putString(TAG_ONE_KEY, tagOne);
        args.putString(TAG_TWO_KEY, tagTwo);
        args.putString(TAG_THREE_KEY, tagThree);
        args.putString(MESSAGE_KEY, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            title = getArguments().getString(TITLE_KEY);
            tagOne = getArguments().getString(TAG_ONE_KEY);
            tagTwo = getArguments().getString(TAG_TWO_KEY);
            tagThree = getArguments().getString(TAG_THREE_KEY);
            message = getArguments().getString(MESSAGE_KEY);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDualPane = getResources().getBoolean(R.bool.dual_pane);

        labelTitle = (TextView) rootView.findViewById(R.id.detail_label_title);
        labelTags = (TextView) rootView.findViewById(R.id.detail_label_tag);
        labelMessage = (TextView) rootView.findViewById(R.id.detail_label_message);

        textTitle = (TextView) rootView.findViewById(R.id.text_title);
        textTagOne = (TextView) rootView.findViewById(R.id.text_tag_one);
        textTagTwo = (TextView) rootView.findViewById(R.id.text_tag_two);
        textTagThree = (TextView) rootView.findViewById(R.id.text_tag_three);
        textMessage = (TextView) rootView.findViewById(R.id.message_tv);
        messageSV = (ScrollView) rootView.findViewById(R.id.message_sv);

        emptyRecyclerCard = (CardView) rootView.findViewById(R.id.empty_recycler_detail_card);
        emptyView = (TextView) rootView.findViewById(R.id.empty_recycler_detail_textview);

        UpdateUIDetail();

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(TITLE_KEY)) {
                title = savedInstanceState.getString(TITLE_KEY);
            }

            if (savedInstanceState.containsKey(TAG_ONE_KEY)) {
                tagOne = savedInstanceState.getString(TAG_ONE_KEY);
            }

            if (savedInstanceState.containsKey(TAG_TWO_KEY)) {
                tagTwo = savedInstanceState.getString(TAG_TWO_KEY);
            }

            if (savedInstanceState.containsKey(TAG_THREE_KEY)) {
                tagThree = savedInstanceState.getString(TAG_THREE_KEY);
            }

            if (savedInstanceState.containsKey(MESSAGE_KEY)) {
                message = savedInstanceState.getString(MESSAGE_KEY);
            }
        }

        setText(title, tagOne, tagTwo, tagThree, message);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {

        mFABHide.ExitHideFABDetail();

        super.onPause();
    }

    public void setText(String title, String tagOne, String tagTwo, String tagThree, String message) {
        textTitle.setText(title);
        textTagOne.setText(tagOne);
        textTagTwo.setText(tagTwo);
        textTagThree.setText(tagThree);
        textMessage.setText(message);
    }

    /**
     * This method is executed upon refresh of data to Firebase Database.
     * <p>
     * If the recyclerview in the ListFragment instance is empty, a message is shown indicating this.
     * If in dual pane, the app name remains as the text in the textview of the toolbar. In single
     * pane, the textview text is changed to 'Detail'.
     */
    public void UpdateUIDetail() {

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty || tagOne == null) {
            ShowEmptyView();
        } else {
            ShowTextViews();
        }

        if (!mDualPane) {
            mEditTitle.EditToolbarText(getResources().getString(R.string.main_toolbar_title_detail));
        } else {
            mEditTitle.EditToolbarText(getResources().getString(R.string.app_name));
        }

        mFABHide.EnterHideFABDetail();
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
            mFABHide = (OnFABHide) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnFABHide");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void ShowTextViews() {
        labelTitle.setVisibility(View.VISIBLE);
        labelTags.setVisibility(View.VISIBLE);
        labelTitle.setVisibility(View.VISIBLE);
        labelMessage.setVisibility(View.VISIBLE);
        textTagOne.setVisibility(View.VISIBLE);
        textTagTwo.setVisibility(View.VISIBLE);
        textTagThree.setVisibility(View.VISIBLE);
        textMessage.setVisibility(View.VISIBLE);
        emptyRecyclerCard.setVisibility(View.GONE);
    }

    private void ShowEmptyView() {
        labelTitle.setVisibility(View.GONE);
        labelTags.setVisibility(View.GONE);
        labelTitle.setVisibility(View.GONE);
        labelMessage.setVisibility(View.GONE);
        textTagOne.setVisibility(View.GONE);
        textTagTwo.setVisibility(View.GONE);
        textTagThree.setVisibility(View.GONE);
        textMessage.setVisibility(View.GONE);
        emptyRecyclerCard.setVisibility(View.VISIBLE);

        SetEmptyRecyclerText();
    }

    private void SetEmptyRecyclerText() {
        emptyView.setText(R.string.empty_recycler_detail_text);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * This method is executed during the onClick method of MainActivity for the Edit FAB.
     *
     * @return a string array to the onClick method in MainActivity for the Edit FAB. The array is
     * the data of the currently selected Jot being displayed.
     */
    public String[] DataForEdit() {

        String[] currentJot = new String[5];

        currentJot[0] = title;
        currentJot[1] = tagOne;
        currentJot[2] = tagTwo;
        currentJot[3] = tagThree;
        currentJot[4] = message;

        return currentJot;
    }

    /**
     * Interface is implemented upon creation of a new instance of DetailFragment and when exiting.
     * This interface is used to control the visibility of the proper FABs to be displayed.
     */
    public interface OnFABHide {
        void EnterHideFABDetail();
        void ExitHideFABDetail();
    }

    /**
     * Interface is implemented to edit the text of the textview of the toolbar to reflect that the
     * user is showing the details of a selected Jot.
     */
    public interface OnToolbarTitleTextEdit {
        void EditToolbarText(String title);
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
