package com.example.balloonpopper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balloonpopper.utils.HighScoreHelper;
import com.example.balloonpopper.utils.SimpleAlertDialog;
import com.example.balloonpopper.utils.SoundHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {
    private static final String TAG = "MainActivity";

    private static final int MIN_ANIMATION_DELAY=500;
    private static final int MAX_ANIMATION_DELAY=1500;
    private static final int MIN_ANIMATION_DURATION=1000;
    private static final int MAX_ANIMATION_DURATION=8000;
    private static final int NUMBER_OF_PINS=5;
    private static final int BALLOONS_PER_LEVEL =10;
    private List<Balloon> mBalloons=new ArrayList<>();
    private Button mGoButton;
    private boolean mPlaying;
    private boolean mGameStopped=true;
    private SoundHelper mSoundHelper;




    private int mLevel,mScore, mPinsUsed;

    private List<ImageView> pinImages= new ArrayList<>();


    private ViewGroup mContentView;

    private int[] mBalloonColors= new int[3];

    private int mNextColor, mScreenWidth, mScreenHeight;


    private TextView mScoreDipslay, mLevelDisplay;
    private int mBalloonsPopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBalloonColors[0]= Color.argb(255,255,0,0);
        mBalloonColors[1]= Color.argb(255,0,255,0);
        mBalloonColors[2]= Color.argb(255,0,0,255);

        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);

//      Get background reference.
        mContentView = (ViewGroup) findViewById(R.id.activity_main);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver=mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth=mContentView.getWidth();
                    mScreenHeight=mContentView.getHeight();
                }
            });

        }

        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToFullScreen();
            }
        });

        mScoreDipslay=findViewById(R.id.score_display);
        mLevelDisplay=findViewById(R.id.level_display);
        pinImages.add((ImageView) findViewById(R.id.pushpin1));
        pinImages.add((ImageView) findViewById(R.id.pushpin2));
        pinImages.add((ImageView) findViewById(R.id.pushpin3));
        pinImages.add((ImageView) findViewById(R.id.pushpin4));
        pinImages.add((ImageView) findViewById(R.id.pushpin5));
        mGoButton=findViewById(R.id.go_button);


        updateDisplay();

        mSoundHelper = new SoundHelper(this);
        mSoundHelper.PrepareMusicPlayer(this);

//        mContentView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if(event.getAction()==MotionEvent.ACTION_UP)
//                {
//                    Balloon b=new Balloon(MainActivity.this, mBalloonColors[mNextColor],100);
//                    b.setX(event.getX());
//                    b.setY(mScreenHeight);
//                    mContentView.addView(b);
//                    b.releaseBalloon(mScreenHeight,3000);
//
//                    if (mNextColor+1==mBalloonColors.length)
//                    {
//                        mNextColor=0;
//                    }
//                    else
//                    {
//                        mNextColor++;
//                    }
//                }
//                return false;
//            }
//        });


    }


    // Set full screen mode
    private void setToFullScreen()
    {
        ViewGroup rootView=findViewById(R.id.activity_main);
        mContentView.setSystemUiVisibility(SYSTEM_UI_FLAG_LOW_PROFILE
                | SYSTEM_UI_FLAG_FULLSCREEN
                | SYSTEM_UI_FLAG_LAYOUT_STABLE
                | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void startGame()
    {
        setToFullScreen();
        mScore=0;
        mLevel=0;
        mPinsUsed=0;
        for (ImageView pin: pinImages)
        {
            pin.setImageResource(R.drawable.pin);
        }
        mGameStopped=false;
        startLevel();
        mSoundHelper.playMusic();
    }

    private void startLevel()
    {
        mLevel++;
        BalloonLauncher mBallonLauncher= new BalloonLauncher();
        mBallonLauncher.execute(mLevel);
        mPlaying=true;
        mBalloonsPopped=0;
        mGoButton.setText("stop game");
    }

    private void finishLevel()
    {
        Toast.makeText(this, String.format("you finished level %d",mLevel), Toast.LENGTH_SHORT).show();
        mPlaying=false;
        mGoButton.setText(String.format("start level %d",mLevel+1));

    }

    public void goButtonClickHandler(View view) {
        if (mPlaying)
        {
            gameOver(false);
        } else if (mGameStopped)
        {
            startGame();
        } else
        {
            startLevel();
            updateDisplay();
        }

    }

    @Override
    public void popBallon(Balloon balloon, boolean userTouch) {


        mBalloonsPopped++;
        mSoundHelper.playSound();
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (userTouch)
        {
            mScore++;
        } else
        {
            mPinsUsed++;
            if (mPinsUsed<=pinImages.size())
            {
                pinImages.get(mPinsUsed-1).setImageResource(R.drawable.pin_off);

            }
            if(mPinsUsed==NUMBER_OF_PINS)
            {
                gameOver(true);
                return;
            } else {
                Toast.makeText(this, "Missed that one", Toast.LENGTH_SHORT).show();
            } 
        }
        updateDisplay();
        if (mBalloonsPopped==BALLOONS_PER_LEVEL)
        {
           finishLevel();
        }

    }

    private void gameOver(boolean allPinsUsed) {
        mSoundHelper.pauseMusic();

        Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show();
        for (Balloon balloon: mBalloons)
        {
          mContentView.removeView(balloon);
          balloon.setPopped(true);
        }
        mBalloons.clear();
        mPlaying=false;
        mGameStopped=true;
        mGoButton.setText("Start game");

        if (allPinsUsed)
        {
            if (HighScoreHelper.isTopScore(this,mScore))
            {
                HighScoreHelper.setTopScore(this,mScore);
            }
            SimpleAlertDialog dialog= SimpleAlertDialog.newInstance("New High Score!",
                    String.format("Your new high score is %d", mScore));
            dialog.show(getSupportFragmentManager(),null);
        }
    }

    private void updateDisplay() {

       mScoreDipslay.setText(String.valueOf(mScore));
       mLevelDisplay.setText(String.valueOf(mLevel));

    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            Log.d(TAG, "doInBackground: "+level);
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (mPlaying && balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Balloon balloon = new Balloon(this, mBalloonColors[mNextColor], 150);
        mBalloons.add(balloon);
        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenHeight, duration);

    }
}




