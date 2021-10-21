package com.ozayakcan.chat.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Utils.Veritabani;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView profilResmi, kamera;
    private EditText isimTW, hakkimdaTW;
    private Button bitirBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        isimTW = findViewById(R.id.isimTW);
        hakkimdaTW = findViewById(R.id.hakkimdaTW);
        bitirBtn = findViewById(R.id.bitirBtn);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        isimTW.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT){
                hakkimdaTW.requestFocus();
            }
            return false;
        });

        hakkimdaTW.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet();
            }
            return false;
        });
        bitirBtn.setOnClickListener(v -> Kaydet());

        profilResmi.setOnClickListener(v -> ProfilResmiDegistir());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                assert kullanici != null;
                isimTW.setText(kullanici.getIsim());
                hakkimdaTW.setText(kullanici.getHakkimda());
                if (!kullanici.getIsim().isEmpty()){
                    isimTW.setSelection(isimTW.getText().length());
                }
                if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
                    Picasso.get().load(kullanici.getProfilResmi()).into(profilResmi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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