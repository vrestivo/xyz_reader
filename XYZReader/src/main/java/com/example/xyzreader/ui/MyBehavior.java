package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.example.xyzreader.R;

/**
 * Created by devbox on 4/10/17.
 */

//public class MyBehavior extends CoordinatorLayout.Behavior {

public class MyBehavior extends AppBarLayout.ScrollingViewBehavior {



    private final String LOG_TAG = "MyBehavior";
    private final String LOGO_TAG = "app_logo";
    private final String PHOTO_TAG = "tag_photo";

    private int mOverlap = 0;

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

        if(result){
            setOverlap(parent, dependency);
        }

        //return super.layoutDependsOn(parent, child, dependency);

        return result;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        int bottom = dependency.getBottom();
        DisplayMetrics dm = dependency.getContext().getResources().getDisplayMetrics();

        //get 1/3rd of screen's height
        int third =  Math.round(dm.heightPixels/3);


        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + dependency.getTag() + " " + bottom);
        Log.v(LOG_TAG, "_in onDepencentViewChanged: " + child.getTag() + " " + child.getBottom());
        Log.v(LOG_TAG, "_in onDepencentViewChanged: overlap: " + mOverlap);

        Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + dm.scaledDensity + "/" + DisplayMetrics.DENSITY_DEFAULT);
        Log.v(LOG_TAG, "_in onDepencentViewChanged: rate " + (Math.round(dm.xdpi/DisplayMetrics.DENSITY_DEFAULT)));


        setOverlap(parent, dependency);

        if(bottom/dm.scaledDensity<=112) {
            child.setAlpha(1);
        }
        else {
            child.setAlpha(0);
        }

        //TODO set animation for the logo

        return super.onDependentViewChanged(parent, child, dependency);
    }


    public void setOverlap(CoordinatorLayout parent, View dependency){

        int bottom = 0;
        int third =0;

        if(parent!=null && dependency!=null){
            DisplayMetrics dm = dependency.getResources().getDisplayMetrics();
            third = Math.round(dm.heightPixels/3);
            bottom = dependency.getBottom();
        }


        if(bottom>third){
            int newOverlap = bottom-third;
            if(newOverlap>mOverlap){
                mOverlap=newOverlap;
            }
            Log.v(LOG_TAG, "_in onDepencentViewChanged: setting NEW overlap: " + newOverlap);
            NestedScrollView nestedScrollView = (NestedScrollView) parent.findViewById(R.id.nested_scrollview);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
            ((AppBarLayout.ScrollingViewBehavior)layoutParams.getBehavior()).setOverlayTop(mOverlap);
            parent.requestLayout();

        }
    }


}
