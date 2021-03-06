package compass.compass;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import compass.compass.fragments.StatusFragment;
import compass.compass.models.Event;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by amusipatla on 7/14/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    public static ArrayList<Event> mEvents;
    public Map userEvents;
    public DatabaseReference mDatabase;
    public ArrayList keys;
    static Context mContext;

    public EventsAdapter(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userEvents = new HashMap();
        keys = new ArrayList();
        mEvents = new ArrayList<>();
        mContext = context;

        //MAKE SURE THAT DB IS SET UP SO ID FOR A CHILD OF USERS IS ALWAYS IDENTICAL TO NAME
        getEvents(currentProfile.userId);
    }

    public void getEvents(final String name) {
        mDatabase.child("Events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String eventKey = dataSnapshot.getKey();
                keys.add(eventKey);
                if(eventKey != null) {
                    Map info = (Map) dataSnapshot.getValue();
                    Map users = (Map) info.get("Members");

                    if (users.containsKey(name)) {
                        Event event = new Event();
                        event.setEndTime((Long) info.get("End"));
                        event.setStartTime((Long) info.get("Start"));
                        event.id = eventKey;
                        event.setMyStatus((String) users.get(name));
                        event.setName((String) info.get("EventName"));

                        if(event.getMyStatus().contentEquals("null")){
                            mEvents.add(0, event);
                        }
                        else{
                            mEvents.add(event);
                        }
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final String eventKey = dataSnapshot.getKey();
                //keys.add(eventKey);
                if(eventKey != null) {
                    Map info = (Map) dataSnapshot.getValue();
                    Map users = (Map) info.get("Members");

                    if (users.containsKey(name) && mEvents.indexOf(name) != -1) {
                        mEvents.get(mEvents.indexOf(name)).setMyStatus((String) users.get(name));

                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("childremovedadapter", "why is this getting removed??");
//                final String eventKey = dataSnapshot.getKey();
//                int index = keys.indexOf(eventKey);
//                keys.remove(index);
//                mEvents.remove(index);
//                notifyDataSetChanged();
//                Log.d("wow", "here");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("wow", "here");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("wow", "here");
            }
        });

    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View eventView = inflater.inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        holder.tvEventName.setText(mEvents.get(position).getName());

        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(mEvents.get(position).getStartTime());
        DateFormat formatterStart = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String dateFormattedStart = formatterStart.format(calStart.getTime());

        holder.tvStart.setText(dateFormattedStart);

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(mEvents.get(position).getEndTime());
        DateFormat formatterEnd = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String dateFormattedEnd = formatterEnd.format(calEnd.getTime());

        holder.tvEnd.setText(dateFormattedEnd);


        if(mEvents.get(position).getName().contentEquals("Crew Team")){
            holder.ivCircleImage.setImageResource(R.drawable.rowing);
        }
        else if(mEvents.get(position).getName().contentEquals("Dorm Friends")){
            holder.ivCircleImage.setImageResource(R.drawable.dorm_friends);
        }
        else if(mEvents.get(position).getName().contentEquals("Study Group")){
            holder.ivCircleImage.setImageResource(R.drawable.robotics);
        }
        else{
            holder.ivCircleImage.setImageResource(R.drawable.dorm_friends);
        }

        if(mEvents.get(position).getMyStatus().equals("null")){
            holder.clEvent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.twelvePercentOpacityBlack));
        }
        else{
            holder.clEvent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.default_fill_color));
        }

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;
        public TextView tvStart;
        public TextView tvEnd;
        public ConstraintLayout rlEvent;
        public ConstraintLayout clEvent;
        public CircleImageView ivCircleImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvStart = (TextView) itemView.findViewById(R.id.tvStart);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            rlEvent = (ConstraintLayout) itemView.findViewById(R.id.rlEvent);
            clEvent = (ConstraintLayout) itemView.findViewById(R.id.clEvent);
            ivCircleImage = itemView.findViewById(R.id.ivCircleImage);

            rlEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    String eventId = mEvents.get(pos).id;

                    String fromHere = "";
                    if(mContext instanceof EventActivity){
                        fromHere = "eventActivity";
                    }else if(mContext instanceof ProfileActivity){
                        fromHere = "profileActivity";
                    }

                    if(mEvents.get(pos).myStatus.contentEquals("null")){

                        EventActivity activity = (EventActivity) mContext;
                        FragmentManager fm = activity.getSupportFragmentManager();
                        StatusFragment statusFragment = StatusFragment.newInstance(eventId, fromHere);
                        statusFragment.show(fm, "tag");

                    }
                    else{
                        Intent i = new Intent(mContext, ChatActivity.class);
                        i.putExtra("eventId", eventId);
                        i.putExtra("fromHere", fromHere);
                        mContext.startActivity(i);
                    }


                }
            });
        }

//        public static void makeClickable(boolean bool){
//            rlEvent.setClickable(bool);
//        }
    }


}
