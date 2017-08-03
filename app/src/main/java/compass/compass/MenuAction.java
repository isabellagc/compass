package compass.compass;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by icamargo on 8/1/17.
 */

public class MenuAction extends AppCompatActivity{

    public void message911(){
        Context context = getApplicationContext();
        Toast.makeText(context, "message 911 item clicked", Toast.LENGTH_LONG).show();
    }

    public void call911(){
        Context context = getApplicationContext();
        Toast.makeText(context, "call 911 item clicked", Toast.LENGTH_LONG).show();
    }
}
