package compass.compass.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import compass.compass.NeedHelpActivity;
import compass.compass.R;
import compass.compass.SwipeButton;
import compass.compass.SwipeButtonCustomItems;

public class NeedHelpSwipe extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private SwipeButton swipeButton;

    public NeedHelpSwipe() {
        // Required empty public constructor
    }


    public static NeedHelpSwipe newInstance() {
        NeedHelpSwipe fragment = new NeedHelpSwipe();
        Bundle args = new Bundle();
        args.putString("title", "hi");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_need_help_swipe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeButton = (SwipeButton) view.findViewById(R.id.swipeHelp);
        swipeButton.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                dismiss();
                onSwiped();
            }
        };

        if (swipeButton != null) {
            swipeButton.setSwipeButtonCustomItems(swipeButtonSettings);
        }

    }

    public void onSwiped(){
        Intent i = new Intent(getActivity(), NeedHelpActivity.class);
        startActivity(i);
    }

}
