package benyamephrem.tilt.UI;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import benyamephrem.tilt.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Redirect extends ActionBarActivity {

    String mScore;
    String mHighScore;
    boolean isLarge = false ,
            isNormal = false;
    TextView exclamationTextView;

    //For ad control
    int n;
    //To determine what activity started this activity
    int activityTag;

    //HighScore
    int highScore;

    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        Button rateButton= (Button) findViewById(R.id.rateButton);
        Button shareButton= (Button) findViewById(R.id.shareButton);
        Button mainMenuButton= (Button) findViewById(R.id.mainMenuButton);
        Button removeAdsButton= (Button) findViewById(R.id.removeAdsButton);

        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        TextView highScoreTextView = (TextView) findViewById(R.id.highScoreTextView);
        TextView newTextView = (TextView) findViewById(R.id.newTextView);
        TextView highScoreTitleTextView = (TextView) findViewById(R.id.highScoreTitleTextView);
        final TextView topTextView = (TextView) findViewById(R.id.topTextView);
        final TextView bottomTextView = (TextView) findViewById(R.id.bottomTextView);



        Intent intent = getIntent();
        mScore = intent.getStringExtra("score");
        mHighScore = intent.getStringExtra("highScore");
        n = intent.getIntExtra("adsNumber", 0);
        activityTag = intent.getIntExtra("activityTag", 0);



        //**************************Ads***********************

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3171635020408755/8456396225");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        n += 1;
        if (n % 3 == 0) {
            mInterstitialAd.setAdListener(new AdListener(){
                public void onAdLoaded(){
                    displayInterstitial();
                }
            });
        }





        //Check screen size
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {

            isLarge = true;

        };

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            exclamationTextView = (TextView) findViewById(R.id.exclamationTextView);
            isNormal = true;

        }

        if (activityTag == 0) {
            SharedPreferences prefs1 = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            highScore = prefs1.getInt("highScore", 0); //0 is the default value
        }
        if (activityTag == 1) {
            SharedPreferences prefs1 = this.getSharedPreferences("myPrefsKey1", Context.MODE_PRIVATE);
            highScore = prefs1.getInt("highScore1", 0); //0 is the default value
        }
        if (activityTag == 2 || activityTag == 3) {
            SharedPreferences prefs1 = this.getSharedPreferences("myPrefsKey2", Context.MODE_PRIVATE);
            highScore = prefs1.getInt("highScore2", 0); //0 is the default value
        }

        if (highScore < Integer.parseInt(mScore) && isNormal){
            exclamationTextView.setVisibility(View.VISIBLE);
            newTextView.setVisibility(View.VISIBLE);

            newTextView.setTextColor(0xFFFDFF00);
            exclamationTextView.setTextColor(0xFFFDFF00);
            highScoreTitleTextView.setTextColor(0xFFFDFF00);
            highScoreTextView.setTextColor(0xFFFDFF00);

        }
        else if (highScore < Integer.parseInt(mScore) && isLarge) {

            newTextView.setVisibility(View.VISIBLE);

            newTextView.setTextColor(0xFFFDFF00);
            highScoreTitleTextView.setTextColor(0xFFFDFF00);
            highScoreTextView.setTextColor(0xFFFDFF00);

        }
        else if (isNormal) {

            newTextView.setVisibility(View.INVISIBLE);
            exclamationTextView.setVisibility(View.INVISIBLE);

        }
        else if (isLarge) {

            newTextView.setVisibility(View.INVISIBLE);

        }


        //Setting TextView if player died due to time running out
        if (activityTag == 3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    topTextView.setText("T I M E");
                    bottomTextView.setText("IS UP !");
                }
            });
        }

        //**********setting preferences************
        if (activityTag == 0) {
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", Integer.parseInt(mHighScore));
            editor.apply();
        }

        if (activityTag == 1) {
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore1", Integer.parseInt(mHighScore));
            editor.apply();
        }

        if (activityTag == 2 || activityTag == 3) {
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey2", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore2", Integer.parseInt(mHighScore));
            editor.apply();
        }




        scoreTextView.setText(mScore);
        highScoreTextView.setText(mHighScore);


        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgain();

            }
        });

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
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

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateGameRedirect();
            }
        });

        removeAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAdsRedirect();
            }
        });



    }


    private void turnOffAdsRedirect() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=benyamephrem.tilt&hl=en")));
        } catch (ActivityNotFoundException anfe){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=benyamephrem.tilt&hl=en")));
        }
    }

    private void rateGameRedirect() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=benyamephrem.tilt&hl=en")));
        } catch (ActivityNotFoundException anfe){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=benyamephrem.tilt&hl=en")));
        }

    }

    public void goToMainMenu()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("adsNumber", n);
        startActivity(intent);
    }

    public void playAgain()
    {
        if (activityTag == 0) {
            Intent intent = new Intent(this, TraditionalMode.class);
            intent.putExtra("adsNumber", n);
            intent.putExtra("highScore", highScore);
            startActivity(intent);
        }

        if (activityTag == 1) {
            Intent intent = new Intent(this, ChaseMode.class);
            intent.putExtra("adsNumber", n);
            intent.putExtra("highScore", highScore);
            startActivity(intent);
        }

        if (activityTag == 2 || activityTag == 3) {
            Intent intent = new Intent(this, ShiftMode.class);
            intent.putExtra("adsNumber", n);
            intent.putExtra("highScore", highScore);
            startActivity(intent);
        }
    }

    public void displayInterstitial() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

}




