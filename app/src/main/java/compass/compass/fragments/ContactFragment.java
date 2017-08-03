package compass.compass.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import compass.compass.R;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;


public class ContactFragment extends DialogFragment {

    TextView tvNameContact;
    CircleImageView ivProfileImageMain;
    TextView tvCallContact;
    User user;
    TextView tvMessageContact;


    public ContactFragment() {
        // Required empty public constructor
    }


    public static ContactFragment newInstance(String name) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            user = allContacts.get(name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNameContact = view.findViewById(R.id.tvNameContact);
        tvCallContact = view.findViewById(R.id.tvCallContact);
        ivProfileImageMain = view.findViewById(R.id.ivProfileImageMain);
        tvMessageContact = view.findViewById(R.id.tvMessageContact);

        ivProfileImageMain.setImageResource(getResources().getIdentifier(user.userId.replaceAll(" ",""), "drawable", getActivity().getPackageName()));

        tvCallContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + user.phoneNumber));
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("YIKES", "lol u don't have permission to call");
                    return;
                }
                getActivity().getApplicationContext().startActivity(callIntent);
            }
        });

        tvMessageContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance("", user.phoneNumber);
                message911MenuItemFragment.show(fm, "TAG");
            }
        });

        tvNameContact.setText(user.name);
    }
}
