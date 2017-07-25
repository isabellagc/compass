package compass.compass;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;

import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by amusipatla on 7/14/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<ChatMessage> mMessages;
    private DatabaseReference mDatabase;
    private Context mContext;
    String my_events[];
    String[] receivers;



    public ChatAdapter(Context context, String eventId) {
        mContext = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessages = new ArrayList<>();
        getMessages(eventId);
    }



    public void getMessages(String eventId) {
        mDatabase.child("messages").child(eventId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map message = (Map) dataSnapshot.getValue();
                final ChatMessage newMessage = new ChatMessage();
                newMessage.setTime((Long) message.get("time"));
                newMessage.setText((String) message.get("text"));
                newMessage.setSender((String) message.get("sender"));
                mMessages.add(newMessage);
                scrollView();
                notifyDataSetChanged();
                //check if the person is available to receive the message

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO: IF WE WANT SOEMTHING TO HAPPEN FOR CHANGED MESSAGES HAVE TO ADD SOMETHING HERE
//                Toast.makeText(mContext, "message changed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void scrollView(){
        RecyclerView rvChat = (RecyclerView) ((ChatActivity) this.mContext).findViewById(R.id.rvChat);
        rvChat.smoothScrollToPosition(mMessages.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        final boolean isMe = message.getSender() != null && message.getSender().equals(currentProfile.userId);

        if (isMe) {
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.GONE);
            holder.tvUserName.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.imageMe.setImageResource(mContext.getResources().getIdentifier(message.getSender().replaceAll(" ",""), "drawable", mContext.getPackageName()));
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.tvUserName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.imageOther.setImageResource(mContext.getResources().getIdentifier(message.getSender().replaceAll(" ",""), "drawable", mContext.getPackageName()));
        }

//        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;
//        Glide.with(mContext).load(getProfileUrl(message.getSender())).into(profileView);
        holder.body.setText(message.getText());
        holder.tvUserName.setText(message.getSender());
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        ImageView imageMe;
        TextView tvUserName;
        TextView body;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvChatText);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);

            imageOther.setImageResource(R.drawable.rsz_girl);
        }
    }
    //constructor for chat adapter
    public ChatAdapter(Class<ChatActivity> chatActivityClass, String eventId) {
    }

//    public void restrictedNotifications() {
//        mDatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map events = (Map) dataSnapshot.getValue();
//                Set<String> valid_events = events.keySet();
//                my_events = (String[]) valid_events.toArray(new String[valid_events.size()]);
//                //go through those valid events
//                for (int i = 0; i < my_events.length; i++ ){
//                    final String this_event = my_events[i];
//                    mDatabase.child("Events").child(this_event).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map the_members = (Map) dataSnapshot.getValue();
//                            Set<String> people = the_members.keySet();
//                            receivers =  people.toArray(new String[people.size()]);
//                            //SEND THE MESSAGE
//                            for(int i = 0; i <receivers.length; i ++){
//                                mDatabase.child("Events").child(this_event).child("Members").child(receivers[i]).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        String my_members = (String) dataSnapshot.getValue();
//                                        //String availability = String.valueOf(my_members.keySet());
//                                        if (my_members != "null"){
//                                            //send message
//                                            //TODO RESTRICT THE NOTIFICATIONS
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
