package com.ozayakcan.chat.Ozellik;

import android.content.ClipData;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ozayakcan.chat.R;

public class Metinler {

    public static String MAIL_LINK = "mailto:";
    public static String TEL_LINK = "tel:";

    public static void PanoyaKopyala(Context context, String yazi){
        yazi = yazi.replace(MAIL_LINK, "").replace(TEL_LINK, "");
        android.content.ClipboardManager clipboardManager =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), yazi);
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    public static void KlavyeAc(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void KlavyeKapat(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
