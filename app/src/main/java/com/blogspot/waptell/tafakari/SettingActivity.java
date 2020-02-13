package com.blogspot.waptell.tafakari;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class SettingActivity extends AppCompatActivity {

    //Toolbar
    private Toolbar mToolbar;

    private static final String TAG = "SettingActivity";

    //ads
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Toolbar
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.setting));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.setting_container)!=null){
            if (savedInstanceState != null)
                return;
            getFragmentManager().beginTransaction().add(R.id.setting_container, new SettingFragment()).commit();
        }

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.e(TAG, "onRewardedVideoAdLoaded: " );
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.e(TAG, "onRewardedVideoAdOpened: " );
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.e(TAG, "onRewardedVideoStarted: " );
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.e(TAG, "onRewardedVideoAdClosed: " );
                loadRewardedVideoAd();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.e(TAG, "onRewarded: " );
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.e(TAG, "onRewardedVideoAdLeftApplication: " );
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.e(TAG, "onRewardedVideoAdFailedToLoad: " );
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.e(TAG, "onRewardedVideoCompleted: " );
            }
        });
        loadRewardedVideoAd();

    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getResources().getString(R.string.abmob_videobanner_id),
                new AdRequest.Builder().build());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }
}
