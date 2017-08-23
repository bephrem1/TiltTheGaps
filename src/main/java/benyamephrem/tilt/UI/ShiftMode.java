package benyamephrem.tilt.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import benyamephrem.tilt.GameLogic.BallView;
import benyamephrem.tilt.GameLogic.Circle;
import benyamephrem.tilt.GameLogic.Rectangle;
import benyamephrem.tilt.GameLogic.Scoreboard;
import benyamephrem.tilt.R;


public class ShiftMode extends ActionBarActivity {

    android.graphics.PointF mBallPos, mBallSpd;
    int mScrWidth, mScrHeight;
    Handler mHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr, mTmr2;
    TimerTask mTsk, mTsk2;
    BallView mBallView = null;
    Circle mStartCircle = null;
    Rectangle[] rect = new Rectangle[6];
    Scoreboard mScoreboard = new Scoreboard();
    int sideVisible;
    public static Random randomGenerator = new Random();

    //Game timer varibles
    TextView gameScoreBoard;
    TextView timeView;

    //Add point sound
    MediaPlayer mediaPlayer;

    //Timer control integers
        //For piercing initial start game circle
        int w = 0;

        //For adding point
        int g = 0;

        //For onPause handling of gameTime
        int s = 1;

        //For adding time for a certain point threshold
        int b = 0;

    //Clock time integer
    int currentGameTime = 16;

    //Screen size varibles
    boolean isNormalScreen = false, isLargeScreen = false;

    //Background
    FrameLayout mainView;

    //Color array
    public static int[] colorArray = {0xFFFF5D62, 0xFF59FF4C, 0xFF7076FF};//Red, Green, Blue

    //Ad frequency number
    int n;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_mode);

        //Pass on ad frequency control
        Intent intent1 = getIntent();
        n = intent1.getIntExtra("adsNumber", 0);

        //Set high score
        Intent intent = getIntent();
        mScoreboard.setHighScore(intent.getIntExtra("highScore", 0));

        //Initialize media player
        mediaPlayer = MediaPlayer.create(this, R.raw.blopsound);

        //Declare views
        mainView = (FrameLayout) findViewById(R.id.main_view);
        gameScoreBoard = (TextView) findViewById(R.id.scoreBoard);
        timeView = (TextView) findViewById(R.id.timeView);

        //Get screen size
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            isNormalScreen = true;

        }
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {

            isLargeScreen = true;

        }

        //get screen dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScrWidth = metrics.widthPixels;
        mScrHeight = metrics.heightPixels;
        mBallPos = new PointF();
        mBallSpd = new PointF();

        //create variables for ball position and speed
        mBallPos.x = mScrWidth / 2;
        mBallPos.y = mScrHeight / 2;
        mBallSpd.x = 0;
        mBallSpd.y = 0;

        //create initial ball

        mBallView = new BallView(this, mBallPos.x / 2, mBallPos.y / 2, mScrWidth/30);

        mainView.addView(mBallView);
        mBallView.invalidate(); //call onDraw in BallView


        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (isNormalScreen) {
                            //set ball speed based on phone tilt (ignore Z axis)
                            mBallSpd.x = -event.values[0] * 5;
                            mBallSpd.y = event.values[1] * 5;
                            //timer event will redraw ball
                        }
                        else if (isLargeScreen) {
                            //set ball speed based on phone tilt (ignore Z axis)
                            mBallSpd.x = -event.values[0];
                            mBallSpd.y = event.values[1];
                            //timer event will redraw ball
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);

        //Create initial start game circle player must exit to start game
        mStartCircle = new Circle(this, mScrWidth / 2, mScrHeight / 2, mScrWidth/6);
        mStartCircle.setColor(0x81A2A2A5);
        mainView.addView(mStartCircle);
        mStartCircle.invalidate();

        //Create initial 9 rectangles
        //Left screen edge
        rect[0] = new Rectangle(this, 0, 0, mScrWidth / 30 , mScrHeight / 3);
        mainView.addView(rect[0]);
        rect[0].invalidate();
        rect[0].setVisibility(View.GONE);

        rect[1] = new Rectangle(this, 0, mScrHeight / 3, mScrWidth / 30 ,(mScrHeight/3) * 2);
        mainView.addView(rect[1]);
        rect[1].invalidate();
        rect[1].setVisibility(View.GONE);

        rect[2] = new Rectangle(this, 0, (mScrHeight / 3) * 2 , mScrWidth / 30 , mScrHeight);
        mainView.addView(rect[2]);
        rect[2].invalidate();
        rect[2].setVisibility(View.GONE);

        //Right screen edge
        rect[3] = new Rectangle(this, (mScrWidth / 30) * 29 , 0, mScrWidth , mScrHeight / 3);
        mainView.addView(rect[3]);
        rect[3].invalidate();
        rect[3].setVisibility(View.GONE);

        rect[4] = new Rectangle(this, (mScrWidth / 30) * 29 , mScrHeight / 3 , mScrWidth , (mScrHeight / 3) * 2);
        mainView.addView(rect[4]);
        rect[4].invalidate();
        rect[4].setVisibility(View.GONE);

        rect[5] = new Rectangle(this, (mScrWidth / 30) * 29 , (mScrHeight / 3) * 2 , mScrWidth , mScrHeight);
        mainView.addView(rect[5]);
        rect[5].invalidate();
        rect[5].setVisibility(View.GONE);


        //Set beginning color of Rectangle array and BallView
        setInitialColors(rect, mBallView);

    }// OnCreate

    @Override
    public void onResume() //app moved to background, stop background threads
    {
        //Game logic and moving circle
        mTmr = new Timer();
        mTsk = new TimerTask() {
            @Override
            public void run() {
                //Begins circle movement
                runCircle();

                //Begins game clock
                if (circleBallViewIntersect(mStartCircle, mBallView) && w == 0) {

                    //Make start game circle pulse then disappear
                    final ScaleAnimation anim = new ScaleAnimation(1, 1.20f, 1f, 1.20f, mStartCircle.getX(), mStartCircle.getY());
                    anim.setDuration(100);
                    anim.setRepeatCount(2);
                    anim.setRepeatMode(Animation.REVERSE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStartCircle.startAnimation(anim);
                            mStartCircle.setVisibility(View.GONE);
                        }
                    });

                    mediaPlayer.start();

                    //Make visible the first side
                    pickNextSide();

                    //*****Begin Game Clock*****
                    mTmr2.schedule(mTsk2, 0, 1000);

                    w += 1;

                }

                //Once initial circle has been pierced
                if (w >= 1) {
                    //Handles on pause event where app is brought back to the foreground to restart gameclock timer
                    if (s == 0) {
                        //*****Begins Game Clock*****
                        mTmr2.schedule(mTsk2, 0, 1000);
                        s += 1;
                    }

                    if (sideVisible == 1) {
                        //Checking for left side rectangles
                        if (intersects(mBallView, rect[0])) {
                            if (g == 0 && rect[0].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[0]);
                                        rect[0].setVisibility(View.GONE);
                                        rect[1].setVisibility(View.GONE);
                                        rect[2].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();

                                pickNextSide();
                                pickNextSideColors(1);
                                g += 1;

                            }
                            else if (rect[0].getColor() != mBallView.getColor() || intersects(mBallView, rect[1])) {
                                goToRedirectScreen();
                            }

                        }
                        if (intersects(mBallView, rect[1])) {
                            if (g == 0 && rect[1].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[1]);
                                        rect[0].setVisibility(View.GONE);
                                        rect[1].setVisibility(View.GONE);
                                        rect[2].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();


                                pickNextSide();
                                pickNextSideColors(1);
                                g += 1;

                            }
                            else if (rect[1].getColor() != mBallView.getColor()  || intersects(mBallView, rect[0])  || intersects(mBallView, rect[2])) {
                                goToRedirectScreen();
                            }

                        }
                        if (intersects(mBallView, rect[2])) {
                            if (g == 0 && rect[2].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[2]);
                                        rect[0].setVisibility(View.GONE);
                                        rect[1].setVisibility(View.GONE);
                                        rect[2].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();


                                pickNextSide();
                                pickNextSideColors(1);
                                g += 1;

                            }
                            else if (rect[2].getColor() != mBallView.getColor() || intersects(mBallView, rect[1])) {
                                goToRedirectScreen();
                            }

                        }
                    }
                    else if (sideVisible == 2) {
                        //Check rectangles on right side
                        if (intersects(mBallView, rect[3])) {
                            if (g == 0 && rect[3].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[3]);
                                        rect[3].setVisibility(View.GONE);
                                        rect[4].setVisibility(View.GONE);
                                        rect[5].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();


                                pickNextSide();
                                pickNextSideColors(2);
                                g += 1;

                            }
                            else if (rect[3].getColor() != mBallView.getColor() || intersects(mBallView, rect[4])) {
                                goToRedirectScreen();
                            }

                        }
                        if (intersects(mBallView, rect[4])) {
                            if (g == 0 && rect[4].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[4]);
                                        rect[3].setVisibility(View.GONE);
                                        rect[4].setVisibility(View.GONE);
                                        rect[5].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();


                                pickNextSide();
                                pickNextSideColors(2);
                                g += 1;

                            }
                            else if (rect[4].getColor() != mBallView.getColor() || intersects(mBallView, rect[3]) || intersects(mBallView, rect[5])) {
                                goToRedirectScreen();
                            }

                        }
                        if (intersects(mBallView, rect[5])) {
                            if (g == 0 && rect[5].getColor() == mBallView.getColor()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addPoint();
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(gameScoreBoard);
                                        pulseRectangle(rect[5]);
                                        rect[3].setVisibility(View.GONE);
                                        rect[4].setVisibility(View.GONE);
                                        rect[5].setVisibility(View.GONE);
                                    }
                                });

                                mediaPlayer.start();


                                pickNextSide();
                                pickNextSideColors(2);
                                g += 1;

                            }
                            else if (rect[5].getColor() != mBallView.getColor() || intersects(mBallView, rect[4])) {
                                goToRedirectScreen();
                            }

                        }
                    }//sidevisible
                }


                //Turn timeView red when time is low
                if (currentGameTime <= 5) {
                    timeView.post(new Runnable() {
                        public void run() {
                            timeView.setTextColor(0xDEFF0003);
                        }
                    });
                }


                //GAME OVER if time is zero
                if (timeView.getText().equals(":00")) {
                    timeRanOut();
                }

                //If player gets 10 points add 5 seconds and pulse TextView
                if (b == 0 && mScoreboard.getScore() % 10 == 0 && mScoreboard.getScore() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pulseTextView(timeView);
                            timeView.setTextColor(0xFF6AFF9A);
                        }
                    });

                    currentGameTime += 10;

                    if (currentGameTime <= 9) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeView.setText(":0" + currentGameTime);
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeView.setText(":" + currentGameTime);
                            }
                        });
                    }

                    b += 1;
                }

                //Reset b
                if ((mScoreboard.getScore() - 1) % 10 == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeView.setTextColor(0xFF3F3F3F);
                        }
                    });

                    b = 0;
                }


                //Reset the timer control integers
                if (sideVisible == 1) {
                    if (!intersects(mBallView, rect[0]) ||
                            !intersects(mBallView, rect[1]) ||
                            !intersects(mBallView, rect[2])) {
                        g = 0;
                    }
                }
                else if (sideVisible == 2) {
                    if (!intersects(mBallView, rect[3]) ||
                            !intersects(mBallView, rect[4]) ||
                            !intersects(mBallView, rect[5])) {
                        g = 0;
                    }
                }

                //Reset b
                if ((mScoreboard.getScore() - 2) % 10 == 0) {
                    b = 0;
                }





            }
        };
        mTmr.schedule(mTsk, 0, 10);

        //Game Clock logic
        mTmr2 = new Timer();
        mTsk2 = new TimerTask() {
            @Override
            public void run() {
                if (currentGameTime <= 10) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeView.setText(":0" + currentGameTime);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeView.setText(":" + currentGameTime);
                        }
                    });
                }


                currentGameTime -= 1;
            }
        };



        super.onResume();
    }// OnResume

    @Override
    public void onPause() //app moved to background, stop background threads
    {
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;
        mTmr2.cancel(); //kill\release timer (our only background thread)
        mTmr2 = null;
        mTsk2 = null;

        s = 0;

        super.onPause();
    }

    @Override
    public void onDestroy() //main thread stopped
    {
        super.onDestroy();
        //wait for threads to exit before clearing app
        System.runFinalizersOnExit(true);
        //remove app from memory
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    private void runCircle() {

        //move ball based on current speed
        mBallPos.x += mBallSpd.y;
        mBallPos.y += -mBallSpd.x;

        //Keep ball from going off screen
        if (mBallPos.x > mScrWidth) mBallPos.x = mScrWidth - 5;
        if (mBallPos.y > mScrHeight) mBallPos.y = mScrHeight - 5;
        if (mBallPos.x < 0) mBallPos.x = 5;
        if (mBallPos.y < 0) mBallPos.y = 5;

        //update ball class instance
        mBallView.x = mBallPos.x;
        mBallView.y = mBallPos.y;

        //redraw ball. Must run in background thread to prevent thread lock.
        mHandler.post(new Runnable() {
            public void run() {
                mBallView.invalidate();
            }
        });

    }

    public boolean intersects(BallView ball, Rectangle rect){
        int ballRadius = ball.getR();
        int ballY = (int) ball.getY();
        int ballX = (int) ball.getX();

        double circleDistanceX;
        double circleDistanceY;
        double cornerDistance_sq;


        circleDistanceX = Math.abs(ballX - rect.getCenterX());
        circleDistanceY = Math.abs(ballY - rect.getCenterY());

        if (circleDistanceX > (rect.getRectWidth() / 2 + ballRadius)) { return false; }
        if (circleDistanceY > (rect.getRectHeight() / 2 + ballRadius)) { return false; }

        if (circleDistanceX <= (rect.getRectWidth() / 2)) { return true; }
        if (circleDistanceY <= (rect.getRectHeight() / 2)) { return true; }

        cornerDistance_sq = Math.pow((circleDistanceX - rect.getRectWidth()/2), 2) +
                Math.pow((circleDistanceY - rect.getRectHeight()/2), 2);

        return (cornerDistance_sq <= Math.pow(ball.getR(), 2));

    }

    private void addPoint() {
        mScoreboard.addPoint();
        gameScoreBoard.setText(mScoreboard.getScore() + "");
    }

    private void setInitialColors(Rectangle[] rect, BallView ball) {
        int randomNumber = randomGenerator.nextInt(colorArray.length);
        int color = colorArray[randomNumber];

        ball.setColor(color);

        rect[0].setFixedColor(0xFFFF5D62);
        rect[1].setFixedColor(0xFF59FF4C);
        rect[2].setFixedColor(0xFF7076FF);
        rect[3].setFixedColor(0xFFFF5D62);
        rect[4].setFixedColor(0xFF59FF4C);
        rect[5].setFixedColor(0xFF7076FF);

    }

    private void pickNextSideColors(int currentSide) {

        int randomNumber1 = randomGenerator.nextInt(3);
        int randomNumber2 = randomGenerator.nextInt(3);
        int randomNumber3 = randomGenerator.nextInt(3);
        int ballNumber = randomGenerator.nextInt(3);
        int newBallColor = 0;

        while (randomNumber1 == randomNumber2 ||
                randomNumber2 == randomNumber3 ||
                randomNumber1 == randomNumber3) {
            randomNumber1 = randomGenerator.nextInt(3);
            randomNumber2 = randomGenerator.nextInt(3);
            randomNumber3 = randomGenerator.nextInt(3);
        }

        //Find ball's new color
        if (ballNumber == 0) {
            newBallColor = colorArray[0];
        }
        else if (ballNumber == 1) {
            newBallColor = colorArray[1];
        }
        else if (ballNumber == 2) {
            newBallColor = colorArray[2];
        }

        //Set new randomized colors
        if (currentSide == 1) {
            rect[3].setFixedColor(colorArray[randomNumber1]);
            rect[4].setFixedColor(colorArray[randomNumber2]);
            rect[5].setFixedColor(colorArray[randomNumber3]);

            mBallView.setColor(newBallColor);
        }

        if (currentSide == 2) {
            rect[0].setFixedColor(colorArray[randomNumber1]);
            rect[1].setFixedColor(colorArray[randomNumber2]);
            rect[2].setFixedColor(colorArray[randomNumber3]);

            mBallView.setColor(newBallColor);
        }
    }

    private boolean circleBallViewIntersect(Circle circle, BallView ballView) {
        boolean intersects = false;

        int radiusDiffSq = (int) Math.pow(ballView.getR() - circle.getR(), 2) ;
        float xValueDiffSq = (float) Math.pow(ballView.getX() - circle.getX(), 2);
        float yValueDiffSq = (float) Math.pow(ballView.getY() - circle.getY(), 2);
        int radiusSumSq = (int) Math.pow(ballView.getR() + circle.getR(), 2);

        if (radiusDiffSq <= xValueDiffSq + yValueDiffSq &&
                xValueDiffSq + yValueDiffSq <= radiusSumSq &&
                radiusDiffSq <= radiusSumSq) {
            intersects = true;
        }

        return intersects;

        //(R0-R1)^2 <= (x0-x1)^2+(y0-y1)^2 <= (R0+R1)^2
    }

    private void pulseRectangle (final Rectangle rect ) {
        final ScaleAnimation anim = new ScaleAnimation(1, 1.50f, 1f, 1.50f, rect.getCenterX(), rect.getCenterY());
        anim.setDuration(50);
        anim.setRepeatCount(2);
        anim.setRepeatMode(Animation.REVERSE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rect.startAnimation(anim);
            }
        });

    }

    private void pulseTextView (final TextView view ) {
        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .playOn(view);

    }

    private void pickNextSide() {
        //Make visible the next side

        int randomNumber = randomGenerator.nextInt(2);

        if (w == 0) {
            if (randomNumber == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rect[0].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[0]);

                        rect[1].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[1]);

                        rect[2].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[2]);
                    }
                });


                sideVisible = 1;
            }

            if (randomNumber == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rect[3].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[3]);

                        rect[4].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[4]);

                        rect[5].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[5]);
                    }
                });


                sideVisible = 2;
            }

        }
        else if (w > 0) {
            if (sideVisible == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rect[3].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[3]);

                        rect[4].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[4]);

                        rect[5].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[5]);
                    }
                });


                sideVisible = 2;
            }

            else if (sideVisible == 2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rect[0].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[0]);

                        rect[1].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[1]);

                        rect[2].setVisibility(View.VISIBLE);
                        pulseRectangle(rect[2]);
                    }
                });


                sideVisible = 1;
            }
        }
    }

    public void goToRedirectScreen()
    {
        //getting preferences
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey2", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore2", 0); //0 is the default value
        mScoreboard.setHighScore(highScore);

        //Sound when player dies
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.deathsound);
        mediaPlayer.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBallView.setColor(0xDEFF000F);

                rect[0].setFixedColor(0xDEFF000F);
                rect[1].setFixedColor(0xDEFF000F);
                rect[2].setFixedColor(0xDEFF000F);
                rect[3].setFixedColor(0xDEFF000F);
                rect[4].setFixedColor(0xDEFF000F);
                rect[5].setFixedColor(0xDEFF000F);
            }
        });


        Intent intent = new Intent(this, Redirect.class);

        if (mScoreboard.getScore() > highScore){
            mScoreboard.setHighScore(mScoreboard.getScore());
        }

        intent.putExtra("score", mScoreboard.getScore() + "");
        intent.putExtra("highScore", mScoreboard.getHighScore() + "");
        intent.putExtra("adsNumber", n);
        intent.putExtra("activityTag", 2);
        startActivity(intent);
    }

    public void timeRanOut()
    {
        //getting preferences
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey2", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore2", 0); //0 is the default value
        mScoreboard.setHighScore(highScore);

        //Sound when player dies
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.deathsound);
        mediaPlayer.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBallView.setColor(0xDEFF000F);

                rect[0].setFixedColor(0xDEFF000F);
                rect[1].setFixedColor(0xDEFF000F);
                rect[2].setFixedColor(0xDEFF000F);
                rect[3].setFixedColor(0xDEFF000F);
                rect[4].setFixedColor(0xDEFF000F);
                rect[5].setFixedColor(0xDEFF000F);
            }
        });

        Intent intent = new Intent(this, Redirect.class);

        if (mScoreboard.getScore() > highScore){
            mScoreboard.setHighScore(mScoreboard.getScore());
        }

        intent.putExtra("score", mScoreboard.getScore() + "");
        intent.putExtra("highScore", mScoreboard.getHighScore() + "");
        intent.putExtra("adsNumber", n);
        intent.putExtra("activityTag", 3);
        startActivity(intent);
    }

}//ShiftModeActivity

