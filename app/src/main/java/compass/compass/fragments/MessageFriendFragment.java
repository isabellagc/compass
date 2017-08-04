package compass.compass.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import compass.compass.R;

import static compass.compass.fragments.Message911MenuItemFragment.message;


public class MessageFriendFragment extends DialogFragment {

    public EditText etMessage;
    public static String number;
    public static String friendName;
    public TextView tvMessage;
    TextView tvContinueMessage, tvCancelMessage;

    public MessageFriendFragment() {
        // Required empty public constructor
    }

    public static MessageFriendFragment newInstance(String contactNumber, String contactName) {
        number = contactNumber;
        friendName = contactName;
        return new MessageFriendFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        tvContinueMessage = (TextView) view.findViewById(R.id.tvContinueMessage);
        tvCancelMessage = (TextView) view.findViewById(R.id.tvCancelMessage);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        tvMessage.setText("MESSAGE " + friendName.toUpperCase());

        etMessage.setText(message);
        etMessage.setSelection(etMessage.getText().toString().length());

        View.OnClickListener dismissAlert = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        View.OnClickListener sendMessage = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newMessage = etMessage.getText().toString();
                try {
                    SmsManager.getDefault().sendTextMessage(number, null, newMessage, null, null);
                } catch (Exception e) {
                    AlertDialog.Builder alertDialogBuilder = new
                            AlertDialog.Builder(getContext());
                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.setMessage(e.getMessage());
                    dialog.show();
                }

                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog).create();
                alertDialog.setMessage("Your message has been sent");
                alertDialog.setIcon(R.drawable.ic_need_help);

                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                };

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", okListener);
                alertDialog.show();

                dismiss();
            }
        };

        tvContinueMessage.setOnClickListener(sendMessage);
        tvCancelMessage.setOnClickListener(dismissAlert);

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
        return inflater.inflate(R.layout.fragment_message_friend, container, false);
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
