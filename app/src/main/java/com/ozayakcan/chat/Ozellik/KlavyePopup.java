package com.ozayakcan.chat.Ozellik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import android.view.WindowMetrics;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ozayakcan.chat.R;

public class KlavyePopup extends PopupWindow {

    public interface KlavyeListener {
        void KlavyeYuksekligiDegisti(int yukseklik);
    }

    private KlavyeListener klavyeListener;

    public int klavyeYuksekligi;

    private final View popupView;

    private final View parentView;

    private final Activity activity;

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    public KlavyePopup(Activity activity) {
        super(activity);
        this.activity = activity;

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = layoutInflater.inflate(R.layout.klavye_popup, null, false);
        setContentView(popupView);

        //setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(this::KlavyeDurumunuBul);
    }

    public void Baslat() {

        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    public void Durdur() {
        this.klavyeListener = null;
        dismiss();
    }

    public void setKlavyeListener(KlavyeListener klavyeListener) {
        this.klavyeListener = klavyeListener;
    }

    @SuppressWarnings("deprecation")
    private void KlavyeDurumunuBul() {

        Point ekranBoyutu = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            ekranBoyutu.x = windowMetrics.getBounds().width() - insets.left - insets.right;
            ekranBoyutu.y = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            ekranBoyutu.x = displayMetrics.widthPixels;
            ekranBoyutu.y = displayMetrics.heightPixels;
        }

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        klavyeYuksekligi = ekranBoyutu.y - rect.bottom;

        klavyeYukseklikDegisikliginiBildir(klavyeYuksekligi);
    }

    private void klavyeYukseklikDegisikliginiBildir(int yukseklik) {
        if (klavyeListener != null) {
            klavyeListener.KlavyeYuksekligiDegisti(yukseklik);
        }
    }
}
