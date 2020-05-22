package com.example.balloonpopper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mContentView;

    private int[] mBalloonColors= new int[3];

    private int mNextColor, mScreenWidth, mScreenHeight;

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

        mContentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    Balloon b=new Balloon(MainActivity.this, mBalloonColors[mNextColor],100);
                    b.setX(event.getX());
                    b.setY(mScreenHeight);
                    mContentView.addView(b);
                    b.releaseBalloon(mScreenHeight,3000);

                    if (mNextColor+1==mBalloonColors.length)
                    {
                        mNextColor=0;
                    }
                    else
                    {
                        mNextColor++;
                    }
                }
                return false;
            }
        });


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
}




