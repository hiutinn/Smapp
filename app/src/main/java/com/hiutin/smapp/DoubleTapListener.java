package com.hiutin.smapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class DoubleTapListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public DoubleTapListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private static class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            // Call the abstract method when a double tap event occurs
            onDoubleTapEvent(event);
            return true;
        }
    }

    // Abstract method to be implemented in the view class
    public abstract void onDoubleTapEvent(MotionEvent event);
}
