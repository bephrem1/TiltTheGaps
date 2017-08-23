package benyamephrem.tilt.GameLogic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

/**
 * Created by Vista on 2/18/15.
 */
public class Rectangle extends View {

    public float left;
    public float top;
    public float right;
    public float bottom;
    public int color;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void setColor(int color) {
        this.color = color;
    }

    public static int[] colorArray = {0xFF949CFF, 0xffeef16b, 0xFFFFAD85, 0xFFAFFF78, 0xFFE3ABFF,
            0xFF7DFFE0, 0xFFFEBC71, 0xFFA877FB, 0xFF62FF8B, 0xFFF99AA1, 0xFFA9FF53,
            0xFFD02A21, 0xFF1D1AD0, 0xFFCED07E, 0xFF60B4FF, 0xFFFFA1E0};


    /*Faded Blue, yellow, salmon, green, pink-red, light blue, light orange, purple, teal, pink,
    light-green, red, blue, sand, lighter blue, pink*/
    public static Random randomGenerator = new Random();


    public int getColor() {
        return color;
    }

    public void setFixedColor(int color) {
        mPaint.setColor(color);
        this.color = color;
    }

    public static void setColor(Rectangle rect1, Rectangle rect2, Rectangle rect3, BallView ball, final TextView textView) {

        int randomNumber = randomGenerator.nextInt(colorArray.length);
        final int color = colorArray[randomNumber];

        rect1.setFixedColor(color);
        rect2.setFixedColor(color);
        rect3.setFixedColor(color);
        ball.setColor(color);
        textView.post(new Runnable() {
            public void run() {
                textView.setTextColor(color);
            }
        });

        rect1.setColor(color);
        rect2.setColor(color);
        rect3.setColor(color);

    }



    public int getTheBottom() {
        return (int) bottom;
    }


    public int getTheLeft() {
        return (int) left;
    }


    public int getTheTop() {
        return (int) top;
    }


    public int getTheRight() {
        return (int) right;
    }

    //construct new rectangle object
    public Rectangle(Context context, float left, float top, float right, float bottom, int color) {
        super(context);
        //color hex is [transparncy][red][green][blue]
        mPaint.setColor(color);  //not transparent. color is white
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    //construct new rectangle object
    public Rectangle(Context context, float left, float top, float right, float bottom) {
        super(context);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    //qcalled by invalidate()
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    public void setX(float left, float right) {
        this.left = left;
        this.right = right;
    }

    public void setY(float top, float bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public int getRectWidth() {
        return (int) (right - left);
    }

    public int getRectHeight() {
        return (int) (bottom - top);
    }

    public int getCenterX() {
        return (int) (right + left) / 2;
    }

    public int getCenterY() {
        return (int) (top + bottom) / 2;
    }

}