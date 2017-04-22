package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import java.util.ArrayList;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */

//FIXME get rid of loader callbacks
public class ArticleDetailActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<Cursor>
{

    private final String LOG_TAG = "ArticleDetailActivity";

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;

    //tags variables to be saved in instance state
    private final String ID_TAG = "ID_TAG";
    private final String ART_TAG = "ART_TAG";

    // ArrayList containing movie IDs which will be passed
    // to MyPagerAdapter
    private ArrayList<Long> mArticleIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FIXME
        //postpone transition
        postponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);


        if(savedInstanceState == null) {
            Intent intent = getIntent();

            if (intent != null && intent.hasExtra(ArticleListActivity.ARTICLE__IDS_TAG)) {
                //This works since ArrayList implements Serializable
                mArticleIdList = (ArrayList<Long>) intent.getSerializableExtra(ArticleListActivity.ARTICLE__IDS_TAG);
                for (Long id : mArticleIdList) {
                    Log.v(LOG_TAG, "_Art_ID" + String.valueOf(id));
                }

                if (getIntent() != null && getIntent().getData() != null) {
                    mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                    mSelectedItemId = mStartId;
                }
            }
        }
        //restore variables state
        else {
            mSelectedItemId = savedInstanceState.getLong(ID_TAG);
            mArticleIdList = (ArrayList<Long>) savedInstanceState.getSerializable(ART_TAG);
        }


        //TODO delete when done
        Log.v(LOG_TAG, "_in onCreate() item ID: " + mSelectedItemId);
        Log.v(LOG_TAG, "_in onCreate() ArrayListSize: " + mArticleIdList.size());


        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        //set current article
        mPager.setCurrentItem(mArticleIdList.indexOf(mSelectedItemId));

        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        //FIX change to addOnPAgeChangeLitener
        //since setOnPageChangeListener() is deprecated
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mArticleIdList != null) {
                    mSelectedItemId =mArticleIdList.get(position);

                }
            }
        });

    } //end of onCreate()



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(ID_TAG, mSelectedItemId);
        outState.putSerializable(ART_TAG, mArticleIdList);
        super.onSaveInstanceState(outState);
    }


    //FIXME
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //informs adapter which page is currently shown
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        //Required
        @Override
        public Fragment getItem(int position) {
            //mCursor.moveToPosition(position);
            if(mArticleIdList!=null && position < mArticleIdList.size() ) {
                Log.v(LOG_TAG, "_art position: " + mArticleIdList.get(position));
                return ArticleDetailFragment.newInstance(mArticleIdList.get(position));
            }

            return  null;
        }

        //Required
        @Override
        public int getCount() {
            //return (mCursor != null) ? mCursor.getCount() : 0;
            if(mArticleIdList!= null){
                return mArticleIdList.size();
            }
            return 0;
        }
    }
}
