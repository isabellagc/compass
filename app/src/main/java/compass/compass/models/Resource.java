package compass.compass.models;

/**
 * Created by brucegatete on 7/12/17.
 */

public class Resource {
    public int police_number;
    public String counsel_name;
    public String counsel_email;
    public String counsel_address;
    public String hospital_name;
    public String hospital_address;
    public int hospital_number;
    public String map_url;

    public void Resource(int p_number, String c_name, String c_email,
                                 String c_address, String h_name, String h_address,
                                 int h_number, String m_url){
        police_number = p_number;
        counsel_name = c_name;
        counsel_email = c_email;
        counsel_address = c_address;
        hospital_name = h_name;
        hospital_address = h_address;
        hospital_number = h_number;
        map_url = m_url;
    }

}
