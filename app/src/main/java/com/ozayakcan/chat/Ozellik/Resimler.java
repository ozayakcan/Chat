package com.ozayakcan.chat.Ozellik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.ozayakcan.chat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class Resimler {

    private final Context mContext;
    public UploadTask resimYukleUploadTask;
    public static final String VarsayilanResimUzantisi = ".jpg";

    public Resimler(Context context) {mContext = context;}

    @SuppressLint("UseCompatLoadingForDrawables")
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

    public void ResimYukle(FirebaseUser firebaseUser, Uri resim, ImageView gosterilecekIW, String konum, LinearLayout progressBarLayout){
        if (resimYukleUploadTask != null && resimYukleUploadTask.isInProgress()) {
            Toast.makeText(mContext, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
        } else {
            progressBarLayout.setVisibility(View.VISIBLE);
            if (resim != null){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(konum);
                resimYukleUploadTask = storageReference.putFile(resim);
                resimYukleUploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String resimKonumu = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.ProfilResmiKey);
						reference.setValue(resimKonumu);
                        if (gosterilecekIW != null){
                            ResimGoster(resimKonumu, gosterilecekIW, R.drawable.ic_profil_resmi);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                    progressBarLayout.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    progressBarLayout.setVisibility(View.GONE);
                });
            }else{
                Toast.makeText(mContext, mContext.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
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
        Izinler izinler = new Izinler(mContext);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(mContext).inflate(R.layout.layout_resim_sec, (LinearLayout) ((Activity) mContext).findViewById(R.id.altMenuLayout));
        altMenuView.findViewById(R.id.resimCek).setOnClickListener(v -> {
            if (izinler.KontrolEt(Manifest.permission.CAMERA)){
                KameradanYukle(activityResultLauncher);
            }else{
                izinler.Sor(Manifest.permission.CAMERA, kameraIzniResultLauncher);
            }
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.galeridenSec).setOnClickListener(v -> {
            if (izinler.KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
                GaleridenYukle(activityResultLauncher);
            }else{
                izinler.Sor(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
            }
            bottomSheetDialog.dismiss();
        });
        if (resimBaglantisi.equals(Veritabani.VarsayilanDeger)){
            altMenuView.findViewById(R.id.resmiKaldir).setVisibility(View.GONE);
        }
        altMenuView.findViewById(R.id.resmiKaldir).setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.ProfilResmiKey);
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
}
