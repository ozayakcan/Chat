package com.passiondroid.imageeditorlib.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import android.view.WindowMetrics;
import android.widget.PopupWindow;
import com.passiondroid.imageeditorlib.R;

/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed. 
 */
public class KeyboardHeightProvider extends PopupWindow {

    /** The tag for logging purposes */
    private final static String TAG = "sample_KeyboardHeightProvider";

    /** The keyboard height observer */
    private KeyboardHeightObserver observer;

    /** The cached landscape height of the keyboard */
    private int keyboardLandscapeHeight;

    /** The cached portrait height of the keyboard */
    private int keyboardPortraitHeight;

    /** The view that is used to calculate the keyboard height */
    private View popupView;

    /** The parent view */
    private View parentView;

    /** The root activity that uses this KeyboardHeightProvider */
    private Activity activity;

    /** 
     * Construct a new KeyboardHeightProvider
     * 
     * @param activity The parent activity
     */
    @SuppressWarnings("deprecation")
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(R.layout.popwindow, null, false);
        setContentView(popupView);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    if (popupView != null) {
                        handleOnGlobalLayout();
                    }
                }
            });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {

        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider, 
     * this provider will not be used anymore.
     */
    public void close() {
        this.observer = null;
        dismiss();
    }

    /** 
     * Set the keyboard height observer to this provider. The 
     * observer will be notified when the keyboard height has changed. 
     * For example when the keyboard is opened or closed.
     * 
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity. 
     * The keyboard can then be calculated by extracting the popup view bottom 
     * from the activity window height. 
     */
    @SuppressWarnings("deprecation")
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        //activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            screenSize.x = windowMetrics.getBounds().width() - insets.left - insets.right;
            screenSize.y = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenSize.x = displayMetrics.widthPixels;
            screenSize.y = displayMetrics.heightPixels;
        }
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation);
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.keyboardPortraitHeight = keyboardHeight; 
            notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation);
        } 
        else {
            this.keyboardLandscapeHeight = keyboardHeight; 
            notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation);
        }
    }

    /**
     *
     */
    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }

    public interface KeyboardHeightObserver {
        void onKeyboardHeightChanged(int height, int orientation);
    }
}