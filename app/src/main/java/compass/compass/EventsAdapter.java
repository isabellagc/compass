package compass.compass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.Event;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by amusipatla on 7/14/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    public ArrayList<Event> mEvents;
    public Map userEvents;
    public DatabaseReference mDatabase;
    public ArrayList keys;
    Context mContext;

    public EventsAdapter(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userEvents = new HashMap();
        keys = new ArrayList();
        mEvents = new ArrayList<>();
        mContext = context;

        getEvents(currentProfile.name);
    }

    public void getEvents(String name) {
        mDatabase.child("Users").child(name).child("events").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String eventKey = dataSnapshot.getKey();
                keys.add(eventKey);
                if(eventKey != null) {
                    mDatabase.child("Events").child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map temp = (Map) dataSnapshot.getValue();
                            Event event = new Event();
                            event.setEndTime((Long) temp.get("End"));
                            event.setStartTime((Long) temp.get("Start"));
                            event.id = (Long) Long.valueOf(eventKey);
                            event.setName((String) temp.get("EventName"));

                            mEvents.add(event);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("wow", "here");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String eventKey = dataSnapshot.getKey();
                int index = keys.indexOf(eventKey);
                keys.remove(index);
                mEvents.remove(index);
                notifyDataSetChanged();
                Log.d("wow", "here");
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
        holder.tvStart.setText(mEvents.get(position).getStartTime().toString());
        holder.tvEnd.setText(mEvents.get(position).getEndTime().toString());
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;
        public TextView tvStart;
        public TextView tvEnd;
        public RelativeLayout rlEvent;

        public ViewHolder(View itemView) {
            super(itemView);

            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvStart = (TextView) itemView.findViewById(R.id.tvStart);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            rlEvent = (RelativeLayout) itemView.findViewById(R.id.rlEvent);

            rlEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Long eventId = mEvents.get(pos).id;
                    Intent i = new Intent(mContext, LocationActivity.class);
                    i.putExtra("eventId", eventId);
                    mContext.startActivity(i);
                }
            });


        }
    }
}
