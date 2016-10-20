package com.vamsi.xyzreader;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.nitrico.stickyscrollview.StickyScrollView;
import com.vamsi.xyzreader.data.ArticleLoader;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ViewPager mPager;
    Cursor mCursor;
    private long mStartId;

    public List<Page> pages = new ArrayList<>();

    MyPagerAdapter mPagerAdapter;

    ImageView ivMain;
    TextView tvTitle, tvDatenAuthor;

    LinearLayout llTitle;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;


    String titleShare = "";
    String TAG = "#XYZ Reader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        //enter transition animation

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.llSticky);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in));
            slide.setDuration(500);
            getWindow().setEnterTransition(slide);
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }

            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetail.this)
                        .setType("text/plain")
                        .setText(""+titleShare+" "+TAG)
                        .getIntent(), getString(R.string.app_name)));
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getLoaderManager().initLoader(0, null, this);

        mStartId = getIntent().getExtras().getLong(Intent.EXTRA_TEXT);

        mPager = (ViewPager) findViewById(R.id.viewPager);

        ivMain = (ImageView) findViewById(R.id.ivMain);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDatenAuthor = (TextView) findViewById(R.id.tvDatenAuthor);

        llTitle = (LinearLayout) findViewById(R.id.llTitle);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                tvTitle.setText(pages.get(position).getTitle());
                titleShare = pages.get(position).getTitle();
                tvDatenAuthor.setText(convertDate(pages.get(position).getDate()) + " by " + pages.get(position).getAuthor());

                Glide
                        .with(ArticleDetail.this)
                        .load(pages.get(position).getImageurl())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                ivMain.setImageBitmap(resource); // Possibly runOnUiThread()

                                if (resource != null) {
                                    myPallet(resource);
                                }

                            }
                        });


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }

        //stickyScrollView.setVisibility(View.GONE);
        super.onBackPressed();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        pages = new ArrayList<>();

        mCursor = cursor;

        int currentPosition = 0;

        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {

                pages.add(new Page(mCursor));

                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    currentPosition = mCursor.getPosition();
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }

//        Toast.makeText(this, ""+pages.size(), Toast.LENGTH_SHORT).show();

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setCurrentItem(currentPosition, false);

        titleShare = pages.get(currentPosition).getTitle();

        tvTitle.setText(pages.get(currentPosition).getTitle());
        tvDatenAuthor.setText(convertDate(pages.get(currentPosition).getDate()) + " by " + pages.get(currentPosition).getAuthor());

        Glide
                .with(ArticleDetail.this)
                .load(pages.get(currentPosition).getImageurl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        ivMain.setImageBitmap(resource); // Possibly runOnUiThread()

                        if (resource != null) {
                            myPallet(resource);
                        }

                    }
                });


        mPagerAdapter.notifyDataSetChanged();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        pages = new ArrayList<>();
        mPagerAdapter.notifyDataSetChanged();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            return ArticleDetailFragment.newInstance(pages.get(pos).getContent());
        }

        @Override
        public int getCount() {
            return pages.size();
        }
    }


    public class Page {

        private String title, imageurl, imageurltn, content, date, author;
        private int color;

        public Page(Cursor cursor) {
            this.title = cursor.getString(ArticleLoader.Query.TITLE);
            this.imageurl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
            this.imageurltn = cursor.getString(ArticleLoader.Query.THUMB_URL);
            this.content = cursor.getString(ArticleLoader.Query.BODY);

            this.date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            this.author = cursor.getString(ArticleLoader.Query.AUTHOR);

            this.color = 0;
        }

        public String getDate() {
            return date;
        }

        public String getAuthor() {
            return author;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public String getImageurl() {
            return imageurl;
        }

        public String getContent() {
            return content;
        }

        public int getColor() {
            return color;
        }
    }


    private void myPallet(Bitmap bitmap) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {

                int dominant = palette.getDominantColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));

                AppBarLayout barLayout = (AppBarLayout) findViewById(R.id.app_bar);

                if (barLayout != null) {
                    barLayout.setBackgroundColor(dominant);
                }

                collapsingToolbarLayout.setBackgroundColor(dominant);
                collapsingToolbarLayout.setContentScrimColor(dominant);
                collapsingToolbarLayout.setStatusBarScrimColor(dominant);

                llTitle.setBackgroundColor(dominant);

            }
        });


    }


    public static String convertDate(String dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy", Long.parseLong(dateInMilliseconds)).toString();
    }

}
