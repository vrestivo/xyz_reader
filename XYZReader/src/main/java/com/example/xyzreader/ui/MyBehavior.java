package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by devbox on 4/10/17.
 */

public class MyBehavior extends CoordinatorLayout.Behavior<Toolbar> {

    private final String LOG_TAG = "MyBehavior";

    public MyBehavior() {
        super();
        Log.v(LOG_TAG, "_in Constructor");
    }


    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v(LOG_TAG, "_in Constructor");
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Toolbar child, View dependency) {
        //return super.layoutDependsOn(parent, child, dependency);
        boolean result = dependency instanceof ImageView;
        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + String.valueOf(result));



        return result;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Toolbar child, View dependency) {
        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + child.getTag());

        return super.onDependentViewChanged(parent, child, dependency);
    }
}
