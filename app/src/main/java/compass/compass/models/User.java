package compass.compass.models;

import org.parceler.Parcel;

import java.util.HashMap;

/**
 * Created by icamargo on 7/11/17.
 */

@Parcel
public class User {
    public static final String KEY_ASTHMA_INFO = "asthma";
    public static final String KEY_OTHER_INFO = "other";
    public static final String KEY_DIABETES_INFO = "diabetic";
    public static final String KEY_ALLERGIES_INFO = "allergies";

    public static final long KEY_NULL_VALUE = -1;
    public String name;
    public String email;
    public String gender;
    public String school;
    public String userId;
    public String address;
    public int drinkCounter;
    public int weight;
    public HashMap<String, Long> alarms;
    public HashMap<String, String> healthInfo;
    public String phoneNumber;
    public double currentBAC;

    //this is stored as a string  our firebase database
    public Event currentEvent;
//    public int height;
//    public int weight;
    //this is just a boolean for now (one event) but when multiple events will have to be included in a
    //hash map of sorts (change the adapter)

    public HashMap<String, Boolean> added;


    //data class for geographic location: latitude, lognitude, timestamp, and other info
    //public Location location;


    //added the default constructor for Firebase interactions...
    public User(){
        added = new HashMap<>();
        alarms = new HashMap<>();
        healthInfo = new HashMap<>();
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    //****INFORMATION TO UNCOMMENT AS WE ADD EXTRA FEATURES *****//
//    public ArrayList<Event> eventList;
//    public DrinkInfo drinkInfo;
//    //would make a drinkInfo class that takes in height, weight, etc. and comes up with formula
//    //or something along those lines
//    public PriorityInfo priorityInfo;
//    //another module to decompose, takes in a string message, time, etc. for priority to keep trakc
//    //of for the user
//    public String school;


    public User(String nameInfo, String emailInfo, String genderInfo, String schoolInfo, int weightInfo, int drinkInfo){
        name = nameInfo;
        email = emailInfo;
        gender = genderInfo;
        weight = weightInfo;
        added = new HashMap<>();
        drinkCounter = drinkInfo;
        //TODO: instead of updating this to null and added to false when you make a new user go through the database for the events and use that info
        currentEvent = null;
    }

    public HashMap<String, Long> getAlarms() {
        return alarms;
    }

    public void setAlarms(HashMap<String, Long> alarms) {
        this.alarms = alarms;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
