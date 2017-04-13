package com.example.xyzreader.ui;

import android.content.Context;
import android.media.Image;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.xyzreader.R;

/**
 * Created by devbox on 4/10/17.
 */

//public class MyBehavior extends CoordinatorLayout.Behavior {

public class MyBehavior extends AppBarLayout.ScrollingViewBehavior {



    private final String LOG_TAG = "MyBehavior";
    private final String LOGO_TAG = "app_logo";
    private final String PHOTO_TAG = "tag_photo";

    private Toolbar mToolbar;
    private ImageView mToolbarLogo;
    private DisplayMetrics mDisplayMetrics;

    //used for setting overlap
    private final int DENOMINATOR = 3;

    private int mOverlap = 0;

    private String mAppLogoTag;

    public MyBehavior() {
        super();

        //TODO delete logging
        Log.v(LOG_TAG, "_in empty Constructor");
    }


    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        //TODO delete logging
        Log.v(LOG_TAG, "_in XML Constructor");
        mDisplayMetrics=context.getResources().getDisplayMetrics();



    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //boolean result = dependency instanceof Toolbar;
        //boolean result = PHOTO_TAG.equals(dependency.getTag());

        boolean result = dependency instanceof AppBarLayout;

        //TODO delete logging
        Log.v(LOG_TAG, "_in layoutDependsOn: " + child.getTag() + " " +dependency.getTag() + " " + String.valueOf(result));

        if(result){
            mToolbar = (Toolbar) dependency.findViewById(R.id.detail_fragment_toolbar);
            mToolbarLogo = (ImageView) mToolbar.findViewById(R.id.detail_toolbar_app_logo);
            //setOverlap(parent, dependency);
            if(mDisplayMetrics==null) {
                mDisplayMetrics = dependency.getResources().getDisplayMetrics();
            }
            int third = Math.round(mDisplayMetrics.heightPixels/DENOMINATOR);
            int bottom = dependency.getBottom();

            int newOverlap = bottom-third;
            if(newOverlap>mOverlap){
                mOverlap=newOverlap;
                setOverlayTop(mOverlap);

            }

        }

        //return super.layoutDependsOn(parent, child, dependency);

        return result;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        int bottom = dependency.getBottom();

        if(mDisplayMetrics==null) {
            mDisplayMetrics = dependency.getContext().getResources().getDisplayMetrics();
        }

        //get 1/3rd of screen's height
        int third =  Math.round(mDisplayMetrics.heightPixels/3);

        //TODO delete logging
        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + dependency.getTag() + " " + bottom + "/" + dependency.getHeight());
        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + child.getTag() + " " + child.getBottom());
        Log.v(LOG_TAG, "_in onDepencentViewChanged: overlap: " + mOverlap);

        //Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + mDisplayMetrics.scaledDensity + "/" + DisplayMetrics.DENSITY_DEFAULT);
        //Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + (Math.round(mDisplayMetrics.xdpi/DisplayMetrics.DENSITY_DEFAULT)));


        //setOverlap(parent, dependency);


        if(bottom/mDisplayMetrics.scaledDensity<=mToolbar.getHeight() && mToolbarLogo!=null) {
            mToolbarLogo.setImageAlpha(255);
        }
        else {
            mToolbarLogo.setImageAlpha(0);
        }

        //TODO set animation for the logo

        return super.onDependentViewChanged(parent, child, dependency);
    }


}
