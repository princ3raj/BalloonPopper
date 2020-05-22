package com.example.balloonpopper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {
    private static final String TAG = "MainActivity";

    public static final int MIN_ANIMATION_DELAY=500;
    public static final int MAX_ANIMATION_DELAY=1500;

    public static final int MIN_ANIMATION_DURATION=1000;
    public static final int MAX_ANIMATION_DURATION=8000;

    private int mLevel;


    private ViewGroup mContentView;

    private int[] mBalloonColors= new int[3];

    private int mNextColor, mScreenWidth, mScreenHeight;
    private int mScore;

    private TextView mScoreDipslay, mLevelDisplay;

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

        updateDisplay();

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

    private void startLevel()
    {
        mLevel++;
        BalloonLauncher mBallonLauncher= new BalloonLauncher();
        mBallonLauncher.execute(mLevel);
    }

    public void goButtonClickHandler(View view) {
        startLevel();
        updateDisplay();
    }

    @Override
    public void popBallon(Balloon balloon, boolean userTouch) {

        mContentView.removeView(balloon);
        if (userTouch)
        {
            mScore++;
        }
        updateDisplay();

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
            while (balloonsLaunched < 3) {

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




