package benyamephrem.tilt.GameLogic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    public float x;
    public float y;
    private final int r;
    public int color;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    String rectTag = "Rectangle1";

    public String getRectTag() {
        return rectTag;
    }

    public void setRectTag(String rectTag) {
        this.rectTag = rectTag;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        this.color = color;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    //construct new ball object
    public BallView(Context context, float x, float y, int r) {
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

    public int getColor() {
        return color;
    }
}
