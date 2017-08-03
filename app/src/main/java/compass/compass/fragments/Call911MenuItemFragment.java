package compass.compass.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import compass.compass.R;
import compass.compass.SwipeButton;
import compass.compass.SwipeButtonCustomItems;

/**
 * Created by icamargo on 8/1/17.
 */

public class Call911MenuItemFragment extends DialogFragment {
    public static final int CALL_ACTIVITY_CODE = 0;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private SwipeButton swipeButton;

    public Call911MenuItemFragment() {
        // Required empty public constructor
    }


    public static Call911MenuItemFragment newInstance() {
        Call911MenuItemFragment fragment = new Call911MenuItemFragment();
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
        return inflater.inflate(R.layout.fragment_call_911, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeButton = (SwipeButton) view.findViewById(R.id.call911Swipe);
        swipeButton.requestFocus();

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
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7325168820"));


        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("YIKES", "lol u dont have permission to call");
            return;
        }

        startActivityForResult(callIntent, CALL_ACTIVITY_CODE);
    }
}
