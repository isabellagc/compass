package compass.compass.models;

/**
 * Created by icamargo on 7/11/17.
 */


public class User {
    public String name;
    public String email;
    public String gender;
    //this is stored as a string  our firebase database
    public Event currentEvent;
    //this is just a boolean for now (one event) but when multiple events will have to be included in a
    //hash map of sorts (change the adapter)
    public boolean added;
    //data class for geographic location: latitude, lognitude, timestamp, and other info
    //public Location location;


    //added the default constructor for Firebase interactions...
    public User(){
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

    public User(String nameInfo, String emailInfo, String genderInfo){
        name = nameInfo;
        email = emailInfo;
        gender = genderInfo;
        added = false;
        //TODO: instead of updating this to null and added to false when you make a new user go through the database for the events and use that info
        currentEvent = null;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
