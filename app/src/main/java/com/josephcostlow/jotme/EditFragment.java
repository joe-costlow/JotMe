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
    private static final String TAG_THREE_KEY = MainActivity.TAG_THREE_KEY;
    private static final String MESSAGE_KEY = MainActivity.MESSAGE_KEY;
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
     * @param toolbarTitle current text of toolbar textview (Edit Jot or Add Jot
     * @param title title of current Jot
     * @param tagOne first tag of current Jot
     * @param tagTwo second tag of current Jot
     * @param tagThree third tag of current Jot
     * @param message message of current Jot
     * @return Fragment
     */
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

    /**
     * Hide the toolbar icons for the search feature and sign out feature.
     *
     * @param menu
     */
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

    /**
     * Set the hints for the edittexts.
     */
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

    /**
     * The text from the edittexts are put into a string array.
     *
     * @return string array to the onClick method in MainActivity for the Save FAB.
     */
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

//        When the first tag edittext is empty, the second and third tag edittexts are disabled.
        if (editTagOne.getText().toString().isEmpty()) {

            editTagTwo.setEnabled(false);
            editTagThree.setEnabled(false);

        } else {
//            If the first tag edittext is equal to the default value, the second tag edittext is
//            disabled and the Save FAB is hidden. If the first tag edittext is not empty or equal
//            to the default value, the second tag edittext is enabled and the Save FAB is visible.
            if (editTagOne.getText().toString().equals(getResources().getString(R.string.empty_tag_edit))) {

                editTagTwo.setEnabled(false);
                mFABHide.HideSaveFABEdit();

            } else {

                editTagTwo.setEnabled(true);
                editTagThree.setEnabled(false);
                mFABHide.ShowSaveFABEdit();
            }
        }

//        If the second tag edittext is empty, the third tag edittext is disabled.
        if (editTagTwo.getText().toString().isEmpty()) {

            editTagThree.setEnabled(false);

        } else {
//            If the second tag edittext is equal to the default value, the third tage edittext is
//            disabled.
            if (editTagTwo.getText().toString().equals(getResources().getString(R.string.empty_tag_edit))) {

                editTagThree.setEnabled(false);

            } else {

                editTagThree.setEnabled(true);
            }
        }

//        If all of the edittexts equal their original values, the Save FAB is hidden.
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

    /**
     * This method is used to set a value to each edittext. Also, it provides a way to have consecutive
     * tags. For example, if two tags are provided, the values will be set to the first and second tag
     * edittext.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

//        If the title edittext is empty, the default value is set to the edittext.
        if (editTitle.getText().toString().isEmpty()) {

            editTitle.setText(getResources().getString(R.string.empty_title_edit));
            editTitle.selectAll();
        }

//        If the first tag edittext is empty, its value is set to the default value.
        if (editTagOne.getText().toString().isEmpty()) {

            editTagOne.setText(getResources().getString(R.string.empty_tag_edit));
            editTagOne.selectAll();
//            If the second tag edittext value is not empty, its current value is set to the first tag
//            edittext and the second tag edittext value is cleared.
            if (!editTagTwo.getText().toString().isEmpty()) {

                editTagOne.setText(editTagTwo.getText().toString());
                editTagTwo.getText().clear();
                editTagOne.selectAll();
            }
        }

//        If the second tag edittext value is empty, its value is set to the default value.
        if (editTagTwo.getText().toString().isEmpty()) {

            editTagTwo.setText(getResources().getString(R.string.empty_tag_edit));
            editTagTwo.selectAll();
//            If the third tag edittext value is not empty, its current value is set to the second tag
//            edittext and the third tag edittext value is cleared.
            if (!editTagThree.getText().toString().isEmpty()) {

                editTagTwo.setText(editTagThree.getText().toString());
                editTagThree.getText().clear();
                editTagTwo.selectAll();
            }
        }

//        If the third tag edittext is empty, its value is set to the default value.
        if (editTagThree.getText().toString().isEmpty()) {

            editTagThree.setText(getResources().getString(R.string.empty_tag_edit));
            editTagThree.selectAll();
        }

//        If the message edittext is empty, its value is set to the default value.
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
