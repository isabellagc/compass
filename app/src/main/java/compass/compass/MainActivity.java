package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void toDrinkCount(View view) {
        Intent i = new Intent(this, DrinkCounter.class);
        startActivity(i);
    }

    public void toEvent(View view) {
        Intent i = new Intent(this, MapMessaging.class);
        startActivity(i);
    }
}
