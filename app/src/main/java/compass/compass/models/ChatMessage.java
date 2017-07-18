package compass.compass.models;

import java.util.Date;

/**
 * Created by amusipatla on 7/14/17.
 */

public class ChatMessage {
    private String Text;
    private String Sender;
    private long Time;

    public ChatMessage(String messageText, String messageUser) {
        this.Text = messageText;
        this.Sender = messageUser;

        // Initialize to current time
        this.Time = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getText() {
        return Text;
    }

    public void setText(String messageText) {
        this.Text = messageText;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String messageUser) {
        this.Sender = messageUser;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long messageTime) {
        this.Time = messageTime;
    }
}

