package com.ozayakcan.chat.Resim;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ResimlerClass {

    private final Context mContext;
    public UploadTask resimYukleUploadTask;
    public static final String VarsayilanResimUzantisi = ".jpg";

    public String MedyaKonumu(){
        return mContext.getExternalFilesDir("Media").getAbsolutePath();
    }
    public static String Sticker_Dosya_Adi = "stickers";

    public ResimlerClass(Context context) {mContext = context;}

    public static ResimlerClass getInstance(Context context){
        return new ResimlerClass(context);
    }

    public void ResimGoster(String resim, ImageView resimIW, int varsayilanResimID){
        Picasso.get().load(resim).networkPolicy(NetworkPolicy.OFFLINE).error(varsayilanResimID).into(resimIW, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(resim).error(varsayilanResimID).into(resimIW);
            }
        });
    }
    public void ResimGoster(File dosya, ImageView resimIW, int varsayilanResimID){
        Picasso.get().load(dosya).error(varsayilanResimID).into(resimIW);
    }
    public void GaleriyiAc(String isim) {
        Intent intent = new Intent(mContext, GaleriActivity.class);
        intent.putExtra(Veritabani.IsimKey, isim);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.asagidan_yukari_giris, R.anim.asagidan_yukari_cikis);
    }

    public void KamerayiAc() {
        mContext.startActivity(new Intent(mContext, KameraActivity.class));
        ((Activity) mContext).overridePendingTransition(R.anim.asagidan_yukari_giris, R.anim.asagidan_yukari_cikis);
    }

    public interface ResimYukleSonuc{
        void Basarili(String resimUrl);
        void Basarisiz(String hata);
    }
    public void ResimYukle(Uri resim, String konum, LinearLayout progressBarLayout, ResimYukleSonuc resimYukleSonuc){
        if (resimYukleUploadTask != null && resimYukleUploadTask.isInProgress()) {
            resimYukleSonuc.Basarisiz(mContext.getString(R.string.upload_in_progress));
            Toast.makeText(mContext, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
        } else {
            progressBarLayout.setVisibility(View.VISIBLE);
            if (resim != null){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(konum);
                resimYukleUploadTask = storageReference.putFile(resim);
                resimYukleUploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        resimYukleSonuc.Basarisiz(task.getException().getLocalizedMessage() == null ? "" : task.getException().getLocalizedMessage());
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String resimKonumu = downloadUri.toString();

                        resimYukleSonuc.Basarili(resimKonumu);
                    } else {
                        resimYukleSonuc.Basarisiz(mContext.getString(R.string.something_went_wrong));
                    }
                    progressBarLayout.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    resimYukleSonuc.Basarisiz(e.getLocalizedMessage());
                    progressBarLayout.setVisibility(View.GONE);
                });
            }else{
                resimYukleSonuc.Basarisiz(mContext.getString(R.string.no_image_selected));
                progressBarLayout.setVisibility(View.GONE);
            }
        }
    }
    public void ProfilResmiDegistir(FirebaseUser firebaseUser,
                                    String resimBaglantisi,
                                    ImageView resimIW,
                                    ActivityResultLauncher<Intent> activityResultLauncher,
                                    ActivityResultLauncher<String> kameraIzniResultLauncher,
                                    ActivityResultLauncher<String> dosyaIzniResultLauncher) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(mContext).inflate(R.layout.layout_resim_sec, (LinearLayout) ((Activity) mContext).findViewById(R.id.altMenuLayout));
        altMenuView.findViewById(R.id.resimCek).setOnClickListener(v -> {
            if (Izinler.getInstance(mContext).KontrolEt(Manifest.permission.CAMERA)){
                KameradanYukle(activityResultLauncher);
            }else{
                Izinler.getInstance(mContext).Sor(Manifest.permission.CAMERA, kameraIzniResultLauncher);
            }
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.galeridenSec).setOnClickListener(v -> {
            if (Izinler.getInstance(mContext).KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
                GaleridenYukle(activityResultLauncher);
            }else{
                Izinler.getInstance(mContext).Sor(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
            }
            bottomSheetDialog.dismiss();
        });
        if (resimBaglantisi.equals(Veritabani.VarsayilanDeger)){
            altMenuView.findViewById(R.id.resmiKaldir).setVisibility(View.GONE);
        }
        altMenuView.findViewById(R.id.resmiKaldir).setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.ProfilResmiKey);
			databaseReference.keepSynced(true);
            databaseReference.setValue(Veritabani.VarsayilanDeger);
            ResimGoster(Veritabani.VarsayilanDeger, resimIW, R.drawable.ic_profil_resmi);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(altMenuView);
        bottomSheetDialog.show();
    }

    public void KameradanYukle(ActivityResultLauncher<Intent> activityResultLauncher){
        Intent kameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(kameraIntent);
    }
    public void GaleridenYukle(ActivityResultLauncher<Intent> activityResultLauncher){
        Intent galeriIntent = new Intent();
        galeriIntent.setType("image/*");
        galeriIntent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(galeriIntent);
    }
    public void ResimKirp(Uri sonuc){
        Uri kaydedilenResim = Uri.fromFile(new File(mContext.getCacheDir(), System.currentTimeMillis()+""));
        UCrop.of(sonuc, kaydedilenResim)
                .withAspectRatio(1,1)
                .withMaxResultSize(640,640)
                .withOptions(profilResmiUCropAyarlari())
                .start(((Activity) mContext),  UCrop.REQUEST_CROP);
    }
    private UCrop.Options profilResmiUCropAyarlari(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        options.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        options.setToolbarWidgetColor(ContextCompat.getColor(mContext, R.color.white));
        options.setToolbarTitle(mContext.getString(R.string.crop_profile_photo));
        return options;
    }

    public void ProfilResmiGoruntule(String isim, String profilResmi) {
        Intent intent = new Intent(mContext, ProfilResmiGoruntuleActivity.class);
        intent.putExtra(Veritabani.IsimKey, isim);
        intent.putExtra(Veritabani.ProfilResmiKey, profilResmi);
        mContext.startActivity(intent);
    }
    public interface KopyalaListener{
        void Tamamlandi(String konum);
        void Tamamlanamadi(String hata);
    }
    public void Kopyala(File kaynak, File hedef, String kopyalanacakKonum, KopyalaListener kopyalaListener) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                File klasor = new File(kopyalanacakKonum);
                if (!klasor.exists()) {
                    boolean b = klasor.mkdirs();
                    Log.d("Kopyala", b+"");
                }
                try (InputStream inputStream = new FileInputStream(kaynak)) {
                    try (OutputStream out = new FileOutputStream(hedef)) {
                        byte[] bytes = new byte[(int) kaynak.length()];
                        int uzunluk;
                        while ((uzunluk = inputStream.read(bytes)) > 0) {
                            out.write(bytes, 0, uzunluk);
                        }
                    }
                } catch (IOException e) {
                    handler.post(() -> kopyalaListener.Tamamlanamadi(e.getLocalizedMessage()));
                }finally {
                    handler.post(() -> kopyalaListener.Tamamlandi(hedef.getAbsolutePath()));
                }
            }
        });
    }
}
