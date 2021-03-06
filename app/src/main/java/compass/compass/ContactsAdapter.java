package compass.compass;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import compass.compass.models.User;

import static compass.compass.MainActivity.allContacts;

/**
 * Created by icamargo on 7/13/17.
 */

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    //list of contacts
    private ArrayList<User> mContacts;
    //store context for easy access
    private Context mContext;
    private boolean startedActivity;
    String eventID;

    //todo: update this so it uses firebase db
    public ContactsAdapter(Context context, ArrayList<User> contacts, String eventIdInfo) {
        mContext = context;
        mContacts = contacts;
        startedActivity = true;
        eventID = eventIdInfo;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    //provide direct reference to each of the views within our data item (item_contact)
    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView tvContactName;
        public CheckBox cbAddContact;
        public ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            cbAddContact = (CheckBox) itemView.findViewById(R.id.cbAddContact);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImageMain1);

            cbAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    User currentUser = mContacts.get(position);
                    currentUser.added.put(eventID, cbAddContact.isChecked());
                }
            });
        }
    }

    public Context getContext() {
        return mContext;
    }

    //inflate the layout from xml and return the holder
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate custom layout
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
       //get data model based on position
        User user = mContacts.get(position);

        //set item views based on views and data model
        TextView tvContactName = holder.tvContactName;
        CheckBox cbAddContact = holder.cbAddContact;
        ImageView ivProfileImage = holder.ivProfileImage;

        tvContactName.setText(user.name);
        cbAddContact.setChecked(user.added.get(eventID));

        if (mContacts != null){
            if(allContacts.containsKey(user.name)){
                int drawableResourceId = mContext.getResources().getIdentifier(mContacts.get(position).name.replaceAll(" ", ""), "drawable", mContext.getPackageName());
                holder.ivProfileImage.setImageResource(drawableResourceId);
            }
            else{
                holder.ivProfileImage.setImageResource(R.drawable.ic_person);
            }
        }

    }


    //return total count of items in the list
    //USE THIS WHEN ITERATING OVER LIST TO SEE WHO TO ADD TO THE GROUP
    @Override
    public int getItemCount() {
        if (mContacts == null){
            return 0;
        }

        return mContacts.size();
    }
}
