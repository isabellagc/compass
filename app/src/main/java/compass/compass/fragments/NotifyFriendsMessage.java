package compass.compass.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import compass.compass.R;


public class NotifyFriendsMessage extends DialogFragment {

    public CardView cvIntoxication, cvInjury, cvSexualAssault, cvOther;
    public TextView tvCancelMessageFriends, tvContinueMessageFriends;
    boolean injury, intoxication, sexualAssault, other;

    private NotifyFriendsMessageListener mListener;

    public NotifyFriendsMessage() {
        // Required empty public constructor
    }

    public static NotifyFriendsMessage newInstance() {
        return new NotifyFriendsMessage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injury = intoxication = sexualAssault = other = false;

        cvIntoxication = (CardView) view.findViewById(R.id.cvIntoxication);
        cvInjury = (CardView) view.findViewById(R.id.cvInjury);
        cvSexualAssault = (CardView) view.findViewById(R.id.cvSexualAssault);
        cvOther = (CardView) view.findViewById(R.id.cvOther);
        tvCancelMessageFriends = (TextView) view.findViewById(R.id.tvCancelMessageFriends);
        tvContinueMessageFriends = (TextView) view.findViewById(R.id.tvContinueMessageFriends);


        View.OnClickListener onClickCardListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.equals(cvIntoxication)){
                    intoxication = !intoxication;
                    recolorView(cvIntoxication, intoxication);
                }else if(view.equals(cvSexualAssault)){
                    sexualAssault = !sexualAssault;
                    recolorView(cvSexualAssault, sexualAssault);
                }else if(view.equals(cvOther)){
                    other = !other;
                    recolorView(cvOther, other);
                }else if(view.equals(cvInjury)){
                    injury = !injury;
                    recolorView(cvInjury, injury);
                }else{
                    //ERROR
                }
            }
        };

        cvIntoxication.setOnClickListener(onClickCardListener);
        cvInjury.setOnClickListener(onClickCardListener);
        cvSexualAssault.setOnClickListener(onClickCardListener);
        cvOther.setOnClickListener(onClickCardListener);

        tvContinueMessageFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something with highlighted items ie booleans that correspond
                dismiss();
            }
        });

        tvCancelMessageFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notify_friends, container, false);
    }

    private void recolorView(CardView v, boolean b){
        if(b){
            v.setCardBackgroundColor(Color.LTGRAY);
        }else{
            v.setCardBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotifyFriendsMessageListener) {
            mListener = (NotifyFriendsMessageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface NotifyFriendsMessageListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
