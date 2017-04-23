package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleListActivity.class.toString();
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private final float mImageAspectRatio = 1.0f;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    //transition name prefix
    public static final String TRANS_PREFIX = "image_";

    //ArrayList to store article IDs will be used to pass to detail activity
    private ArrayList<Long> mArticleIdList;

    //string name for the mArticleIdList
    public static final String ARTICLE__IDS_TAG = "ARTICLE_IDs";

    //request code to determine if detail activity is done
    public static final int REQ_CODE = 1337;

    //tracks state of the detail activity
    private boolean mDetailActivityActive = false;

    //local broadcast manager instance
    private LocalBroadcastManager mLocalBroadcastManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_list);


        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        //TODO cleanup
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //ctl.setExpandedTitleColor(ContextCompat.getColor(this, R.color.transparent));
        //ctl.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.theme_accent));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);


        //TODO cleanup
        //final View toolbarContainerView = findViewById(R.id.toolbar_container);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //changing to local broadcast since we are not sharing data outside our process
        //and the use of sticky broadcasts is discouraged due to security reasons
        //additionally local broadcasts are more efficient
        mLocalBroadcastManager.registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if(data!=null && data.hasExtra(UpdaterService.EXTRA_REFRESHING)){
                    mIsRefreshing = data.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    updateRefreshingUI();
                }
            }
        }
        mDetailActivityActive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalBroadcastManager.unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
                //TODO delete when done
                Toast.makeText(getApplicationContext(), "Broadcast Receiver: updated", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //fill mArticleIdList with article IDs
        //the list will be passed to the detail activity to cut out
        //unneeded database operations
        int numItems = cursor.getCount();
        if (numItems > 0 && cursor.moveToFirst()) {
            mArticleIdList = new ArrayList<>();
            int i = 0;
            while (i < numItems) {
                mArticleIdList.add(cursor.getLong(ArticleLoader.Query._ID));
                cursor.moveToNext();
                i++;
            }
        }


        Adapter adapter = new Adapter(cursor, getApplicationContext(), this);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    //Adapter implementation
    //NOTE default returns _ID column value, NOT position index
    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;
        private AppCompatActivity mActivity;
        private Context mmContext;

        //        public Adapter(Cursor cursor, AppCompatActivity activity) {
        public Adapter(Cursor cursor, Context context, AppCompatActivity activity) {

            mCursor = cursor;
            mActivity = activity;
            mmContext = context;

        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int VhPosition = vh.getAdapterPosition();

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(VhPosition)));
                    //paranoid check, just in case
                    if (mArticleIdList != null) {
                        intent.putExtra(ARTICLE__IDS_TAG, mArticleIdList);
                    }

                    //TODO
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {
                        if (view instanceof CardView) {
                            View thumbnail = view.findViewById(R.id.thumbnail);
                            //TODO get clicked item's ID
                            String transitionName = TRANS_PREFIX + String.valueOf(mArticleIdList.get(VhPosition));

                            thumbnail.setTransitionName(transitionName);

                            //TODO delete logging
                            Log.v(TAG, "_clicked item transition name " + transitionName);

                            String trName = null;
                            if (thumbnail != null && (trName = thumbnail.getTransitionName()) != null) {
                                Bundle transitionBundle = ActivityOptionsCompat
                                        .makeSceneTransitionAnimation(
                                                mActivity,
                                                //getApplication().act,
                                                thumbnail,
                                                trName)
                                        .toBundle();

                                mDetailActivityActive = true;

                                startActivityForResult(intent, REQ_CODE, transitionBundle);

                            }


                        }
                        Log.v(TAG, "_in onCreateViewHolder() view or view transition name is null");

                    } else {
                        mDetailActivityActive = true;
                        startActivityForResult(intent, REQ_CODE);
                    }
                }
            });
            return vh;
        }

        private Date parsePublishedDate() {
            try {
                String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
                return dateFormat.parse(date);
            } catch (ParseException ex) {
                Log.e(TAG, ex.getMessage());
                Log.i(TAG, "passing today's date");
                return new Date();
            }
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.subtitleView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            } else {
                holder.subtitleView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            }
            holder.thumbnailView.setImageUrl(
                    mCursor.getString(ArticleLoader.Query.THUMB_URL),
                    ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
            //TODO cleanup
            //holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
            holder.thumbnailView.setAspectRatio(mImageAspectRatio);

        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public void updateDetailActivity() {
    }

    //ViewHolder implementation
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
