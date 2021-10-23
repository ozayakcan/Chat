package com.ozayakcan.chat.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ozayakcan.chat.Login.RegisterActivity;
import com.ozayakcan.chat.R;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class Resimler {

    private final Context mContext;
    public UploadTask resimYukleUploadTask;

    public Resimler(Context context) {mContext = context;}

    @SuppressLint("UseCompatLoadingForDrawables")
    public void ResimGoster(String resim, ImageView resimIW, int varsayilanResimID){
        Picasso.get().load(resim).error(varsayilanResimID).into(resimIW);
    }

    public void ResimYukle(FirebaseUser firebaseUser, Uri resim, ImageView gosterilecekIW, String konum){
        if (resimYukleUploadTask != null && resimYukleUploadTask.isInProgress()) {
            Toast.makeText(mContext, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(mContext.getString(R.string.uploading));
            progressDialog.show();
            if (resim != null){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(konum);
                resimYukleUploadTask = storageReference.putFile(resim);
                resimYukleUploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
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
                    progressDialog.dismiss();
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }else{
                Toast.makeText(mContext, mContext.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }
    public void ProfilResmiDegistir(FirebaseUser firebaseUser, String resimBaglantisi, ImageView resimIW, ActivityResultLauncher<Intent> activityResultLauncher) {
        Izinler izinler = new Izinler(mContext);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(mContext).inflate(R.layout.layout_resim_sec, (LinearLayout) ((Activity) mContext).findViewById(R.id.altMenuLayout));
        altMenuView.findViewById(R.id.resimCek).setOnClickListener(v -> {
            if (izinler.KontrolEt(Manifest.permission.CAMERA)){
                KameradanYukle(activityResultLauncher);
            }else{
                izinler.Sor(Manifest.permission.CAMERA, Izinler.PROFIL_RESMI_KAMERA_IZIN_KODU);
            }
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.galeridenSec).setOnClickListener(v -> {
            if (izinler.KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
                GaleridenYukle(activityResultLauncher);
            }else{
                izinler.Sor(Manifest.permission.READ_EXTERNAL_STORAGE, Izinler.PROFIL_RESMI_DOSYA_IZIN_KODU);
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
        Uri kaydedilenResim = Uri.fromFile(new File(mContext.getCacheDir(), System.currentTimeMillis()+"."+DosyaUzantisi(sonuc)));
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
        options.setStatusBarColor(mContext.getColor(R.color.colorPrimary));
        options.setToolbarColor(mContext.getColor(R.color.colorPrimary));;
        options.setToolbarTitle(mContext.getString(R.string.crop_profile_photo));
        return options;
    }
    public String DosyaUzantisi(Uri dosya){
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(dosya));
    }
}
