package compass.compass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.john.waveview.WaveView;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/11/17.
 */

public class DrinkActivityReal extends AppCompatActivity{
    public static final int GRAMS_IN_STANDARD_DRINK = 14;
    public static final double LBS_TO_GRAMS_CONVERSION = 453.59237;
    public static final double MALE_CONSTANT = .68;
    public static final double FEMALE_CONSTANT = .55;

    //todo: decide as a group what the max bac (where the body is full) should be!
    public static final double MAX_BAC = .13;

    ImageButton btAddDrink;
    TextView tvDrinkNumber;
    ImageView ivPerson;
    //info: refers to number of STANDARD DRINKS (1.5 oz hard, 12oz beer, 5(?) oz wine, etc)
    public int drinks;

    //    public double Drink_ratio = 0.05;
//    public double Shot_ratio = 0.4;
//    public double Liquor_ratio = 0.3;
    public double BAC;
    public int weight;
    public double index = 0.66;
    public long time_start;
    public long time_end;
    public long time_elapsed;

    //grams of alcohol consumed (standard drinks x 14)
    private double alcoholContent;
    private double percentageOfMax;
    private WaveView waveView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_real);
        btAddDrink = (ImageButton) findViewById(R.id.btDrinkCounter);
        tvDrinkNumber = (TextView) findViewById(R.id.tvDrinkNumber);

        weight = currentProfile.weight;

        waveView = (WaveView) findViewById(R.id.wave_view);
        ivPerson = (ImageView) findViewById(R.id.ivPerson);
        if(currentProfile.gender.equals("f")){
            ivPerson.setImageResource(R.drawable.ic_female);
        }else{
            ivPerson.setImageResource(R.drawable.ic_male);
        }
        increaseDrinkNumber();
//        increaseShotCount();
//        increaseLiquorCount();
    }

    public void increaseDrinkNumber() {
        btAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //increment the number of drinks
                drinks += 1;
                tvDrinkNumber.setText(String.valueOf(drinks));
                BAC = calculateBAC();
                percentageOfMax = (BAC / MAX_BAC)  * 100;
                waveView.setProgress((int) percentageOfMax);

                if(BAC >= 0.08){
                    Toast.makeText(DrinkActivityReal.this, "BAC level: " + Double.toString(BAC) + " SLOW DOWN!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private double calculateBAC(){
        double total = 0.0;
        if (time_start == 0) {
            time_start = System.currentTimeMillis();
        }else{
            time_end = System.currentTimeMillis();
            time_elapsed = time_end - time_start;
        }

        alcoholContent = drinks * GRAMS_IN_STANDARD_DRINK;
        double bodyWeightInGrams = weight * LBS_TO_GRAMS_CONVERSION;
        if(currentProfile.gender.equals("f")){
            bodyWeightInGrams *= FEMALE_CONSTANT;
        }else{
            bodyWeightInGrams *= MALE_CONSTANT;
        }

        total = (alcoholContent / bodyWeightInGrams) * 100;
        //account for elapsed time
        total -= time_elapsed * 0.015/(1000 * 3600);

        return total;
//        BAC = (alcohol_level * 5.14 /(currentProfile.weight * index)) - (time_elapsed * 0.015/(1000 * 3600));
    }
//    public void increaseShotCount() {
//        btLiquor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //increment the number of shots
//                Liquor_no += 1;
//                tvLiquor.setText(String.valueOf(Liquor_no));
//                progress += 5;
//                waveView.setProgress(progress);
//                if (time_start == 0) {
//                    time_start = System.currentTimeMillis();
//                }else{
//                    time_end = System.currentTimeMillis();
//                    time_elapsed = time_end - time_start;
//                }
//                alcohol_level= (Drink_no * Drink_ratio * 12) + (Liquor_no * Liquor_ratio) + (Shot_no * Shot_ratio * 1.5);
//                BAC = (alcohol_level * 5.14 /(currentProfile.weight * index)) - (time_elapsed * 0.015/(1000.0 * 3600));
//                if(BAC >= 0.08){
//                    Toast.makeText(DrinkActivityReal.this,  "Please stop", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//        });
//    }
//    public void increaseLiquorCount() {
//        btShot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Shot_no += 1;
//                tvShot.setText(String.valueOf(Shot_no));
//                progress += 5;
//                waveView.setProgress(progress);
//                if (time_start == 0) {
//                    time_start = System.currentTimeMillis();
//                }else{
//                    time_end = System.currentTimeMillis();
//                    time_elapsed = time_end - time_start;
//                }
//                alcohol_level= (Drink_no * Drink_ratio * 12) + (Liquor_no * Liquor_ratio) + (Shot_no * Shot_ratio * 1.5);
//                BAC = (alcohol_level * 5.14 /(currentProfile.weight * index)) - (time_elapsed * 0.015/(1000.0 * 3600));
//                if (BAC >= 0.08){
//                    Toast.makeText(DrinkActivityReal.this, "Please stop", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}























//