package com.example.balloonpopper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.balloonpopper.utils.PixelHelper;

@SuppressLint("AppCompatCustomView")
public class Balloon extends ImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "Balloon";

    private BalloonListener mBalloonListener;

    private boolean mPopped;


    private ValueAnimator mAnimator;

    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context, int color, int rawHeight) {
        super(context);
        mBalloonListener= (BalloonListener) context;

        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        int rawWidth=rawHeight/2;

        int dpHeight= PixelHelper.pixelsToDp(rawHeight,context);
        int dpWidth=PixelHelper.pixelsToDp(rawWidth,context);

        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(dpWidth,dpHeight);
        setLayoutParams(params);
    }

    public void releaseBalloon(int screenHeight, int duration)
    {
        mAnimator=new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight,0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();



    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

        if(!mPopped)
        {
            mBalloonListener.popBallon(this,false);
        }

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        Log.d(TAG, "onAnimationUpdate: start");

        setY((float) animation.getAnimatedValue());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mPopped && event.getAction()==MotionEvent.ACTION_DOWN)
        {
            mBalloonListener.popBallon(this,true);
            mPopped=true;
            mAnimator.cancel();
        }
        return super.onTouchEvent(event);
    }

    public interface BalloonListener
    {
        void popBallon(Balloon balloon, boolean userTouch);

    }

}
