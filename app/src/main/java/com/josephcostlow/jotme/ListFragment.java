package com.josephcostlow.jotme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment implements JotAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    OnItemClick mOnClickListener;

    ArrayList<Jot> jotsData;
    Context context;
    JotAdapter mAdapter;

    private String AUTO_SELECTOR_KEY = "autoSelector";
    public static boolean autoSelector;
    public static int clickedPosition;
    private String CLICKED_POSITION = "clickedPosition";

    RecyclerView recyclerView;

    public ListFragment() {
        // Required empty public constructor
    }

    public interface OnItemClick {
        void OnListItemClick(String title, String tagOne, String tagTwo, String tagThree, String message);
//        void OnListItemClick(int position);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);

        context = getContext();

//        start of mock data collection     TODO collect real data
        jotsData = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {

            Jot jot = new Jot();
            jot.setTitle("Title " + i);
            jot.setTagOne("Tag One");
            jot.setTagTwo("Tag Two");
            jot.setTagThree("Tag Three");
            jot.setMessage("This is a sample message " + i);

            jotsData.add(jot);

        }
//        end of mock data collection

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new JotAdapter(context, jotsData);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(AUTO_SELECTOR_KEY)) {
            autoSelector = savedInstanceState.getBoolean(AUTO_SELECTOR_KEY);
        } else {
            autoSelector = true;
        }

        // Inflate the layout for this fragment
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
            mOnClickListener = (OnItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnItemClick");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mOnClickListener = null;
    }

    @Override
    public void onClick(View view, int position) {

        autoSelector = false;
        clickedPosition = position;

        String title = jotsData.get(position).getTitle();
        String tagOne = jotsData.get(position).getTagOne();
        String tagTwo = jotsData.get(position).getTagTwo();
        String tagThree = jotsData.get(position).getTagThree();
        String message = jotsData.get(position).getMessage();
        mOnClickListener.OnListItemClick(title, tagOne, tagTwo, tagThree, message);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTO_SELECTOR_KEY, autoSelector);
    }

    @Override
    public void onPause() {
        super.onPause();
        mOnClickListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
