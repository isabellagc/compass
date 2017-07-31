package compass.compass.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import compass.compass.R;

/**
 * Created by icamargo on 7/31/17.
 */

public class MarkUnsafeFragment extends DialogFragment{
    public TextView tvContinue, tvCancel;
    public ImageView ivContinue, ivCancel;
    boolean send = false;

    public MarkUnsafeFragment(){

    }

    public static MarkUnsafeFragment newInstance(){
        MarkUnsafeFragment fragment = new MarkUnsafeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mark_location_unsafe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCancel = view.findViewById(R.id.ivCancel);
        ivContinue = view.findViewById(R.id.ivContinue);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvContinue = view.findViewById(R.id.tvContinue);

        View.OnClickListener continueListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = true;
                sendResult(ChatHomeFragment.REQUEST_CODE);
            }
        };

        View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = false;
                dismiss();
            }
        };

        ivCancel.setOnClickListener(cancelListener);
        tvCancel.setOnClickListener(cancelListener);

        ivContinue.setOnClickListener(continueListener);
        tvContinue.setOnClickListener(continueListener);
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.putExtra("boolSend", send);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }
}
