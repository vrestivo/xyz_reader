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
    }


    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics=context.getResources().getDisplayMetrics();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        boolean result = dependency instanceof AppBarLayout;

        if(result){
            mToolbar = (Toolbar) dependency.findViewById(R.id.detail_fragment_toolbar);
            mToolbarLogo = (ImageView) mToolbar.findViewById(R.id.detail_toolbar_app_logo);

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

        return result;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        int bottom = dependency.getBottom();

        if(mDisplayMetrics==null) {
            mDisplayMetrics = dependency.getContext().getResources().getDisplayMetrics();
        }

        if(bottom/mDisplayMetrics.scaledDensity<=mToolbar.getHeight() && mToolbarLogo!=null) {
            mToolbarLogo.setVisibility(View.VISIBLE);
        }
        else if (bottom/mDisplayMetrics.scaledDensity>mToolbar.getHeight() && mToolbarLogo.getVisibility() == View.VISIBLE) {
            mToolbarLogo.setVisibility(View.GONE);
        }
        
        return super.onDependentViewChanged(parent, child, dependency);
    }


}
