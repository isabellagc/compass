package compass.compass.models;

/**
 * Created by icamargo on 7/11/17.
 */


public class User {
    public String name;
    public String email;
    public String gender;
    public Event currentEvent;
    //data class for geographic location: latitude, lognitude, timestamp, and other info
    //public Location location;


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
        currentEvent = null;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
