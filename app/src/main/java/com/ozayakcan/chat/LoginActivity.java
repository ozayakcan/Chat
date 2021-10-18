package com.ozayakcan.chat;

import static com.ozayakcan.chat.Utils.Animasyonlar.YatayGecisAnimasyonu;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LinearLayout girisLayout, bosNumaraUyari, onayLayout;
    private EditText telefonNumarasi, onayKodu;
    private CountryCodePicker ulkeKodu;
    private Button girisBtn, onayBtn;
    private String tamNumara = "";
    private int asama = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //Telefon
        girisLayout = findViewById(R.id.girisLayout);
        bosNumaraUyari = findViewById(R.id.bosNumaraUyari);
        ulkeKodu = findViewById(R.id.ulkeKodu);
        telefonNumarasi = findViewById(R.id.telefonNumarasi);
        telefonNumarasi.requestFocus();
        KlavyeGoster(telefonNumarasi);
        telefonNumarasi.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Giris();
            }
            return false;
        });
        telefonNumarasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bosNumaraUyari.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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
        String ulke = ulkeKodu.getSelectedCountryCodeWithPlus();
        String numara = telefonNumarasi.getText().toString();
        tamNumara = ulke+numara;
        if (numara.isEmpty()){
            bosNumaraUyari.setVisibility(View.VISIBLE);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(R.string.confirm_phone_number);
            builder.setMessage(tamNumara+"\n"+getResources().getString(R.string.are_you_sure_phone_number));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                Ileri();
            });
            builder.setNegativeButton(R.string.change, (dialog, which) -> {
                dialog.cancel();
                telefonNumarasi.requestFocus();
                telefonNumarasi.selectAll();
                KlavyeGoster(telefonNumarasi);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void Ileri(){
        if (asama == 2){
            return;
        }else{
            asama++;
        }
        if (asama == 2){
            YatayGecisAnimasyonu(girisLayout, onayLayout);
            onayKodu.requestFocus();
            KlavyeGoster(onayKodu);
        }
    }
    private void Geri(){
        if (asama == 1){
            return;
        }else{
            asama--;
        }
        if (asama == 1){
            YatayGecisAnimasyonu(onayLayout, girisLayout);
            telefonNumarasi.requestFocus();
            KlavyeGoster(telefonNumarasi);
        }
    }
    private void KlavyeGoster(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    @Override
    public void onBackPressed() {
        if(asama == 1){
            super.onBackPressed();
        }else{
            Geri();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (asama == 1){
                return super.onKeyDown(keyCode, event);
            } else{
                Geri();
                return false;
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}