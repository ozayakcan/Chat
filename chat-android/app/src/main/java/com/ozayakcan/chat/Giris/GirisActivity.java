package com.ozayakcan.chat.Giris;

import static com.ozayakcan.chat.Ozellik.Animasyonlar.YatayGecisAnimasyonu;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GirisActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String dogrulamaID = "";
    private LinearLayout girisLayout, onayLayout;
    private EditText telefonNumarasi, onayKodu;
    private TextView bosNumaraUyari, dogrulamaHataMesaji, geriSayimText, onayKoduHata;
    private CountryCodePicker ulkeKodu;
    private Button girisBtn, girisBtnDevredisi, onayBtn, onayBtnDevredisi, tekrarGonderBtn, tekrarGonderBtnDevredisi;
    private ProgressBar progressBar;
    private String tamNumara = "";
    private boolean telefonDogrulamasi = false;
    //Gerisayım
    private static final long BASLANGIC_SURESI_MILISANIYE = 61000;
    private CountDownTimer countDownTimer;
    private long kalanSure = BASLANGIC_SURESI_MILISANIYE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Locale.getDefault().getLanguage());
        progressBar = findViewById(R.id.progressBar);
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
                GirisButonDurumu(false);
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
        girisBtnDevredisi = findViewById(R.id.girisBtnDevredisi);
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
                OnayKoduHatalariniGizle();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        onayBtn = findViewById(R.id.onayBtn);
        onayBtnDevredisi = findViewById(R.id.onayBtnDevredisi);
        onayBtn.setOnClickListener(v -> OnayKodunuDogrula());
        tekrarGonderBtn = findViewById(R.id.tekrarGonderBtn);
        tekrarGonderBtnDevredisi = findViewById(R.id.tekrarGonderBtnDevredisi);
        TekrarGonderButonDurumu(false);
        tekrarGonderBtn.setOnClickListener(v -> TekrarGonder());
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
            builder.setMessage(tamNumara+"\n"+getString(R.string.are_you_sure_phone_number));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> OnayKoduGonder());
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
                dogrulamaHataMesaji.setText(getString(R.string.too_many_requests));
            }else{
                dogrulamaHataMesaji.setText(getString(R.string.something_went_wrong));
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
            Ileri();
            SureyiBaslat();
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
    }
    private void TekrarGonder(){
        OnayButonDurumu(true);
        TekrarGonderButonDurumu(false);
        OnayKoduHatalariniGizle();
        kalanSure = BASLANGIC_SURESI_MILISANIYE;
        GeriSayimYazisiniGuncelle();
        OnayKoduGonder();
        SureyiBaslat();
    }
    private void OnayKodunuDogrula(){
        String kod = onayKodu.getText().toString().trim();
        if (kod.length() < 6){
            onayKoduHata.setText(getString(R.string.confirmation_code_is_wrong));
            onayKoduHata.setVisibility(View.VISIBLE);
        }else{
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(dogrulamaID, kod);
            GirisYap(phoneAuthCredential);
        }
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
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
                        databaseReference.keepSynced(true);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                                Intent intent = new Intent(GirisActivity.this, BilgilerActivity.class);
                                if (kullanici == null){
                                    intent.putExtra(Veritabani.ProfilResmiKey, Veritabani.VarsayilanDeger);
                                    intent.putExtra(Veritabani.IsimKey, "");
                                    intent.putExtra(Veritabani.HakkimdaKey, "");
                                }else{
                                    intent.putExtra(Veritabani.ProfilResmiKey, kullanici.getProfilResmi());
                                    intent.putExtra(Veritabani.IsimKey, kullanici.getIsim());
                                    intent.putExtra(Veritabani.HakkimdaKey, kullanici.getHakkimda());
                                }
                                overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(GirisActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            onayKoduHata.setText(getString(R.string.confirmation_code_is_wrong));
                        }else{
                            onayKoduHata.setText(getString(R.string.something_went_wrong));
                        }
                        onayKoduHata.setVisibility(View.VISIBLE);
                    }
                });
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void GirisButonDurumu(boolean durum){
        if (durum){
            progressBar.setVisibility(View.GONE);
            girisBtnDevredisi.setVisibility(View.GONE);
            girisBtn.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            girisBtn.setVisibility(View.GONE);
            girisBtnDevredisi.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void OnayButonDurumu(boolean durum){
        if (durum){
            onayBtnDevredisi.setVisibility(View.GONE);
            onayBtn.setVisibility(View.VISIBLE);
        }else{
            onayBtn.setVisibility(View.GONE);
            onayBtnDevredisi.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void TekrarGonderButonDurumu(boolean durum){
        if (durum){
            tekrarGonderBtnDevredisi.setVisibility(View.GONE);
            tekrarGonderBtn.setVisibility(View.VISIBLE);
        }else{
            tekrarGonderBtn.setVisibility(View.GONE);
            tekrarGonderBtnDevredisi.setVisibility(View.VISIBLE);
        }
    }
    private void OnayKoduHatalariniGizle(){
        dogrulamaHataMesaji.setVisibility(View.GONE);
        dogrulamaHataMesaji.setText("");
        onayKoduHata.setVisibility(View.GONE);
        onayKoduHata.setText("");
    }
    private void Ileri(){
        if (!telefonDogrulamasi) {
            telefonDogrulamasi = true;
            OnayButonDurumu(true);
            TekrarGonderButonDurumu(false);
            GirisButonDurumu(true);
            YatayGecisAnimasyonu(girisLayout, onayLayout);
            onayKodu.requestFocus();
            KlavyeGoster(onayKodu);
        }
    }
    private void Geri(){
        telefonDogrulamasi = false;
        OnayButonDurumu(true);
        TekrarGonderButonDurumu(false);
        GirisButonDurumu(true);
        kalanSure = BASLANGIC_SURESI_MILISANIYE;
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        OnayKoduHatalariniGizle();
        YatayGecisAnimasyonu(onayLayout, girisLayout);
        telefonNumarasi.requestFocus();
        KlavyeGoster(telefonNumarasi);
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
        if(telefonDogrulamasi){
            Geri();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (telefonDogrulamasi){
                Geri();
                return false;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}