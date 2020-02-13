package com.blogspot.waptell.tafakari;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import android.view.View;

import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;


public class IntroActivity extends MaterialIntroActivity {
    //SharedPreferenceConfig
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sharedpreference
        sharedPreferenceConfig = new SharedPreferenceConfig(this);

        //enableLastSlideAlphaExitTransition(true);
        hideBackButton();

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.mipmap.bible_icon)
                        .title("Masomo ya Misa kila siku")
                        .description("Upo Tayari kujifunza?")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .image(R.mipmap.cross_icon)
                .backgroundColor(R.color.colorSlide)
                .buttonsColor(R.color.colorAccent)
                .title("Sauti ya karmeli")
                .description("endelea..")
                .build());


        addSlide(new SlideFragmentBuilder()
                .image(R.mipmap.group_icon)
                .backgroundColor(R.color.colorSlide2)
                .buttonsColor(R.color.colorPrimary)
                .title("Jifunze kupitia Sauti ya karmeli app")
                .description("Upo Tayari kuungana nasi?")
                .build());
    }



    @Override
    public void onFinish() {
        super.onFinish();
        sharedPreferenceConfig.write_welcome_status(true);
//        Toast.makeText(this, "Try this library in your project! :)", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
    }
}
