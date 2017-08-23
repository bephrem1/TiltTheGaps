package benyamephrem.tilt.UI;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

import benyamephrem.tilt.GameLogic.BallView;
import benyamephrem.tilt.R;
import benyamephrem.tilt.Fragments.AccelerometerFragment;
import benyamephrem.tilt.Fragments.InstructionsFragment;


public class MainActivity extends ActionBarActivity {

    private MediaPlayer mediaPlayer;
    android.graphics.PointF mBallPos, mBallSpd;
    int mScrWidth, mScrHeight;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    BallView mBallView = null;

    RelativeLayout mainView;
    InterstitialAd mInterstitialAd;

    int n = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button playGameButton = (Button) findViewById(R.id.playGameButton);
        final Button turnOffAdsButton = (Button) findViewById(R.id.turnOffAdsButton);
        final Button shareButton = (Button) findViewById(R.id.shareButton);
        final Button rateButton = (Button) findViewById(R.id.rateButton);

        final TextView musicTextView = (TextView) findViewById(R.id.musicTextView);
        final TextView instructionsTextView = (TextView) findViewById(R.id.instructionsTextView);
        final TextView titleTextView = (TextView) findViewById(R.id.titleTextView);


        Intent intent = getIntent();
        n = intent.getIntExtra("adsNumber", 0);

        mainView = (RelativeLayout) findViewById(R.id.game_title);

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

        mBallView = new BallView(this, mBallPos.x, mBallPos.y, mScrWidth / 30);
        mBallView.setColor(0xFFFFFFFF);

        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView


        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        //set ball speed based on phone tilt (ignore Z axis)
                        mBallSpd.x = -event.values[0] * 3;
                        mBallSpd.y = event.values[1] * 3;
                        //timer event will redraw ball
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_NORMAL);

        //Check if device has an accelerometer
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            runCircle(); //Begins sampling
        } else {
            showAccelerometerFragment();
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.chopin);
        mediaPlayer.setLooping(true);

        mediaPlayer.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.Wobble)
                        .duration(3000)
                        .playOn(titleTextView);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn)
                        .duration(2000)
                        .playOn(playGameButton);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn)
                        .duration(2000)
                        .playOn(shareButton);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn)
                        .duration(2000)
                        .playOn(turnOffAdsButton);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn)
                        .duration(2000)
                        .playOn(rateButton);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.RotateInUpLeft)
                        .duration(1000)
                        .playOn(musicTextView);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.RotateInDownRight)
                        .duration(1000)
                        .playOn(instructionsTextView);
            }
        });


        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this,  playGameButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle item selection
                        switch (item.getItemId()) {
                            case R.id.traditional:
                                goToTraditionalScreen();
                                return true;
                            case R.id.chase:
                                goToChaseScreen();
                                return true;
                            case R.id.shift:
                                goToShiftScreen();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show(); //showing popup menu
            }


        });

        musicTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    //stop or pause your media player mediaPlayer.stop(); or mediaPlayer.pause();
                    mediaPlayer.pause();
                    musicTextView.setText("Turn Music On");

                } else {
                    mediaPlayer.start();
                    musicTextView.setText("Turn Music Off");

                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sharing_string) + "\n\n https://play.google.com/store/apps/details?id=benyamephrem.tilt&hl=en");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        instructionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionFragment();
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rateGameRedirect();
            }
        });

        turnOffAdsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOffAdsRedirect();
            }
        });



        //**************************Ads***********************

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3171635020408755/6979663022");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        n += 1;
        if (n % 4 == 0) {
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    displayInterstitial();
                }
            });
        }

    }


    @Override
    public void onPause() //app moved to background, stop background threads
    {
        super.onPause();
        mediaPlayer.release();
    }

    private void turnOffAdsRedirect() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=benyamephrem.tilt&hl=en")));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=benyamephrem.tilt&hl=en")));
        }
    }

    private void rateGameRedirect() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=benyamephrem.tilt&hl=en")));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=benyamephrem.tilt&hl=en")));
        }

    }


    public void goToTraditionalScreen() {
        Intent intent = new Intent(this, TraditionalMode.class);
        intent.putExtra("adsNumber", n);
        startActivity(intent);

    }

    public void goToChaseScreen() {
        Intent intent = new Intent(this, ChaseMode.class);
        intent.putExtra("adsNumber", n);
        startActivity(intent);

    }

    public void goToShiftScreen() {
        Intent intent = new Intent(this, ShiftMode  .class);
        intent.putExtra("adsNumber", n);
        startActivity(intent);

    }


    private void showInstructionFragment() {
        InstructionsFragment dialog = new InstructionsFragment();
        dialog.show(getFragmentManager(), "instruction_dialog");
    }

    private void showAccelerometerFragment() {
        AccelerometerFragment dialog = new AccelerometerFragment();
        dialog.show(getFragmentManager(), "instruction_dialog");
    }

    private void runCircle() {

        //create timer to move ball to new position
        mTmr = new Timer();
        mTsk = new TimerTask() {
            public void run() {

                //move ball based on current speed
                mBallPos.x += mBallSpd.y;
                mBallPos.y += -mBallSpd.x;

                //if ball goes off screen, reposition to opposite side of screen
                if (mBallPos.x > mScrWidth) mBallPos.x = 0;
                if (mBallPos.y > mScrHeight) mBallPos.y = 0;
                if (mBallPos.x < 0) mBallPos.x = mScrWidth;
                if (mBallPos.y < 0) mBallPos.y = mScrHeight;


                //update ball class instance
                mBallView.x = mBallPos.x;
                mBallView.y = mBallPos.y;

                //redraw ball. Must run in background thread to prevent thread lock.
                RedrawHandler.post(new Runnable() {
                    public void run() {
                        mBallView.invalidate();
                    }
                });


            } //run
        }; // TimerTask

        mTmr.schedule(mTsk, 0, 10); //start timer (Timer)
    }

    public void displayInterstitial() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}



