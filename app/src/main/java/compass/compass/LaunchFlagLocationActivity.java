package compass.compass;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class LaunchFlagLocationActivity extends DialogFragment {// Use this instance of the interface to deliver action events
    MyDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MyDialogListener so we can send events to the host
            mListener = (MyDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement MyDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("FLAG LOCATION UNSAFE");
        alertDialog.setMessage("Would you like to mark this location as unsafe?");
        alertDialog.setIcon(R.drawable.ic_error_outline_black_24px);

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "cancelled", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        };

        DialogInterface.OnClickListener continueListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onContinueClicked();
                alertDialog.dismiss();
            }
        };

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", cancelListener);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "CONTINUE", continueListener);
        alertDialog.show();

        return alertDialog;
    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog).create();
//        alertDialog.setTitle("FLAG LOCATION UNSAFE");
//        alertDialog.setMessage("Would you like to mark this location as unsafe?");
//        alertDialog.setIcon(R.drawable.ic_error_outline_black_24px);
//
//        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getContext(), "cancelled", Toast.LENGTH_LONG).show();
//                alertDialog.dismiss();
//            }
//        };
//
//        DialogInterface.OnClickListener continueListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                alertDialog.dismiss();
//
//                Intent intent = new Intent(getContext(), ChatActivity.class);
//                intent.putExtra("eventId", eventId);
//                intent.putExtra("fromHere", "eventActivity");
//                startActivity(intent);
//            }
//        };
//
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", cancelListener);
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "CONTINUE", continueListener);
//        alertDialog.show();

////        FragmentManager fm = getSupportFragmentManager();
////        FriendNeedsHelpFragment friendNeedsHelpFragment = FriendNeedsHelpFragment.newInstance(message);
////        friendNeedsHelpFragment.show(fm, "idk_what_goes_here");
//    }

    public interface MyDialogListener {
        public void onContinueClicked();
    }
}
