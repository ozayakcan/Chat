package com.ozayakcan.chat.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.ozayakcan.chat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView profilResmi;
    private EditText isimTW;
    private Button bitirBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        profilResmi = findViewById(R.id.profilResmi);
        isimTW = findViewById(R.id.isimTW);
        bitirBtn = findViewById(R.id.bitirBtn);

        isimTW.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet();
            }
            return false;
        });
        bitirBtn.setOnClickListener(v -> Kaydet());

        profilResmi.setOnClickListener(v -> ProfilResmiDegistir());
    }


    private void Kaydet(){
        String isim = isimTW.getText().toString();
        if (isim.isEmpty()){
            //İsim boş
        }else{
            //İlerle
        }
    }

    private void ProfilResmiDegistir() {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return false;
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }
}