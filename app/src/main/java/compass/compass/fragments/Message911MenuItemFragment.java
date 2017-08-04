package compass.compass.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import compass.compass.NeedHelpActivity;
import compass.compass.R;
import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.MainActivity.peopleInEvents;


public class Message911MenuItemFragment extends DialogFragment {
    //private NotifyFriendsMessageListener mListener;
    public static String message = "";
    public EditText etMessage911;
    public TextView tvMessage911;
    TextView tvContinueMessage911, tvCancelMessage911;

    static Message911FragmentListener myListener;

    public Message911MenuItemFragment() {
        // Required empty public constructor
    }

    public static Message911MenuItemFragment newInstance() {
        return new Message911MenuItemFragment();
    }

    public static Message911MenuItemFragment newInstance(String messageInfo) {
        message = messageInfo;
        return new Message911MenuItemFragment();
    }

    public static Message911MenuItemFragment newInstance(String messageInfo, Message911FragmentListener listener) {
        message = messageInfo;
        myListener = listener;
        return new Message911MenuItemFragment();
    }

    public static Message911MenuItemFragment newInstance(Message911FragmentListener listener) {
        myListener = listener;
        return new Message911MenuItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etMessage911 = (EditText) view.findViewById(R.id.etMessage);
        tvContinueMessage911 = (TextView) view.findViewById(R.id.tvContinueMessage911);
        tvCancelMessage911 = (TextView) view.findViewById(R.id.tvCancelMessage911);
        tvMessage911 = (TextView) view.findViewById(R.id.tvMessage);

        if(message.length() == 0){
            message = "Hello my name is " + changeStringCase(currentProfile.name) + " I am in need of help!! My current location is (" + currentProfile.latitude + ", " + currentProfile.longitude + "). ";
        }


        etMessage911.setText(message);
        etMessage911.setSelection(etMessage911.getText().toString().length());

        View.OnClickListener dismissAlert = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        View.OnClickListener sendMessage = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newMessage = etMessage911.getText().toString();
                try {
                    SmsManager.getDefault().sendTextMessage("4258924209", null, newMessage, null, null);
                } catch (Exception e) {
                    AlertDialog.Builder alertDialogBuilder = new
                            AlertDialog.Builder(getContext());
                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.setMessage(e.getMessage());
                    dialog.show();
                }
                myListener.launchNeedHelpFromMessage();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                String Alert_message = ("Please check in on " + currentProfile.name + "! They have messaged 911 and may need your help.");
                mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
                currentProfile.status = true;
                mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender("BOT");
                message.setTime((new Date().getTime()));

                NeedHelpActivity.sendNotificationToUser(peopleInEvents, message, mDatabase);

                dismiss();
            }
        };

        tvContinueMessage911.setOnClickListener(sendMessage);
        tvCancelMessage911.setOnClickListener(dismissAlert);

    }

    @NonNull
    public static String changeStringCase(String s) {
        final String DELIMITERS = " '-/";
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_911, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof NotifyFriendsMessageListener) {
//            mListener = (NotifyFriendsMessageListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
//    public interface NotifyFriendsMessageListener {
//        void writeMessageToUsers(String messageInfo);
//    }

    public interface Message911FragmentListener{
        void launchNeedHelpFromMessage();
    }
}
