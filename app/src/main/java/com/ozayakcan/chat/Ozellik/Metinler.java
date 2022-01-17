package com.ozayakcan.chat.Ozellik;

import android.content.ClipData;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ozayakcan.chat.R;

public class Metinler {

    private final Context mContext;

    public static String MAIL_LINK = "mailto:";
    public static String TEL_LINK = "tel:";

    public Metinler(Context context){
        mContext = context;
    }

    public static Metinler getInstance(Context context){
        return new Metinler(context);
    }

    public void PanoyaKopyala(String yazi){
        yazi = yazi.replace(MAIL_LINK, "").replace(TEL_LINK, "");
        android.content.ClipboardManager clipboardManager =
                (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(mContext.getString(R.string.app_name), yazi);
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    public void KlavyeAc(EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.postDelayed(() -> inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED), 200);
    }

    public void KlavyeKapat(EditText editText){
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
