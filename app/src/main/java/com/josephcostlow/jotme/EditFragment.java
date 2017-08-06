package com.josephcostlow.jotme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView labelTitle, labelTags, labelMessage;
    EditText editTitle, editTagOne, editTagTwo, editTagThree, editMessage;
    Bundle bundle;
    OnToolbarTitleTextEdit mEditTitle;
    OnFABHide mFABHide;
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

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        labelTitle = (TextView) rootView.findViewById(R.id.edit_label_title);
        labelTags = (TextView) rootView.findViewById(R.id.edit_label_tag);
        labelMessage = (TextView) rootView.findViewById(R.id.edit_label_message);

        editTitle = (EditText) rootView.findViewById(R.id.edit_edit_title);
        editTagOne = (EditText) rootView.findViewById(R.id.edit_edit_tag_one);
        editTagTwo = (EditText) rootView.findViewById(R.id.edit_edit_tag_two);
        editTagThree = (EditText) rootView.findViewById(R.id.edit_edit_tag_three);
        editMessage = (EditText) rootView.findViewById(R.id.edit_edit_message);

        mFABHide.EnterHideFABEdit();

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

            setEditText(title, tagOne, tagTwo, tagThree, message);

        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void setEditText(String title, String tagOne, String tagTwo, String tagThree, String message) {
        editTitle.setText(title);
        editTagOne.setText(tagOne);
        editTagTwo.setText(tagTwo);
        editTagThree.setText(tagThree);
        editMessage.setText(message);

        if (!title.isEmpty()) {
            mEditTitle.EditToolbarText(getResources().getString(R.string.main_toolbar_title_edit));
        } else {
            mEditTitle.EditToolbarText(getResources().getString(R.string.main_toolbar_title_add));
        }
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
            throw new ClassCastException(context.toString() + "must implement OnToolbarTitleTextEdit");
        }

        try {
            mFABHide = (OnFABHide) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnFABHide");
        }
    }

    @Override
    public void onPause() {

        mEditTitle.EditToolbarText(getResources().getString(R.string.app_name));
        mFABHide.ExitHideFABEdit();

        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TITLE_KEY, editTitle.getText().toString());
        outState.putString(TAG_ONE_KEY, editTagOne.getText().toString());
        outState.putString(TAG_TWO_KEY, editTagTwo.getText().toString());
        outState.putString(TAG_THREE_KEY, editTagThree.getText().toString());
        outState.putString(MESSAGE_KEY, editMessage.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(TITLE_KEY);
            tagOne = bundle.getString(TAG_ONE_KEY);
            tagTwo = bundle.getString(TAG_TWO_KEY);
            tagThree = bundle.getString(TAG_THREE_KEY);
            message = bundle.getString(MESSAGE_KEY);

            setEditText(title, tagOne, tagTwo, tagThree, message);
        }
    }

    public interface OnFABHide {
        void EnterHideFABEdit();

        void ExitHideFABEdit();
    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
