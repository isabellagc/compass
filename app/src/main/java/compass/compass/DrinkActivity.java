package compass.compass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by brucegatete on 7/11/17.
 */

public class DrinkActivity extends AppCompatActivity{
    ImageButton btAddDrink;
    ImageButton btShot;
    ImageButton btLiquor;
    TextView tvDrinkNumber;
    TextView tvShot;
    TextView tvLiquor;
    public int Drink_no = 0;
    public int Shot_no = 0;
    public int Liquor_no = 0;
    public double Drink_ratio = 0.05;
    public double Shot_ratio = 0.4;
    public double Liquor_ratio = 0.3;
    public double BAC;
    public int weight = 150;
    public double index = 0.66;
    public long time_start;
    public long time_end;
    public long time_elapsed;
    public double alcohol_level;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_backup);
        btAddDrink = (ImageButton) findViewById(R.id.btAddDrink);
        tvDrinkNumber = (TextView) findViewById(R.id.tvDrinkNumber);
        btShot = (ImageButton) findViewById(R.id.btShot);
        btLiquor = (ImageButton) findViewById(R.id.btLiquor);
        tvLiquor = (TextView) findViewById(R.id.tvLiquor);
        tvShot = (TextView) findViewById(R.id.tvShot);
        increaseDrinkNumber();
        increaseShotCount();
        increaseLiquorCount();
    }

    public void increaseDrinkNumber() {
        btAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //increment the number of drinks
                Drink_no += 1;
                tvDrinkNumber.setText(String.valueOf(Drink_no));
                if (time_start == 0) {
                    time_start = System.currentTimeMillis();
                }else{
                    time_end = System.currentTimeMillis();
                    time_elapsed = time_end - time_start;
                }
                alcohol_level= (Drink_no * Drink_ratio * 12) + (Liquor_no * Liquor_ratio) + (Shot_no * Shot_ratio);
                BAC = (alcohol_level * 5.14 /(weight * index)) - (time_elapsed * 0.015/(1000 * 3600));
                if(BAC >= 0.08){
                    Toast.makeText(DrinkActivity.this, String.valueOf(BAC), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void increaseShotCount() {
        btLiquor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //increment the number of shots
                Liquor_no += 1;
                tvLiquor.setText(String.valueOf(Liquor_no));
                if (time_start == 0) {
                    time_start = System.currentTimeMillis();
                }else{
                    time_end = System.currentTimeMillis();
                    time_elapsed = time_end - time_start;
                }
                alcohol_level= (Drink_no * Drink_ratio * 12) + (Liquor_no * Liquor_ratio) + (Shot_no * Shot_ratio * 1.5);
                BAC = (alcohol_level * 5.14 /(weight * index)) - (time_elapsed * 0.015/(1000.0 * 3600));
                if(BAC >= 0.08){
                    Toast.makeText(DrinkActivity.this,  String.valueOf(BAC), Toast.LENGTH_SHORT).show();

                }
            }

        });
    }
    public void increaseLiquorCount() {
        btShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Shot_no += 1;
                tvShot.setText(String.valueOf(Shot_no));
                if (time_start == 0) {
                    time_start = System.currentTimeMillis();
                }else{
                    time_end = System.currentTimeMillis();
                    time_elapsed = time_end - time_start;
                }
                alcohol_level= (Drink_no * Drink_ratio * 12) + (Liquor_no * Liquor_ratio) + (Shot_no * Shot_ratio * 1.5);
                BAC = (alcohol_level * 5.14 /(weight * index)) - (time_elapsed * 0.015/(1000.0 * 3600));
                if (BAC >= 0.08){
                    Toast.makeText(DrinkActivity.this, String.valueOf(BAC), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
