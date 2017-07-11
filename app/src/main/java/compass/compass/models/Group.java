package compass.compass.models;

import java.util.ArrayList;

/**
 * Created by icamargo on 7/11/17.
 */

public class Group {
    private ArrayList<User> users;
    //commented out until we get locations working
    //private HashMap<User, Location> mappedLocations;
    public boolean active;

    public Group(ArrayList<User> list, boolean activeInfo){
        users = list;
        active = activeInfo;
    }


    //unsure if we need this..
    public void addUser(User user){
        users.add(user);
        //probably have to do something here to notify an adapter? if we are
        //showing all users.
    }
}
