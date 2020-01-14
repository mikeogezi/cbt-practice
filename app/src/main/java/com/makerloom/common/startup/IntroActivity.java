package com.makerloom.common.startup;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.makerloom.ujcbt.R;

/**
 * Created by michael on 4/11/18.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.title_1), getString(R.string.message_1), R.drawable.ic_image_1,
                getResources().getColor(R.color.md_grey_50)));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.title_2), getString(R.string.message_2), R.drawable.ic_image_2,
                getResources().getColor(R.color.md_grey_50)));

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.title_3), getString(R.string.message_3), R.drawable.ic_image_3,
                getResources().getColor(R.color.md_grey_50)));
    }

    @Override
    public void onDonePressed (Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToMain();
    }

    @Override
    public void onSkipPressed (Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToMain();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void goToMain () {
        Intent main = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }
}
