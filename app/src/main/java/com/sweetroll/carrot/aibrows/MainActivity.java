package com.sweetroll.carrot.aibrows;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private final String TAG = "FragmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instantiate a ViewPager and a PagerAdapter
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

    //Override to define ViewPager behaviors when user press 'Back' button
    @Override
    public void onBackPressed(){
        //if current page is the main (1st) screen, then call pop the back stack
        if (mViewPager.getCurrentItem() == 0){
            super.onBackPressed();
        } else { //if not, back to the previous step
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    //Implement the pager adapter class that will work with ViewPager here
    private class MainPagerAdapter extends FragmentStatePagerAdapter{
        public MainPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position){
            return new CameraFragment();
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }

}
