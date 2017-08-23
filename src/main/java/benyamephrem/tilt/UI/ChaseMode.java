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
import benyamephrem.tilt.GameLogic.Scoreboard;
import benyamephrem.tilt.R;


public class ChaseMode extends ActionBarActivity {

    android.graphics.PointF mBallPos, mBallSpd;
    int mScrWidth, mScrHeight;
    Handler mHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr;
    TimerTask mTsk = null;
    BallView mBallView = null, mChaseBall = null;
    Circle mStartCircle = null;
    Circle[] circle = new Circle[8];
    Scoreboard mScoreboard = new Scoreboard();
    int circleVisible;
    public static Random randomGenerator = new Random();
    //Add point sound
    MediaPlayer mediaPlayer;


    //Array of sampled values
    float[] ballXValues = new float[15000]; //Game will not exceed 2.5 minutes (100 samples per second for 150 seconds)
    float[] ballYValues = new float[15000]; //Game will not exceed 2.5 minutes (100 samples per second for 150 seconds)
    int numberOfCycles = 0;
    //Offset of sampled values from player's ball current position
    int pastValueSampleSpeed = 50;

    //Game timer varibles
    TextView gameScoreBoard;
    //Only lets timer start once
        //For picking what runs before and after initial start game circle is pierced
        int w = 0;
        //For adding score and picking next visible circle
        int g = 0;

    //Ad frequency number
    int n;

    //Prevents same circle from popping up
    int lastPickedNumber = 3;

    //Screen size varibles
    boolean isNormalScreen = false, isLargeScreen = false;

    //Background
    FrameLayout mainView;

    //Integer for color
    int ballAndCircleColor;
    int backgroundFade = 5000000;

    //Color array
    public static int[] colorArray = {0xFF949CFF, 0xffeef16b, 0xFFFFAD85, 0xFFAFFF78, 0xFFE3ABFF,
            0xFF7DFFE0, 0xFFFEBC71, 0xFFA877FB, 0xFF62FF8B, 0xFFF99AA1, 0xFFA9FF53,
            0xFFCED07E, 0xFF60B4FF, 0xFFFFA1E0};


    /*Faded Blue, yellow, salmon, green, pink-red, light blue, light orange, purple, teal, pink,
    light-green, sand, lighter blue, pink*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chase_mode);

        //Pass on ad frequency control
        Intent intent1 = getIntent();
        n = intent1.getIntExtra("adsNumber", 0);

        //Set high score
        Intent intent = getIntent();
        mScoreboard.setHighScore(intent.getIntExtra("highScore", 0));

        //Initialize media player
        mediaPlayer = MediaPlayer.create(this, R.raw.blopsound);

        mainView = (FrameLayout) findViewById(R.id.main_view);
        gameScoreBoard = (TextView) findViewById(R.id.scoreBoard);


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

        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView


        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (isNormalScreen) {
                            //set ball speed based on phone tilt (ignore Z axis)
                            mBallSpd.x = -event.values[0] * 4;
                            mBallSpd.y = event.values[1] * 4;
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



        //Create initial 9 scoring circles and make them invisible
        circle[0] = new Circle(this, mScrWidth / 7, mScrHeight / 6, mScrWidth/15);
        mainView.addView(circle[0]);
        circle[0].invalidate();
        circle[0].setVisibility(View.INVISIBLE);

        circle[1] = new Circle(this, mScrWidth / 7, mScrHeight / 2, mScrWidth/15);
        mainView.addView(circle[1]);
        circle[1].invalidate();
        circle[1].setVisibility(View.INVISIBLE);

        circle[2] = new Circle(this, mScrWidth / 7, (mScrHeight/6) * 5 , mScrWidth/15);
        mainView.addView(circle[2]);
        circle[2].invalidate();
        circle[2].setVisibility(View.INVISIBLE);

        circle[3] = new Circle(this, mScrWidth / 2, mScrHeight / 2, mScrWidth/15);
        mainView.addView(circle[3]);
        circle[3].invalidate();
        circle[3].setVisibility(View.INVISIBLE);

        circle[4] = new Circle(this, mScrWidth / 2, (mScrHeight/6) * 5 , mScrWidth/15);
        mainView.addView(circle[4]);
        circle[4].invalidate();
        circle[4].setVisibility(View.INVISIBLE);

        circle[5] = new Circle(this, (mScrWidth/7) * 6 , mScrHeight / 6, mScrWidth/15);
        mainView.addView(circle[5]);
        circle[5].invalidate();
        circle[5].setVisibility(View.INVISIBLE);

        circle[6] = new Circle(this, (mScrWidth/7) * 6 , mScrHeight / 2, mScrWidth/15);
        mainView.addView(circle[6]);
        circle[6].invalidate();
        circle[6].setVisibility(View.INVISIBLE);

        circle[7] = new Circle(this, (mScrWidth/7) * 6 , (mScrHeight/6) * 5 , mScrWidth/15);
        mainView.addView(circle[7]);
        circle[7].invalidate();
        circle[7].setVisibility(View.INVISIBLE);


        //Set beginning color of TextView, Circle array, Ballview, and mainView
        setColors(gameScoreBoard, circle, mainView, mBallView);

    } //OnCreate


    @Override
    public void onResume() //app moved to background, stop background threads ****HAndle this so game resumes***
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

                    //Create initial red chase ball
                    mChaseBall = new BallView(getBaseContext(), mScrWidth / 2, mScrHeight / 2, mScrWidth/30);
                    mChaseBall.setColor(0xDEFF0600);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainView.addView(mChaseBall);
                            mChaseBall.invalidate();
                        }
                    });

                    mediaPlayer.start();

                    //Make visible the first circle
                    pickNextCircle();

                    w += 1;

                }

                //Once initial circle has been pierced
                if (w >= 1) {
                    //Make other circle visible once point is scored
                    if (circleBallViewIntersect(circle[circleVisible], mBallView)) {
                        final int circleVisiblePriviously = circleVisible;
                        if (g == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addPoint();
                                    YoYo.with(Techniques.Bounce)
                                            .duration(400)
                                            .playOn(gameScoreBoard);
                                    pulseCircle(circle[circleVisiblePriviously]);
                                    circle[circleVisiblePriviously].setVisibility(View.INVISIBLE);
                                }
                            });
                            mediaPlayer.start();

                            pickNextCircle();
                            g += 1;

                        }

                    }

                }

                //Start sampling circle position values
                sampleValues(mBallView);

                //Start chasing mechanism ******Make sure negative values cannot be sampled******
                if (mChaseBall != null) {
                    if (numberOfCycles >= 50) {
                        mChaseBall.setX(ballXValues[numberOfCycles - pastValueSampleSpeed]);
                        mChaseBall.setY(ballYValues[numberOfCycles - pastValueSampleSpeed]);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChaseBall.invalidate();
                            }
                        });
                    }
                }

                //Increase game difficulty be increasing sampling score
                if (mScoreboard.getScore() == 5) {
                    pastValueSampleSpeed = 40;
                }
                else if (mScoreboard.getScore() == 10) {
                    pastValueSampleSpeed = 33;
                }
                else if (mScoreboard.getScore() == 15) {
                    pastValueSampleSpeed = 26;
                }
                else if (mScoreboard.getScore() == 20) {
                    pastValueSampleSpeed = 19;
                }
                else if (mScoreboard.getScore() == 25) {
                    pastValueSampleSpeed = 12;
                }
                /*If pastValueSampleSpeed is greater than 10 player can't live*/


                //GAME OVER if the player's ball touches red ball
                if (mChaseBall != null) {
                    if (ballViewsIntersect(mBallView, mChaseBall)) {
                        mBallView.setColor(0xDEFF0600);
                        goToRedirectScreen();
                    }
                }

                //Reset the timer control integers
                if (!circleBallViewIntersect(circle[circleVisible], mBallView)) {
                    g = 0;
                }


                //Integer of number of times this code has cycled
                numberOfCycles += 1;
            }
        };
        mTmr.schedule(mTsk, 0, 10);

        super.onResume();
    }// OnResume

    @Override
    public void onPause() //app moved to background, stop background threads ****HAndle this so game resumes***
    {
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;

        super.onPause();
    }

    @Override
     public void onBackPressed() {
        // do nothing.
    }

    private void pickNextCircle() {
        //Make visible the next circle
        final int randomNumber = randomGenerator.nextInt(circle.length);
        if (randomNumber == lastPickedNumber) {
            final int randomNumber1 = randomGenerator.nextInt(circle.length);

            if (randomNumber1 == lastPickedNumber) {
                final int randomNumber2 = randomGenerator.nextInt(circle.length);

                if (randomNumber2 == lastPickedNumber) {
                    final int randomNumber3 = randomGenerator.nextInt(circle.length);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circle[randomNumber3].setVisibility(View.VISIBLE);
                            pulseCircle(circle[randomNumber3]);

                        }
                    });
                    circleVisible = randomNumber3;

                    lastPickedNumber = randomNumber3;
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circle[randomNumber2].setVisibility(View.VISIBLE);
                            pulseCircle(circle[randomNumber2]);

                        }
                    });
                    circleVisible = randomNumber2;

                    lastPickedNumber = randomNumber2;
                }
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circle[randomNumber1].setVisibility(View.VISIBLE);
                        pulseCircle(circle[randomNumber1]);

                    }
                });
                circleVisible = randomNumber1;

                lastPickedNumber = randomNumber1;
            }
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    circle[randomNumber].setVisibility(View.VISIBLE);
                    pulseCircle(circle[randomNumber]);

                }
            });
            circleVisible = randomNumber;

            lastPickedNumber = randomNumber;
        }
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

    private boolean ballViewsIntersect(BallView ballOne, BallView ballTwo) {
        boolean intersects = false;

        int radiusDiffSq = (int) Math.pow(ballOne.getR() - ballTwo.getR(), 2) ;
        float xValueDiffSq = (float) Math.pow(ballOne.getX() - ballTwo.getX(), 2);
        float yValueDiffSq = (float) Math.pow(ballOne.getY() - ballTwo.getY(), 2);
        int radiusSumSq = (int) Math.pow(ballOne.getR() + ballTwo.getR(), 2);

        if (radiusDiffSq <= xValueDiffSq + yValueDiffSq &&
                xValueDiffSq + yValueDiffSq <= radiusSumSq &&
                radiusDiffSq <= radiusSumSq) {
            intersects = true;
        }

        return intersects;

        //(R0-R1)^2 <= (x0-x1)^2+(y0-y1)^2 <= (R0+R1)^2
    }

    private void pulseCircle(final Circle circle) {
        final ScaleAnimation anim = new ScaleAnimation(1, 1.20f, 1f, 1.20f, circle.getX(), circle.getY());
        anim.setDuration(50);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circle.startAnimation(anim);
            }
        });

    }

    private void addPoint() {
        mScoreboard.addPoint();
        gameScoreBoard.setText(mScoreboard.getScore() + "");
    }

    private void sampleValues(BallView ball) {
        ballXValues[numberOfCycles] = ball.getX();
        ballYValues[numberOfCycles] = ball.getY();
    }

    private void setColors(final TextView gameScoreBoard, Circle[] circle, FrameLayout background, BallView ball) {
        int randomNumber = randomGenerator.nextInt(colorArray.length);
        final int color = colorArray[randomNumber];
        ballAndCircleColor = color;

        gameScoreBoard.post(new Runnable() {
            public void run() {
                gameScoreBoard.setTextColor(ballAndCircleColor);
            }
        });
        ball.setColor(ballAndCircleColor);
        background.setBackgroundColor(ballAndCircleColor - backgroundFade);

        int integer = 0;
        while (integer < 8) {
            circle[integer].setColor(ballAndCircleColor);
            integer += 1;
        }

    }

    public void goToRedirectScreen()
    {
        //getting preferences
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey1", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore1", 0); //0 is the default value
        mScoreboard.setHighScore(highScore);

        //Sound when player dies
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.deathsound);
        mediaPlayer.start();

        Intent intent = new Intent(this, Redirect.class);

        if (mScoreboard.getScore() > highScore){
            mScoreboard.setHighScore(mScoreboard.getScore());
        }

        intent.putExtra("score", mScoreboard.getScore() + "");
        intent.putExtra("highScore", mScoreboard.getHighScore() + "");
        intent.putExtra("adsNumber", n);
        intent.putExtra("activityTag", 1);
        startActivity(intent);
    }

}
