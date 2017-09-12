package com.josephcostlow.jotme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
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
public class EditFragment extends Fragment implements TextWatcher {
    private static final String TOOLBAR_TITLE_KEY = MainActivity.TOOLBAR_TITLE;
    private static final String TITLE_KEY = MainActivity.TITLE_KEY;
    private static final String TAG_ONE_KEY = MainActivity.TAG_ONE_KEY;
    private static final String TAG_TWO_KEY = MainActivity.TAG_TWO_KEY;
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private static final String TAG_THREE_KEY = MainActivity.TAG_THREE_KEY;
    private static final String MESSAGE_KEY = MainActivity.MESSAGE_KEY;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    TextView labelTitle, labelTags, labelMessage;
    EditText editTitle, editTagOne, editTagTwo, editTagThree, editMessage;
    OnToolbarTitleTextEdit mEditTitle;
    OnFABHide mFABHide;
    private OnFragmentInteractionListener mListener;
    private String toolbarTitle, title, tagOne, tagTwo, tagThree, message;
    private String restoredTitle, restoredTagOne, restoredTagTwo, restoredTagThree, restoredMessage;

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param /param1 Parameter 1.
     * @param /param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String toolbarTitle, String title, String tagOne, String tagTwo, String tagThree, String message) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();

        args.putString(TOOLBAR_TITLE_KEY, toolbarTitle);
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

            toolbarTitle = getArguments().getString(TOOLBAR_TITLE_KEY);
            title = getArguments().getString(TITLE_KEY);
            tagOne = getArguments().getString(TAG_ONE_KEY);
            tagTwo = getArguments().getString(TAG_TWO_KEY);
            tagThree = getArguments().getString(TAG_THREE_KEY);
            message = getArguments().getString(MESSAGE_KEY);
        }

        setRetainInstance(true);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_signout).setVisible(false);

        super.onPrepareOptionsMenu(menu);
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

        editTitle.addTextChangedListener(this);
        editTagOne.addTextChangedListener(this);
        editTagTwo.addTextChangedListener(this);
        editTagThree.addTextChangedListener(this);
        editMessage.addTextChangedListener(this);

        editTitle.setSelectAllOnFocus(true);
        editTagOne.setSelectAllOnFocus(true);
        editTagTwo.setSelectAllOnFocus(true);
        editTagThree.setSelectAllOnFocus(true);
        editMessage.setSelectAllOnFocus(true);

        mFABHide.EnterHideFABEdit();

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(TOOLBAR_TITLE_KEY)) {
                toolbarTitle = savedInstanceState.getString(TOOLBAR_TITLE_KEY);
            }

            if (savedInstanceState.containsKey(TITLE_KEY)) {
                restoredTitle = savedInstanceState.getString(TITLE_KEY);
            }

            if (savedInstanceState.containsKey(TAG_ONE_KEY)) {
                restoredTagOne = savedInstanceState.getString(TAG_ONE_KEY);
            }

            if (savedInstanceState.containsKey(TAG_TWO_KEY)) {
                restoredTagTwo = savedInstanceState.getString(TAG_TWO_KEY);
            }

            if (savedInstanceState.containsKey(TAG_THREE_KEY)) {
                restoredTagThree = savedInstanceState.getString(TAG_THREE_KEY);
            }

            if (savedInstanceState.containsKey(MESSAGE_KEY)) {
                restoredMessage = savedInstanceState.getString(MESSAGE_KEY);
            }

            setEditText(restoredTitle, restoredTagOne, restoredTagTwo, restoredTagThree, restoredMessage);

        } else {

            setEditText(title, tagOne, tagTwo, tagThree, message);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void setEditText(String titleSent, String tagOneSent, String tagTwoSent, String tagThreeSent, String messageSent) {

        editTitle.setText(titleSent);
        editTagOne.setText(tagOneSent);
        editTagTwo.setText(tagTwoSent);
        editTagThree.setText(tagThreeSent);
        editMessage.setText(messageSent);

        mEditTitle.EditToolbarText(toolbarTitle);

        setHints();
    }

    private void setHints() {

        if (editTitle.getText().length() == 0) {
            editTitle.setHint(R.string.empty_title_edit);
        }

        if (editTagOne.getText().length() == 0) {
            editTagOne.setHint(R.string.empty_tag_edit);
        }

        if (editTagTwo.getText().length() == 0) {
            editTagTwo.setHint(R.string.empty_tag_edit);
        }

        if (editTagThree.getText().length() == 0) {
            editTagThree.setHint(R.string.empty_tag_edit);
        }

        if (editMessage.getText().length() == 0) {
            editMessage.setHint(R.string.empty_message_edit);
        }
    }

    public String[] dataToSave() {

        String[] jotToSave = new String[5];

        String titleToSave = editTitle.getText().toString();
        String tagOneToSave = editTagOne.getText().toString();
        String tagTwoToSave = editTagTwo.getText().toString();
        String tagThreeToSave = editTagThree.getText().toString();
        String messageToSave = editMessage.getText().toString();

        jotToSave[0] = titleToSave;
        jotToSave[1] = tagOneToSave;
        jotToSave[2] = tagTwoToSave;
        jotToSave[3] = tagThreeToSave;
        jotToSave[4] = messageToSave;

        return jotToSave;
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

        mFABHide.ExitHideFABEdit();

        editTitle.removeTextChangedListener(this);
        editTagOne.removeTextChangedListener(this);
        editTagTwo.removeTextChangedListener(this);
        editTagThree.removeTextChangedListener(this);
        editMessage.removeTextChangedListener(this);

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

        outState.putString(TOOLBAR_TITLE_KEY, toolbarTitle);

        outState.putString(TITLE_KEY, editTitle.getText().toString());
        outState.putString(TAG_ONE_KEY, editTagOne.getText().toString());
        outState.putString(TAG_TWO_KEY, editTagTwo.getText().toString());
        outState.putString(TAG_THREE_KEY, editTagThree.getText().toString());
        outState.putString(MESSAGE_KEY, editMessage.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        editTitle.setSelectAllOnFocus(true);
        editTagOne.setSelectAllOnFocus(true);
        editTagTwo.setSelectAllOnFocus(true);
        editTagThree.setSelectAllOnFocus(true);
        editMessage.setSelectAllOnFocus(true);

        setHints();
        mFABHide.HideSaveFABEdit();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (editTagOne.getText().toString().isEmpty()) {

            editTagTwo.setEnabled(false);
            editTagThree.setEnabled(false);

        } else {

            if (editTagOne.getText().toString().equals(getResources().getString(R.string.empty_tag_edit))) {

                editTagTwo.setEnabled(false);
                mFABHide.HideSaveFABEdit();

            } else {

                editTagTwo.setEnabled(true);
                editTagThree.setEnabled(false);
                mFABHide.ShowSaveFABEdit();
            }
        }

        if (editTagTwo.getText().toString().isEmpty()) {

            editTagThree.setEnabled(false);

        } else {

            if (editTagTwo.getText().toString().equals(getResources().getString(R.string.empty_tag_edit))) {

                editTagThree.setEnabled(false);

            } else {

                editTagThree.setEnabled(true);
            }
        }

        if (editTitle.getText().toString().equals(title)) {

            if (editTagOne.getText().toString().equals(tagOne)) {

                if (editTagTwo.getText().toString().equals(tagTwo)) {

                    if (editTagThree.getText().toString().equals(tagThree)) {

                        if (editMessage.getText().toString().equals(message)) {
                            mFABHide.HideSaveFABEdit();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (editTitle.getText().toString().isEmpty()) {

            editTitle.setText(getResources().getString(R.string.empty_title_edit));
            editTitle.selectAll();
        }

        if (editTagOne.getText().toString().isEmpty()) {

            editTagOne.setText(getResources().getString(R.string.empty_tag_edit));
            editTagOne.selectAll();

            if (!editTagTwo.getText().toString().isEmpty()) {

                editTagOne.setText(editTagTwo.getText().toString());
                editTagTwo.getText().clear();
                editTagOne.selectAll();
            }
        }

        if (editTagTwo.getText().toString().isEmpty()) {

            editTagTwo.setText(getResources().getString(R.string.empty_tag_edit));
            editTagTwo.selectAll();

            if (!editTagThree.getText().toString().isEmpty()) {

                editTagTwo.setText(editTagThree.getText().toString());
                editTagThree.getText().clear();
                editTagTwo.selectAll();
            }
        }

        if (editTagThree.getText().toString().isEmpty()) {

            editTagThree.setText(getResources().getString(R.string.empty_tag_edit));
            editTagThree.selectAll();
        }

        if (editMessage.getText().toString().isEmpty()) {

            editMessage.setText(getResources().getString(R.string.empty_message_edit));
            editMessage.selectAll();
        }
    }

    public interface OnFABHide {
        void EnterHideFABEdit();

        void ExitHideFABEdit();

        void HideSaveFABEdit();

        void ShowSaveFABEdit();
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
