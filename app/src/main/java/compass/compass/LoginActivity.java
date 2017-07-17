package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by icamargo on 7/14/17.
 */

public class LoginActivity extends AppCompatActivity{
    private EditText etName;
    private Button btLogin;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);

        etName = (EditText) findViewById(R.id.etName);
        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                Intent i  = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("userToCheck", name);
                startActivity(i);
                //setMainUser(name);
            }
        });

    }

//    private void setMainUser(String s){
//        if(MainActivity.checkDBForUser(s)){
//            //move on to next intent
//        }else{
//            //do something to tell the people they didnt pick a good user
//        }
//    }
}
