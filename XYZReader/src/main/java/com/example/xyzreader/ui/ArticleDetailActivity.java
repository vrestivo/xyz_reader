package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

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

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mUpdating;

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


        if (savedInstanceState == null) {
            Intent intent = getIntent();

            if (intent != null && intent.hasExtra(ArticleListActivity.ARTICLE__IDS_TAG)) {
                //This works since ArrayList implements Serializable
                mArticleIdList = (ArrayList<Long>) intent.getSerializableExtra(ArticleListActivity.ARTICLE__IDS_TAG);

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


        //setup broadcast receiver for updates from UpdaterService
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Toast.makeText(context, context.getString(R.string.toast_update_complete), Toast.LENGTH_SHORT).show();



                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {


                    if(intent.hasExtra(UpdaterService.EXTRA_REFRESHING)) {
                        boolean updated =  intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                        ArticleDetailActivity.this.setResult(
                                RESULT_OK,
                                new Intent().putExtra(UpdaterService.EXTRA_REFRESHING, updated));
                    }

                    if (intent.hasExtra(ArticleListActivity.ARTICLE__IDS_TAG)) {
                        int currentItem = mPager.getCurrentItem();

                        mPager.getAdapter().notifyDataSetChanged();
                        mArticleIdList = (ArrayList<Long>) intent.getSerializableExtra(ArticleListActivity.ARTICLE__IDS_TAG);

                        if (mArticleIdList.size() > 0) {

                            mPagerAdapter = new MyPagerAdapter(getFragmentManager());
                            mPager.setAdapter(mPagerAdapter);

                            //FIX: blank screen on updates by resetting adapter to new data;
                            if (currentItem < mArticleIdList.size()) {
                                mPager.setCurrentItem(currentItem);
                            } else {
                                mPager.setCurrentItem(0);
                            }
                        }
                        else {
                            finish();
                        }
                    }
                }
            }
        };

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        //set current article
        mPager.setCurrentItem(mArticleIdList.indexOf(mSelectedItemId));

        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        //FIX: change to addOnPAgeChangeLitener
        //since setOnPageChangeListener() is deprecated
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mArticleIdList != null) {
                    mSelectedItemId = mArticleIdList.get(position);

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



    @Override
    protected void onStart() {
        super.onStart();
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
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
            if (mArticleIdList != null && position < mArticleIdList.size()) {
                Log.v(LOG_TAG, "_art position: " + mArticleIdList.get(position));
                return ArticleDetailFragment.newInstance(mArticleIdList.get(position));
            }

            return null;
        }

        //Required
        @Override
        public int getCount() {
            if (mArticleIdList != null) {
                return mArticleIdList.size();
            }
            return 0;
        }
    }
}
