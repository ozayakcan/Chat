package com.ozayakcan.chat.Ozellik;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ozayakcan.chat.R;

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

    public interface AnimasyonListener{
        void Basladi(Animation animation);
        void Bitti(Animation animation);
    }
    public static void Buyut(Context context, View view, long sure){
        OzelAnimasyon(context, view, sure, R.anim.buyut, new AnimasyonListener() {
            @Override
            public void Basladi(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void Bitti(Animation animation) {

            }
        });
    }
    public static void Kucult(Context context, View view, long sure){
        OzelAnimasyon(context, view, sure, R.anim.kucult, new AnimasyonListener() {
            @Override
            public void Basladi(Animation animation) {

            }

            @Override
            public void Bitti(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });
    }
    public static void OzelAnimasyon(Context context, View view, long sure, int animasyonID, AnimasyonListener animasyonListener){
        Animation animation = AnimationUtils.loadAnimation(context, animasyonID);
        animation.setDuration(sure);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animasyonListener.Basladi(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animasyonListener.Bitti(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }
}