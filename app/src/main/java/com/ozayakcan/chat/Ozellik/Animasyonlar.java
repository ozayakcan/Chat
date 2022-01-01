package com.ozayakcan.chat.Ozellik;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Animasyonlar {

    public static int varsayilanAnimasyonSuresi = 300;

    public static void Boyut(View view, int suankiBoyut, int yeniBoyut, boolean dikey){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(suankiBoyut, yeniBoyut).setDuration(varsayilanAnimasyonSuresi);
        valueAnimator.addUpdateListener(animation1 -> {
            if (dikey){
                view.getLayoutParams().height = (Integer) animation1.getAnimatedValue();
                view.requestLayout();
            }else{
                view.getLayoutParams().width = (Integer) animation1.getAnimatedValue();
                view.requestLayout();
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(valueAnimator);
        animatorSet.start();
    }

    public static void DikeyGecisAnimasyonu(View gizlenecekView, View gosterilecekView){
        int layoutBoyutu = gizlenecekView.getHeight();
        Boyut(gizlenecekView, gizlenecekView.getHeight(), 0, true);
        Boyut(gosterilecekView, 0, layoutBoyutu, true);
    }
    public static void YatayGecisAnimasyonu(View gizlenecekView, View gosterilecekView){
        int layoutBoyutu = gizlenecekView.getWidth();
        Boyut(gizlenecekView, gizlenecekView.getWidth(), 0, false);
        Boyut(gosterilecekView, 0, layoutBoyutu, false);
    }

    public static void GosterGizle(Context context, boolean goster, View view, long sure, int animasyonID){
        Animation animation = AnimationUtils.loadAnimation(context, animasyonID);
        animation.setDuration(sure);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (goster){
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!goster){
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }
}