package compass.compass.models;

import android.util.Log;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icamargo on 7/11/17.
 */

public class Event {
    public Group group;
    public Time startTime;
    public Time endTime;
    public Timer timer;
    public TimerTask timerTask;


    public Event(Group invitees, Time starting, Time ending){
        group = invitees;
        startTime = starting;
        endTime = ending;

        setTimer();
    }

    public void setTimer(){
        timer = new Timer("Event Timer", true);
        //boolean corresponds to isDaemon, which i think is true (runs in background...?)
        //todo: ask about this to Abhik

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("Event", "inside timer task");
            }
        };
        //TODO: figure out this part with how we want to implement the timer: check all the time?
        //ask abhik
        //timer.schedule(task, );
        //NEED TO MAKE THIS AFFECT THE BOOLEAN WITHIN GROUP
    }

}
