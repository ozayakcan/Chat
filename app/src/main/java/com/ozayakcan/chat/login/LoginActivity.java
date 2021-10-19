package com.ozayakcan.chat.login;

import static com.ozayakcan.chat.Utils.Animasyonlar.YatayGecisAnimasyonu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.ozayakcan.chat.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String dogrulamaID = "";
    private LinearLayout girisLayout, onayLayout;
    private EditText telefonNumarasi, onayKodu;
    private TextView bosNumaraUyari, dogrulamaHataMesaji, geriSayimText, onayKoduHata;
    private CountryCodePicker ulkeKodu;
    private Button girisBtn, onayBtn, tekrarGonderBtn;
    private String tamNumara = "";
    private int asama = 1;
    //Gerisayım
    private static final long BASLANGIC_SURESI_MILISANIYE = 61000;
    private CountDownTimer countDownTimer;
    private long kalanSure = BASLANGIC_SURESI_MILISANIYE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Locale.getDefault().getLanguage());
        //Telefon
        girisLayout = findViewById(R.id.girisLayout);
        bosNumaraUyari = findViewById(R.id.bosNumaraUyari);
        dogrulamaHataMesaji = findViewById(R.id.dogrulamaHataMesaji);
        geriSayimText = findViewById(R.id.geriSayimText);
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
        girisBtn.setOnClickListener(v -> {
            GirisButonDurumu(false);
            Giris();
        });
        //Onay
        onayLayout = findViewById(R.id.onayLayout);
        onayKodu = findViewById(R.id.onayKodu);
        onayKodu.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                OnayKodunuDogrula();
            }
            return false;
        });
        onayKoduHata = findViewById(R.id.onayKoduHata);
        onayKodu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onayKoduHata.setVisibility(View.GONE);
                onayKoduHata.setText("");
                dogrulamaHataMesaji.setVisibility(View.GONE);
                dogrulamaHataMesaji.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        onayBtn = findViewById(R.id.onayBtn);
        onayBtn.setOnClickListener(v -> {
            OnayKodunuDogrula();
        });
        tekrarGonderBtn = findViewById(R.id.tekrarGonderBtn);
        TekrarGonderButonDurumu(false);
        tekrarGonderBtn.setOnClickListener(v -> {
            TekrarGonder();
        });
    }

    private void Giris(){
        String ulke = ulkeKodu.getSelectedCountryCodeWithPlus();
        String numara = telefonNumarasi.getText().toString();
        tamNumara = ulke+numara;
        if (numara.isEmpty()){
            bosNumaraUyari.setVisibility(View.VISIBLE);
        }else{
            KlavyeGizle();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(R.string.confirm_phone_number);
            builder.setMessage(tamNumara+"\n"+getResources().getString(R.string.are_you_sure_phone_number));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                OnayKoduGonder();
            });
            builder.setNegativeButton(R.string.change, (dialog, which) -> {
                dialog.cancel();
                telefonNumarasi.requestFocus();
                telefonNumarasi.selectAll();
                KlavyeGoster(telefonNumarasi);
                tamNumara = "";
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    // https://firebase.google.com/docs/auth/android/phone-auth
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String kod = phoneAuthCredential.getSmsCode();
            if (kod != null){
                OnayKodunuDogrula(kod);
                onayKodu.setText(kod);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseTooManyRequestsException) {
                dogrulamaHataMesaji.setText(getResources().getString(R.string.too_many_requests));
            }else{
                dogrulamaHataMesaji.setText(getResources().getString(R.string.something_went_wrong));
            }
            dogrulamaHataMesaji.setVisibility(View.VISIBLE);
            if(countDownTimer != null){
                countDownTimer.cancel();
            }
            geriSayimText.setText("");
            TekrarGonderButonDurumu(true);
            OnayButonDurumu(false);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            dogrulamaID = s;
        }
    };
    private void OnayKoduGonder() {
        TekrarGonderButonDurumu(false);
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(tamNumara)
                .setTimeout(BASLANGIC_SURESI_MILISANIYE, TimeUnit.MILLISECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        Ileri();
        SureyiBaslat();
    }
    private void TekrarGonder(){
        OnayButonDurumu(true);
        TekrarGonderButonDurumu(false);
        kalanSure = BASLANGIC_SURESI_MILISANIYE;
        GeriSayimYazisiniGuncelle();
        OnayKoduGonder();
    }
    private void OnayKodunuDogrula(){
        String kod = onayKodu.getText().toString().trim();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(dogrulamaID, kod);
        GirisYap(phoneAuthCredential);
    }
    private void OnayKodunuDogrula(String kod){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(dogrulamaID, kod);
        GirisYap(phoneAuthCredential);
    }
    //Gerisayım
    private void SureyiBaslat(){
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(kalanSure, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                kalanSure = millisUntilFinished;
                GeriSayimYazisiniGuncelle();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                OnayButonDurumu(false);
                TekrarGonderButonDurumu(true);
                geriSayimText.setText("00:00");
            }
        }.start();
    }
    private void GeriSayimYazisiniGuncelle(){
        int dakika = (int) (kalanSure / 1000) / 60;
        int saniye = (int) (kalanSure / 1000) % 60;
        String kalanSureFormati = String.format(Locale.getDefault(),"%02d:%02d", dakika, saniye);
        geriSayimText.setText(kalanSureFormati);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("kalanSure", kalanSure);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        kalanSure = savedInstanceState.getLong("kalanSure");
        GeriSayimYazisiniGuncelle();
        SureyiBaslat();
    }

    private void GirisYap(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
                        finish();
                    }else{
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            onayKoduHata.setText(getResources().getString(R.string.confirmation_code_is_wrong));
                        }else{
                            onayKoduHata.setText(getResources().getString(R.string.something_went_wrong));
                        }
                        onayKoduHata.setVisibility(View.VISIBLE);
                    }
                });
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void GirisButonDurumu(boolean durum){
        girisBtn.setEnabled(durum);
        if (durum){
            girisBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_enabled));
            girisBtn.setTextColor(getResources().getColor(R.color.white));
        }else{
            girisBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_disabled));
            girisBtn.setTextColor(getResources().getColor(R.color.disabledText));
        }
    }
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void OnayButonDurumu(boolean durum){
        onayBtn.setEnabled(durum);
        if (durum){
            onayBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_enabled));
            onayBtn.setTextColor(getResources().getColor(R.color.white));
        }else{
            onayBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_disabled));
            onayBtn.setTextColor(getResources().getColor(R.color.disabledText));
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void TekrarGonderButonDurumu(boolean durum){
        tekrarGonderBtn.setEnabled(durum);
        if (durum){
            tekrarGonderBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_enabled));
            tekrarGonderBtn.setTextColor(getResources().getColor(R.color.white));
        }else{
            tekrarGonderBtn.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundtint_disabled));
            tekrarGonderBtn.setTextColor(getResources().getColor(R.color.disabledText));
        }
    }
    private void Ileri(){
        if (asama == 2){
            return;
        }else{
            asama++;
        }
        if (asama == 2){
            OnayButonDurumu(true);
            TekrarGonderButonDurumu(false);
            GirisButonDurumu(true);
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
            OnayButonDurumu(true);
            TekrarGonderButonDurumu(false);
            GirisButonDurumu(true);
            kalanSure = BASLANGIC_SURESI_MILISANIYE;
            if(countDownTimer != null){
                countDownTimer.cancel();
            }
            onayKoduHata.setVisibility(View.GONE);
            onayKoduHata.setText("");
            dogrulamaHataMesaji.setVisibility(View.GONE);
            dogrulamaHataMesaji.setText("");
            YatayGecisAnimasyonu(onayLayout, girisLayout);
            telefonNumarasi.requestFocus();
            KlavyeGoster(telefonNumarasi);
        }
    }
    private void KlavyeGoster(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    private void KlavyeGizle(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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