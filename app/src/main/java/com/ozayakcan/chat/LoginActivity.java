package com.ozayakcan.chat;

import static com.ozayakcan.chat.Utils.Animasyonlar.YatayGecisAnimasyonu;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LinearLayout girisLayout, onayLayout;
    private EditText telefonNumarasi;
    private Button girisBtn;
    private boolean telefonOnayi = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        girisLayout = findViewById(R.id.girisLayout);
        telefonNumarasi = findViewById(R.id.telefonNumarasi);
        girisBtn = findViewById(R.id.girisBtn);
        girisBtn.setOnClickListener(v -> Giris());
        onayLayout = findViewById(R.id.onayLayout);
    }

    private void Giris(){
        YatayGecisAnimasyonu(girisLayout, onayLayout);
        telefonOnayi = true;
    }
    @Override
    public void onBackPressed() {
        if(telefonOnayi){
            YatayGecisAnimasyonu(onayLayout, girisLayout);
            telefonOnayi = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (telefonOnayi){
                YatayGecisAnimasyonu(onayLayout, girisLayout);
                telefonOnayi = false;
                return false;
            } else{
                return super.onKeyDown(keyCode, event);
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (telefonOnayi){
                YatayGecisAnimasyonu(onayLayout, girisLayout);
                telefonOnayi = false;
                return false;
            }else{
                return super.onKeyLongPress(keyCode, event);
            }
        }else{
            return super.onKeyLongPress(keyCode, event);
        }
    }
}