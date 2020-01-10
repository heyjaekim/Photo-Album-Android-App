package com.example.photolibraryapp;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class FlingGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private OpenPhoto context;

    public FlingGestureDetector(OpenPhoto context){
        this.context = context;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if ((Math.abs(e1.getX() - e2.getX()) >= 100) && (e1.getX() - e2.getX() <= 2000)) {
            if (e1.getX() - e2.getX() > 0) {
                this.context.nextPhoto();
            }else{
                this.context.lastPhoto();
            }

        }
        return false;
    }
}
