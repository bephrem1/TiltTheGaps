package benyamephrem.tilt.GameLogic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Vista on 4/19/15.
 */
public class Circle extends View {
    public float x;
    public float y;
    private int r;
    public int color;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //construct new ball object
    public Circle (Context context, float x, float y, int r) {
        super(context);
        //color hex is [transparncy][red][green][blue]
        mPaint.setColor(0xFF15FFD4);  //not transparent. color is white
        this.x = x;
        this.y = y;
        this.r = r;  //radius
    }

    //qcalled by invalidate()
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, r, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public int getR() {
        return r;
    }


    public int getColor() {
        return color;
    }
}

