package com.ozayakcan.chat.Resim;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class KameraButonView extends AppCompatImageView {

    public interface KameraButonListener{
        void FotografCek();
        void VideoyuBaslat();
        void VideoyuDurdur();
    }

    private Context mContext;

    private KameraButonListener kameraButonListener;

    public KameraButonView(@NonNull Context context) {
        super(context);
        Init(context);
    }

    public KameraButonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public KameraButonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }
    private boolean videoKaydediliyor = false;
    private void Init(Context context){
        this.mContext = context;
        setOnLongClickListener(v -> {
            if (kameraButonListener != null){
                videoKaydediliyor = true;
                kameraButonListener.VideoyuBaslat();
            }
            return true;
        });
    }

    public void setKameraButonListener(KameraButonListener kameraButonListener) {
        this.kameraButonListener = kameraButonListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (videoKaydediliyor){
                    videoKaydediliyor = false;
                    if (kameraButonListener != null){
                        kameraButonListener.VideoyuDurdur();
                    }
                }else{
                    performClick();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (kameraButonListener != null){
            kameraButonListener.FotografCek();
        }
        return true;
    }


}
