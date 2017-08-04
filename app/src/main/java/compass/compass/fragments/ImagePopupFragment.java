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
import android.widget.TextView;
import android.widget.Toast;

import compass.compass.R;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;


/**
 * Created by brucegatete on 8/3/17.
 */

public class ImagePopupFragment extends DialogFragment{
    TextView tvNameContact1;
    CircleImageView ivProfileImageMain1;
    TextView tvCallContact1;
    TextView tvLocation;
    User user;


    public ImagePopupFragment() {
        // Required empty public constructor
    }


    public static ImagePopupFragment newInstance(String name) {
        ImagePopupFragment fragment = new ImagePopupFragment();
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
        return inflater.inflate(R.layout.fragment_image_popup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNameContact1 = view.findViewById(R.id.tvNameContact1);
        tvCallContact1 = view.findViewById(R.id.tvCallContact1);
        ivProfileImageMain1 = view.findViewById(R.id.ivProfileImageMain1);
        tvLocation = view.findViewById(R.id.tvLocation);
        ivProfileImageMain1.setImageResource(getResources().getIdentifier(user.userId.replaceAll(" ", ""), "drawable", getActivity().getPackageName()));

        tvCallContact1.setOnClickListener(new View.OnClickListener() {
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
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "location pressed", Toast.LENGTH_SHORT).show();
            }
        });

        tvNameContact1.setText(user.name);
    }


}
