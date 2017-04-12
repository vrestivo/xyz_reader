package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by devbox on 4/10/17.
 */

public class MyBehavior extends CoordinatorLayout.Behavior {

    private final String LOG_TAG = "MyBehavior";
    private final String LOGO_TAG = "app_logo";
    private final String PHOTO_TAG = "tag_photo";

    private String mAppLogoTag;

    public MyBehavior() {
        super();
        Log.v(LOG_TAG, "_in Constructor");
    }


    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v(LOG_TAG, "_in Constructor");
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //boolean result = dependency instanceof Toolbar;
        //boolean result = PHOTO_TAG.equals(dependency.getTag());

        boolean result = dependency instanceof AppBarLayout;


        Log.v(LOG_TAG, "_in layoutDependsOn: " + child.getTag() + " " +dependency.getTag() + " " + String.valueOf(result));


        //return super.layoutDependsOn(parent, child, dependency);

        return result;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        int bottom = dependency.getBottom();
        DisplayMetrics dm = dependency.getContext().getResources().getDisplayMetrics();

        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + dependency.getTag() + " " + bottom);
        Log.v(LOG_TAG, "_in onDepencentViewChanged: min height " + dependency.getMinimumHeight());
        Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + dm.scaledDensity + "/" + DisplayMetrics.DENSITY_DEFAULT);
        Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + (Math.round(dm.xdpi/DisplayMetrics.DENSITY_DEFAULT)));




        if(bottom/dm.scaledDensity<=112) {
            child.setAlpha(1);
        }
        else {
            child.setAlpha(0);
        }

        //TODO set animation for the logo

        return super.onDependentViewChanged(parent, child, dependency);
    }
}
