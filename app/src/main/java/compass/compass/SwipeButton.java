package compass.compass;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by amusipatla on 7/11/17.
 */

public class SwipeButton extends AppCompatButton {

    private float x1;
    private float y1;
    private String originalButtonText;
    private boolean confirmThresholdCrossed;
    private boolean swipeTextShown;
    private boolean swiping = false;
    private float x2Start;

    private SwipeButtonCustomItems swipeButtonCustomItems;

    public SwipeButton(Context context) {
        super(context);
    }

    public SwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSwipeButtonCustomItems(SwipeButtonCustomItems swipeButtonCustomItems) {
        this.swipeButtonCustomItems = swipeButtonCustomItems;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                //when user first touches the screen change the button text to desired value
                x1 = event.getX();
                y1 = event.getY();

                this.originalButtonText = this.getText().toString();

                confirmThresholdCrossed = false;

                if(!swipeTextShown) {
                    this.setText(swipeButtonCustomItems.getButtonPressText());
                    swipeTextShown = true;
                }

                swipeButtonCustomItems.onButtonPress();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //when the user releases touch then revert back the text
                float x2 = event.getX();
                float y2 = event.getY();

                if(!swiping) {
                    x2Start = event.getX();
                    swiping = true;
                }

                if (x1 < x2 && !confirmThresholdCrossed) {
                    this.setBackgroundDrawable(null);

                    ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());

                    int gradientColor1 = swipeButtonCustomItems.getGradientColor1();
                    int gradientColor2 = swipeButtonCustomItems.getGradientColor2();
                    int gradientColor2Width = swipeButtonCustomItems.getGradientColor2Width();
                    int gradientColor3 = swipeButtonCustomItems.getGradientColor3();
                    double actionConfirmDistanceFraction = swipeButtonCustomItems.getActionConfirmDistanceFraction();

                    Shader shader = new LinearGradient(x2, 0, x2 - gradientColor2Width, 0,
                            new int[]{gradientColor3, gradientColor2, gradientColor1},
                            new float[] {0, 0.5f, 1}, Shader.TileMode.CLAMP);

                    mDrawable.getPaint().setShader(shader);
                    this.setBackgroundDrawable(mDrawable);

                    if (!swipeTextShown) {
                        this.setText(swipeButtonCustomItems.getButtonPressText());
                        swipeTextShown = true;
                    }

                    if ((x2-x2Start) > (this.getWidth() * actionConfirmDistanceFraction)) {
                        Log.d("CONFIRMATION", "Action Confirmed");

                        confirmThresholdCrossed = true;
                    }
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                //here we'll capture when the user swipes from left to right and write the logic to create the swiping effect
                swiping = false;
                float x2 = event.getX();
                int buttonColor = swipeButtonCustomItems.getPostConfirmationColor();
                String actionConfirmText = swipeButtonCustomItems.getActionConfirmText() == null ? this.originalButtonText : swipeButtonCustomItems.getActionConfirmText();

                this.setBackgroundDrawable(null);
                this.setBackgroundColor(buttonColor);
                swipeTextShown = false;

                if ((x2-x2Start) <= (this.getWidth() * swipeButtonCustomItems.getActionConfirmDistanceFraction())) {
                    Log.d("CONFIRMATION", "Action not confirmed");
                    this.setText(originalButtonText);
                    swipeButtonCustomItems.onSwipeCancel();
                    confirmThresholdCrossed = false;

                } else {
                    Log.d("CONFIRMATION", "Action confirmed");
                    this.setText(actionConfirmText);
                }

                break;
            }
        }

        return super.onTouchEvent(event);
    }
}
