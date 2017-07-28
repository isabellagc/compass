package compass.compass;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        RecyclerView rvChat = (RecyclerView) ((ChatActivity) this.mContext).findViewById(R.id.rvContacts);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        final boolean isMe = message.getSender() != null && message.getSender().equals(currentProfile.userId);
        final boolean isBOT =  message.getSender() != null && message.getSender().equals("BOT");

        if (isMe) {
//            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.GONE);
            holder.tvMeChatText.setVisibility(View.VISIBLE);
            holder.tvMeChatText.setText(message.getText());
            holder.body.setVisibility(View.GONE);
            holder.tvBot.setVisibility(View.GONE);
 //           holder.tvUserName.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            holder.tvMeChatText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
//            holder.imageMe.setImageResource(mContext.getResources().getIdentifier(message.getSender().replaceAll(" ",""), "drawable", mContext.getPackageName()));
//            holder.body.setText(message.getText());
            holder.tvUserName.setVisibility(View.GONE);
        } else if(isBOT) {
            holder.imageOther.setVisibility(View.GONE);
           // holder.imageMe.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.GONE);
            holder.tvMeChatText.setVisibility(View.GONE);
            holder.tvBot.setVisibility(View.VISIBLE);
            holder.body.setVisibility(View.GONE);
            holder.tvBot.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
           // holder.imageOther.setImageResource(mContext.getResources().getIdentifier(message.getSender().replaceAll(" ",""), "drawable", mContext.getPackageName()));
            holder.tvBot.setText(message.getText()+ '\n');
        } else {
            holder.body.setVisibility(View.VISIBLE);
            if(position == 0){
                holder.tvUserName.setVisibility(View.VISIBLE);
                holder.tvUserName.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                holder.tvUserName.setText(message.getSender());

            }
            else if(!(mMessages.get(position-1).getSender().contentEquals(message.getSender()))){
                holder.tvUserName.setVisibility(View.VISIBLE);
                holder.tvUserName.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                holder.tvUserName.setText(message.getSender());

            }
            else{
                holder.tvUserName.setVisibility(View.GONE);

            }
            if(position != getItemCount()-1 && mMessages.get(position+1).getSender().contentEquals(message.getSender())){
                holder.imageOther.setVisibility(View.GONE);
            }
            else{
                holder.imageOther.setVisibility(View.VISIBLE);
                int tempId = mContext.getResources().getIdentifier(message.getSender().replaceAll(" ",""), "drawable", mContext.getPackageName());
                Drawable something = ContextCompat.getDrawable(mContext, tempId);
                holder.imageOther.setImageDrawable(something);
            }
            //holder.imageMe.setVisibility(View.GONE);
            holder.tvMeChatText.setVisibility(View.GONE);
            holder.tvBot.setVisibility(View.GONE);
            //holder.rlChat.setMinimumHeight(holder.body.getHeight());
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.START | Gravity.FILL_VERTICAL);
            holder.body.setText(message.getText());

        }
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
        TextView tvBot;
        TextView tvMeChatText;
        RelativeLayout rlChat;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            //imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvChatText);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBot = (TextView) itemView.findViewById(R.id.tvBot);
            tvMeChatText = (TextView) itemView.findViewById(R.id.tvMeChatText);
            rlChat = (RelativeLayout) itemView.findViewById(R.id.rlChat);

            imageOther.setImageResource(R.drawable.rsz_girl);
        }
    }
    //constructor for chat adapter
    public ChatAdapter(Class<ChatActivity> chatActivityClass, String eventId) {
    }
    //another constructor for chat adapter
    public ChatAdapter(FragmentManager supportFragmentManager, ChatActivity chatActivity) {
    }


}
