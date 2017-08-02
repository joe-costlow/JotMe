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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String TITLE_KEY = MainActivity.TITLE_KEY;
    private String TAG_ONE_KEY = MainActivity.TAG_ONE_KEY;
    private String TAG_TWO_KEY = MainActivity.TAG_TWO_KEY;
    private String TAG_THREE_KEY = MainActivity.TAG_THREE_KEY;
    private String MESSAGE_KEY = MainActivity.MESSAGE_KEY;

    private String title, tagOne, tagTwo, tagThree, message;

    TextView labelTitle, labelTags, labelMessage;
    TextView textTitle, textTagOne, textTagTwo, textTagThree, textMessage;
    ScrollView messageSV;

    CardView emptyRecyclerCard;
    TextView emptyView;

    private boolean recyclerIsEmpty = ListFragment.recyclerIsEmpty;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_FILENAME = "com.josephcostlow.jotme.shared";
    private static final String SHARED_PREFS_EMPTY_RECYCLER_KEY = "emptyRecycler";

    Bundle bundle;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

        ShowTextViews();

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS_FILENAME, 0);
        recyclerIsEmpty = sharedPreferences.getBoolean(SHARED_PREFS_EMPTY_RECYCLER_KEY, true);

        if (recyclerIsEmpty) {
            ShowEmptyView();
        }

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

            setText(title, tagOne, tagTwo, tagThree, message);

        } else {

//            bundle = getArguments();
            if (bundle != null) {
//            TODO implement bundle when interface is made
//                title = bundle.getString(TITLE_KEY);
//                tagOne = bundle.getString(TAG_ONE_KEY);
//                tagTwo = bundle.getString(TAG_TWO_KEY);
//                tagThree = bundle.getString(TAG_THREE_KEY);
//                message = bundle.getString(MESSAGE_KEY);

//            ShowEmptyView();
//                ShowTextViews();

            }
// else {
//
//                ShowEmptyView();
//            }
        }

//        setText(title, tagOne, tagTwo, tagThree, message);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void setText(String title, String tagOne, String tagTwo, String tagThree, String message) {
        textTitle.setText(title);
        textTagOne.setText(tagOne);
        textTagTwo.setText(tagTwo);
        textTagThree.setText(tagThree);
        textMessage.setText(message);

//        ShowTextViews();
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

        outState.putString(TITLE_KEY, textTitle.getText().toString());
        outState.putString(TAG_ONE_KEY, textTagOne.getText().toString());
        outState.putString(TAG_TWO_KEY, textTagTwo.getText().toString());
        outState.putString(TAG_THREE_KEY, textTagThree.getText().toString());
        outState.putString(MESSAGE_KEY, textMessage.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bundle = getArguments();
        if (bundle != null) {
//            TODO implement bundle when interface is made
            title = bundle.getString(TITLE_KEY);
            tagOne = bundle.getString(TAG_ONE_KEY);
            tagTwo = bundle.getString(TAG_TWO_KEY);
            tagThree = bundle.getString(TAG_THREE_KEY);
            message = bundle.getString(MESSAGE_KEY);

            setText(title, tagOne, tagTwo, tagThree, message);
//
//            int position = bundle.getInt(BUNDLE_POSITION, 0);
//            setText(position);
        }
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
