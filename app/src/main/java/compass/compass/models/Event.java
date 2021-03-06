package compass.compass.models;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icamargo on 7/11/17.
 */

public class Event {
    public Group group;
    public String name;
//    public Date startTime;
//    public Date endTime;
    public Long startTime;
    public Long endTime;
    public Timer timer;
    public TimerTask timerTask;
    public String id;
    public String myStatus;


    public Event(){
        //default constructor
    }

    public Event(String myname, Group invitees, Long starting, Long ending){
        name = myname;
        group = invitees;
        startTime = starting;
        endTime = ending;
        setTimer();
    }

//    public Event(String myname, Group invitees, Long starting, Long ending){
//        name = myname;
//        group = invitees;
////        startTime = new Date(starting);
////        endTime = new Date(ending);
//
//
//        setTimer();
//    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    //    public Date getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(Long startTime) {
//        this.startTime = new Date(startTime);
//    }
//
//    public Date getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(Long endTime) {
//        this.endTime = new Date(endTime);
//    }

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

    public String getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(String myStatus) {
        this.myStatus = myStatus;
    }
}
