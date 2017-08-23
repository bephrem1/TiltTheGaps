package benyamephrem.tilt.UI;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import benyamephrem.tilt.GameLogic.BallView;
import benyamephrem.tilt.GameLogic.Rectangle;
import benyamephrem.tilt.GameLogic.Scoreboard;
import benyamephrem.tilt.R;


public class TraditionalMode extends ActionBarActivity {

    BallView mBallView = null;
    Rectangle rectWhite1 = null
            , rectBlack1 = null
            , rectWhite2 = null
            , rectBlack2 = null
            , rectWhite3 = null
            , rectBlack3 = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr , mTmr2 = null;
    TimerTask mTsk , mTsk2 = null;
    static int mScrWidth, mScrHeight;
    static int mRectSpeed;
    static int mBallSpeed;
    Scoreboard mScoreboard = new Scoreboard();
    static android.graphics.PointF mBallPos, mBallSpd;
    TextView scoreTextView;
    TextView instructionsTextView;
    //For the point adding mechanism restriction
    int mNumber1 = 0;
    int mNumber2 = 0;
    int mNumber3 = 0;
    int backgroundFade = 5000000;
    //For controlling ad frequency
    int n;
    //Integer to control background change
    int w = 0;
    static boolean isNormalScreen = false,
            isLargeScreen = false;
    private FrameLayout mainView = null;
    private ObjectAnimator colorFade = null;

    public static int[] colorArray = {0xFF949CFF, 0xffeef16b, 0xFFFFAD85, 0xFFAFFF78, 0xFFE3ABFF,
            0xFF7DFFE0, 0xFFFEBC71, 0xFFA877FB, 0xFF62FF8B, 0xFFF99AA1, 0xFFA9FF53, 0xFFD02A21,
            0xFF1D1AD0, 0xFFCED07E, 0xFF60B4FF, 0xFFFFA1E0};
    public static Random randomGenerator = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        //set app to full screen and keep screen on
        getWindow().setFlags(0xFFFFFFFF, LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Pass on ad frequency control
        Intent intent1 = getIntent();
        n = intent1.getIntExtra("adsNumber", 0);

        //create pointer to main screen
        mainView = (FrameLayout) findViewById(R.id.main_view);


        //get screen dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScrWidth = metrics.widthPixels;
        mScrHeight = metrics.heightPixels;
        mBallPos = new PointF();
        mBallSpd = new PointF();

        //Set rectangle subtracted pixel amount
        mRectSpeed = mScrWidth / 300;


        //create variables for ball position and speed
        mBallPos.x = mScrWidth / 7;
        mBallPos.y = mScrHeight / 2;
        mBallSpd.x = 0;
        mBallSpd.y = 0;



        //create initial ball

        mBallView = new BallView(this, mBallPos.x, mBallPos.y, mScrWidth/30);

        //Set high score
        Intent intent = getIntent();
        mScoreboard.setHighScore(intent.getIntExtra("highScore", 0));


        //**************Create initial 6 rectangles****************
        rectWhite1 = new Rectangle(this, ((mScrWidth/18) * 17) + mScrWidth , 0 , ((mScrWidth/18) * 19) + mScrWidth , mScrHeight, 0xFFFFFFFF);
        rectBlack1 = new Rectangle(this, ((mScrWidth/18) * 17) + mScrWidth , (mScrHeight/20) * 7 ,((mScrWidth/18) * 19) + mScrWidth  ,(mScrHeight/20) * 13, 0xFF15FFD4);
        rectWhite2 = new Rectangle(this, ((mScrWidth/9) * 5) + mScrWidth , 0, ((mScrWidth/9) * 6) + mScrWidth , mScrHeight,0xFFFFFFFF);
        rectBlack2 = new Rectangle(this, ((mScrWidth/9) * 5) + mScrWidth , (mScrHeight/20) * 7, ((mScrWidth/9) * 6) + mScrWidth , (mScrHeight/20) * 13, 0xFF15FFD4);
        rectWhite3 = new Rectangle(this, ((mScrWidth/36) * 7) + mScrWidth , 0, ((mScrWidth/36) * 11) + mScrWidth , mScrHeight, 0xFFFFFFFF);
        rectBlack3 = new Rectangle(this, ((mScrWidth/36) * 7) + mScrWidth , (mScrHeight/20) * 7, ((mScrWidth/36) * 11) + mScrWidth , (mScrHeight/20) * 13 , 0xFF15FFD4);


        //*Invalidate makes the previous views under the view being drawn*

        mainView.addView(rectWhite1);
        rectWhite1.invalidate();

        mainView.addView(rectWhite2);
        rectWhite2.invalidate();

        mainView.addView(rectWhite3);
        rectWhite3.invalidate();

        mainView.addView(rectBlack1);
        rectBlack1.invalidate();

        mainView.addView(rectBlack2);
        rectBlack2.invalidate();

        mainView.addView(rectBlack3);
        rectBlack3.invalidate();

        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView

        //Random starting colors every time
        scoreTextView = (TextView)findViewById(R.id.scoreTextView);
        //Instructions text view
        instructionsTextView = (TextView)findViewById(R.id.instructionsTextView);

        int randomNumber = randomGenerator.nextInt(colorArray.length);
        final int color = colorArray[randomNumber];

        rectBlack1.setFixedColor(color);
        rectBlack2.setFixedColor(color);
        rectBlack3.setFixedColor(color);
        mBallView.setColor(color);
        scoreTextView.post(new Runnable() {
            public void run() {
                scoreTextView.setTextColor(color);
            }
        });
        instructionsTextView.post(new Runnable() {
            public void run() {
                instructionsTextView.setTextColor(color);
            }
        });
        mainView.setBackgroundColor(color - backgroundFade);

        mTmr2 = new Timer();
        mTsk2 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        instructionsTextView.setVisibility(View.INVISIBLE);
                    }
                });
            }
        };
        mTmr2.schedule(mTsk2, 3000);




        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        //set ball speed based on phone tilt (ignore Z axis)
                        mBallSpd.x = -event.values[0] * mBallSpeed;
                        mBallSpd.y = event.values[1] * mBallSpeed;
                        //timer event will redraw ball
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);

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


    } //OnCreate

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        scoreTextView = (TextView)findViewById(R.id.scoreTextView);

        mainView = (FrameLayout) findViewById(R.id.main_view);


        final int[] blackRectangleTopHeights = {0, (mScrHeight/10), (mScrHeight/10) * 2,(mScrHeight/10) * 3,
                                                   (mScrHeight/10) * 4,(mScrHeight/10) * 5,(mScrHeight/10) * 6,
                                                   (mScrHeight/10) * 7};

        final int[] blackRectangleBottomHeights = {(mScrHeight/10) * 3,(mScrHeight/10) * 4,(mScrHeight/10) * 5,
                                                  (mScrHeight/10) * 6, (mScrHeight/10) * 7,(mScrHeight/10) * 8,
                                                  (mScrHeight/10) * 9,mScrHeight};
        final Random randomGenerator = new Random();

        //Add point sound
        final MediaPlayer mediaPlayer1 = MediaPlayer.create(this, R.raw.blopsound);


        runCircle();

        //************Set up rectangle moving mechanism and Logic*********************

      mTmr = new Timer();
        mTsk = new TimerTask() {
            @Override
            public void run() {

                int randomNumber = randomGenerator.nextInt(blackRectangleTopHeights.length);
                int topBlackRectHeight = blackRectangleTopHeights[randomNumber];
                int bottomBlackRectHeight = blackRectangleBottomHeights[randomNumber];

                //Moves rectangles left across the screen
                int rectWhite1Left = rectWhite1.getTheLeft() - mRectSpeed;
                int rectWhite1Right = rectWhite1.getTheRight() - mRectSpeed;
                rectWhite1.setX(rectWhite1Left, rectWhite1Right);

                int rectBlack1Left = rectBlack1.getTheLeft() - mRectSpeed;
                int rectBlack1Right = rectBlack1.getTheRight() - mRectSpeed;
                rectBlack1.setX(rectBlack1Left, rectBlack1Right);

                int rectWhite2Left = rectWhite2.getTheLeft() - mRectSpeed;
                int rectWhite2Right = rectWhite2.getTheRight() - mRectSpeed;
                rectWhite2.setX(rectWhite2Left, rectWhite2Right);

                int rectBlack2Left = rectBlack2.getTheLeft() - mRectSpeed;
                int rectBlack2Right = rectBlack2.getTheRight() - mRectSpeed;
                rectBlack2.setX(rectBlack2Left, rectBlack2Right);

                int rectWhite3Left = rectWhite3.getTheLeft() - mRectSpeed;
                int rectWhite3Right = rectWhite3.getTheRight() - mRectSpeed;
                rectWhite3.setX(rectWhite3Left, rectWhite3Right);

                int rectBlack3Left = rectBlack3.getTheLeft() - mRectSpeed;
                int rectBlack3Right = rectBlack3.getTheRight() - mRectSpeed;
                rectBlack3.setX(rectBlack3Left, rectBlack3Right);


                //Cycles rectangles when they hit left side of the screen
                // Randomizes where middle rectangle will be postitoned vertically
                if (rectWhite1.getTheRight() > 0 && rectWhite1.getTheRight() < 20) {
                    rectWhite1.setX(mScrWidth, rectWhite1.getRectWidth() + mScrWidth);
                }

                if (rectBlack1.getTheRight() > 0 && rectBlack1.getTheRight() < 20) {
                    rectBlack1.setX(mScrWidth, rectBlack1.getRectWidth() + mScrWidth);
                    rectBlack1.setY(topBlackRectHeight, bottomBlackRectHeight);
                }

                if (rectWhite2.getTheRight() > 0 && rectWhite2.getTheRight() < 20) {
                    rectWhite2.setX(mScrWidth, rectWhite2.getRectWidth() + mScrWidth);
                }

                if (rectBlack2.getTheRight() > 0 && rectBlack2.getTheRight() < 20) {
                    rectBlack2.setX(mScrWidth, rectBlack2.getRectWidth() + mScrWidth);
                    rectBlack2.setY(topBlackRectHeight, bottomBlackRectHeight);
                }

                if (rectWhite3.getTheRight() > 0 && rectWhite3.getTheRight() < 20) {
                    rectWhite3.setX(mScrWidth, rectWhite3.getRectWidth() + mScrWidth);
                }

                if (rectBlack3.getTheRight() > 0 && rectBlack3.getTheRight() < 20) {
                    rectBlack3.setX(mScrWidth, rectBlack3.getRectWidth() + mScrWidth);
                    rectBlack3.setY(topBlackRectHeight, bottomBlackRectHeight);
                }


                //*********Keeps track of score and update TextView with score*************
                if (intersects(mBallView, rectBlack1)) {
                    mNumber1 += 1;
                    if(mNumber1 == 1 && mBallView.getRectTag().equals("Rectangle3")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScoreboard.addPoint();
                                scoreTextView.setText(mScoreboard.getScore() + "");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(scoreTextView);
                                    }
                                });
                            }
                        });
                        mediaPlayer1.start();
                        mBallView.setRectTag("Rectangle1");
                    }

                } else if(mBallView.getX() > rectBlack1.getTheLeft() && mBallView.getX() < rectBlack1.getTheRight()) {
                    mBallView.setRectTag("Rectangle1");
                }


                if (intersects(mBallView, rectBlack2)) {
                    mNumber2 += 1;
                    if(mNumber2 == 1 && mBallView.getRectTag().equals("Rectangle2")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScoreboard.addPoint();
                                scoreTextView.setText(mScoreboard.getScore() + "");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(scoreTextView);
                                    }
                                });
                            }
                        });
                        mediaPlayer1.start();
                        mBallView.setRectTag("Rectangle3");
                    }

                } else if(mBallView.getX() > rectBlack2.getTheLeft() && mBallView.getX() < rectBlack2.getTheRight()) {
                    mBallView.setRectTag("Rectangle3");
                }


                if (intersects(mBallView, rectBlack3)) {
                    mNumber3 += 1;
                    if(mNumber3 == 1 && mBallView.getRectTag().equals("Rectangle1")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScoreboard.addPoint();
                                scoreTextView.setText(mScoreboard.getScore() + "");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YoYo.with(Techniques.Bounce)
                                                .duration(400)
                                                .playOn(scoreTextView);
                                    }
                                });
                            }
                        });
                        mediaPlayer1.start();
                        mBallView.setRectTag("Rectangle2");
                    }

                } else if(mBallView.getX() > rectBlack3.getTheLeft() && mBallView.getX() < rectBlack3.getTheRight()) {
                    mBallView.setRectTag("Rectangle2");
                }


                if (!intersects(mBallView, rectBlack1)) {
                    mNumber1 = 0;
                }
                if (!intersects(mBallView, rectBlack2)) {
                    mNumber2 = 0;
                }
                if (!intersects(mBallView, rectBlack3)) {
                    mNumber3 = 0;
                }


                //Speeds up game correspondingly to the score
                if (isNormalScreen) {

                    mBallSpeed = 3;

                    if (mScoreboard.getScore() == 10) {
                        mRectSpeed = mScrWidth / 270;
                        mBallSpeed = 3;
                    } else if (mScoreboard.getScore() == 20) {
                        mRectSpeed = mScrWidth / 240;
                        mBallSpeed = 4;
                    } else if (mScoreboard.getScore() == 30) {
                        mRectSpeed = mScrWidth / 210;
                    } else if (mScoreboard.getScore() == 40) {
                        mRectSpeed = mScrWidth / 180;
                    } else if (mScoreboard.getScore() == 50) {
                        mRectSpeed = mScrWidth / 160;
                    }
                }
                else if (isLargeScreen) {

                    mBallSpeed = 1;

                    if (mScoreboard.getScore() == 10) {
                        mRectSpeed = mScrWidth / 270;
                    } else if (mScoreboard.getScore() == 20) {
                        mRectSpeed = mScrWidth / 240;
                    } else if (mScoreboard.getScore() == 30) {
                        mRectSpeed = mScrWidth / 210;
                    } else if (mScoreboard.getScore() == 40) {
                        mRectSpeed = mScrWidth / 180;
                    } else if (mScoreboard.getScore() == 50) {
                        mRectSpeed = mScrWidth / 160;
                    }

                }

                if (mScoreboard.getScore() % 5 == 0 && mScoreboard.getScore() != 0 && w == 0){
                    Rectangle.setColor(rectBlack1, rectBlack2, rectBlack3, mBallView, scoreTextView);

                        backgroundColorswitch(rectBlack1);

                    w += 1;
                }

                if (((mScoreboard.getScore() + 1) % 5 == 0)) {
                    w = 0;
                }



                //Collision detection statements
                if(intersects(mBallView, rectWhite1) && !intersects(mBallView, rectBlack1)){
                    goToRedirectScreen();

                }
                if(intersects(mBallView, rectWhite2) && !intersects(mBallView, rectBlack2)){
                    goToRedirectScreen();
                }
                if(intersects(mBallView, rectWhite3) && !intersects(mBallView, rectBlack3)){
                    goToRedirectScreen();
                }

                runCircle();

                RedrawHandler.post(new Runnable() {
                    public void run() {
                        rectWhite1.invalidate();
                        rectWhite2.invalidate();
                        rectWhite3.invalidate();
                        rectBlack1.invalidate();
                        rectBlack2.invalidate();
                        rectBlack3.invalidate();
                    }
                });



            }

        };
        mTmr.schedule(mTsk, 10, 10); //start timer (Timer 2)


        super.onResume();
    } // onResume

    @Override
    public void onPause() //app moved to background, stop background threads
    {
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;

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

     /*Disable screen buttons so user doesn't exit game*/

    private void backgroundColorswitch(Rectangle rect) {
        Drawable background = mainView.getBackground();
        int color = ((ColorDrawable) background).getColor();
        int red =   (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue =  (color) & 0xFF;
        int alpha = (color >> 24) & 0xFF;
        colorFade = ObjectAnimator.ofObject(mainView, "backgroundColor", new ArgbEvaluator(), Color.argb(alpha, red, green, blue), rect.getColor() - backgroundFade); //Flashes to 4th parameter and settles on 5th parameter
        colorFade.setDuration(2000); //length of transition
        mainView.post(new Runnable() {
            @Override
            public void run() {
                colorFade.start(); //Runs the animation
            }
        });
    }

    private void runCircle() {

        //move ball based on current speed
                mBallPos.x += mBallSpd.y;
                mBallPos.y += -mBallSpd.x;

                //if ball goes off screen, reposition to opposite side of screen
                if(mScoreboard.getScore() > 3) {
                    if (mBallPos.x > mScrWidth) mBallPos.x = mScrWidth - 5;
                    if (mBallPos.y > mScrHeight) mBallPos.y = mScrHeight - 5;
                    if (mBallPos.x < 0) mBallPos.x = 5;
                    if (mBallPos.y < 0) mBallPos.y = 5;
                }
                else {
                    if (mBallPos.x > mScrWidth) mBallPos.x = mScrWidth - 5;
                    if (mBallPos.y > mScrHeight) mBallPos.y = 0;
                    if (mBallPos.x < 0) mBallPos.x = 5;
                    if (mBallPos.y < 0) mBallPos.y = mScrHeight;
                }

                //update ball class instance
                mBallView.x = mBallPos.x;
                mBallView.y = mBallPos.y;

            //redraw ball. Must run in background thread to prevent thread lock.
                RedrawHandler.post(new Runnable() {
                    public void run() {
                        mBallView.invalidate();
                    }
                });

    }

    public void goToRedirectScreen()
    {
        //getting preferences
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0); //0 is the default value
        mScoreboard.setHighScore(highScore);

        rectBlack1.setFixedColor(0xFFFF0015);
        rectBlack2.setFixedColor(0xFFFF0015);
        rectBlack3.setFixedColor(0xFFFF0015);
        rectWhite1.setFixedColor(0xFFFF0015);
        rectWhite2.setFixedColor(0xFFFF0015);
        rectWhite3.setFixedColor(0xFFFF0015);

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
        intent.putExtra("activityTag", 0);
        startActivity(intent);
    }

    public boolean intersects(BallView ball, Rectangle rect){
        boolean intersects = false;
        int ballRadius = ball.getR();
        int ballY = (int) ball.getY();
        int ballX = (int) ball.getX();

        if (ballX + ballRadius >= rect.getTheLeft() &&
                ballX - ballRadius <= rect.getTheRight() &&
                ballY + ballRadius <= rect.getTheBottom() &&
                ballY - ballRadius >= rect.getTheTop())
        {
            intersects = true;
        }

        return intersects;

    }

} //TiltBallActivity


