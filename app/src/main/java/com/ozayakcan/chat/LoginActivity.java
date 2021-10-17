package com.ozayakcan.chat;

import static com.ozayakcan.chat.Utils.Animasyonlar.YatayGecisAnimasyonu;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LinearLayout girisLayout, onayLayout;
    private EditText telefonNumarasi, onayKodu;
    private Button girisBtn, onayBtn;
    private boolean telefonOnayi = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //Telefon
        girisLayout = findViewById(R.id.girisLayout);
        telefonNumarasi = findViewById(R.id.telefonNumarasi);
        telefonNumarasi.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Giris();
            }
            return false;
        });
        girisBtn = findViewById(R.id.girisBtn);
        girisBtn.setOnClickListener(v -> Giris());
        //Onay
        onayLayout = findViewById(R.id.onayLayout);
        onayKodu = findViewById(R.id.onayKodu);
        onayKodu.setOnEditorActionListener((v, actionId, event) -> {
            //Henüz Eklenmedi
            return false;
        });
        onayBtn = findViewById(R.id.onayBtn);
        onayBtn.setOnClickListener(v -> {
            //Henüz Eklenmedi
        });
    }

    private void Giris(){
        Ileri();
    }
    private void Ileri(){
        YatayGecisAnimasyonu(girisLayout, onayLayout);
        telefonOnayi = true;
        onayKodu.requestFocus();
        KlavyeGoster(onayKodu);
    }
    private void Geri(){
        YatayGecisAnimasyonu(onayLayout, girisLayout);
        telefonOnayi = false;
        telefonNumarasi.requestFocus();
        KlavyeGoster(telefonNumarasi);
    }
    private void KlavyeGoster(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    @Override
    public void onBackPressed() {
        if(telefonOnayi){
            Geri();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (telefonOnayi){
                Geri();
                return false;
            } else{
                return super.onKeyDown(keyCode, event);
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}