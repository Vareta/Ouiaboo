package com.ouiaboo.ouiaboo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Vareta on 29-03-2016.
 */
public class Tutorial extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance(getString(R.string.title1_tutorial), getString(R.string.info1_tutorial), R.drawable.tutorial_ini, Color.parseColor("#212121")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title2_tutorial), getString(R.string.info2_tutorial), R.drawable.long_press, Color.parseColor("#607D8B")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title3_tutorial), getString(R.string.info3_tutorial), R.drawable.swipe_down, Color.parseColor("#0277BD")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title4_tutorial), getString(R.string.info4_tutorial), R.drawable.swipe_left, Color.parseColor("#7C4DFF")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title5_tutorial), getString(R.string.info5_tutorial), R.drawable.config, Color.parseColor("#8BC34A")));

        showStatusBar(true);
        setNavBarColor("#388e3c");
        setFadeAnimation();
    }


    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        //completa el tutorial
        SharedPreferences.Editor editor = getSharedPreferences(Utilities.PREFERENCIAS, MODE_PRIVATE).edit();
        editor.putBoolean("tutorialCompletado", true);
        editor.apply();
        //inicia actividad central
        Intent intent = new Intent(getBaseContext(), Central.class);
        startActivity(intent); //pasa a la nueva actividad
        finish(); //cierra la actividad actual, para no poder volver con el boton back
    }

    @Override
    public void onSlideChanged() {

    }
}
