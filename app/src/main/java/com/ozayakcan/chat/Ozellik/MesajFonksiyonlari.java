package com.ozayakcan.chat.Ozellik;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Model.Mesajlar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MesajFonksiyonlari {

    private Context mContext;
    private SharedPreference sharedPreference;

    public MesajFonksiyonlari(Context context){
        this.mContext = context;
        this.sharedPreference = new SharedPreference(mContext);
    }

    public static MesajFonksiyonlari getInstance(Context context){
        return new MesajFonksiyonlari(context);
    }

    public static final String KaydedilecekTur= "MESAJLAR";
    public static final String KaydedilecekTurArsiv= "ARSIVMESAJLAR";
    public static final String BildirimGonderilecekKisiler = "BILDIRIMGONDERILECEKKISI";
    public Mesaj MesajiKaydet(String key, String kisi, String mesaj, long mesajDurumu, boolean gonderen){
        List<Mesaj> mesajList;
        Mesaj mesaj1 = new Mesaj(key, mesaj, System.currentTimeMillis(), mesajDurumu, gonderen, false, false);
        Gson gson = new Gson();
        String mesajlar = sharedPreference.GetirStringOzel(KaydedilecekTur, kisi, "");
        if (mesajlar.equals("")){
            mesajList = new ArrayList<>();
        }else{
            mesajList = gson.fromJson(mesajlar, new TypeToken<List<Mesaj>>(){}.getType());
        }
        if (!gonderen){
            mesaj1.setMesajDurumu(Veritabani.MesajDurumuGonderildi);
        }
        mesajList.add(mesaj1);
        String kaydedilecekMesaj = gson.toJson(mesajList);
        sharedPreference.KaydetStringOzel(KaydedilecekTur, kisi, kaydedilecekMesaj);
        return mesaj1;
    }
    public List<Mesaj> MesajlariGetir(String kisi, String getirilecekyer){
        List<Mesaj> mesajList;
        Gson gson = new Gson();
        String mesajlar = sharedPreference.GetirStringOzel(getirilecekyer, kisi, "");
        if (mesajlar.equals("")){
            mesajList = new ArrayList<>();
        }else{
            mesajList = gson.fromJson(mesajlar, new TypeToken<List<Mesaj>>(){}.getType());
        }
        return mesajList;
    }
    public List<String> CokKisiliMesajlariGetir(String gosterilecekyer){
        return sharedPreference.CokluStringGetirOzel(gosterilecekyer);
    }
    public void MesajDuzenle(String kisi, List<Mesaj> mesajList){
        Gson gson = new Gson();
        String kaydedilecekMesaj = gson.toJson(mesajList);
        kaydedilecekMesaj = kaydedilecekMesaj.replace("\"tarihGoster\":true", "\"tarihGoster\":false");
        sharedPreference.KaydetStringOzel(KaydedilecekTur,kisi, kaydedilecekMesaj);
    }
    public void MesajlariSil(String kisi, String silinecekYer){
        sharedPreference.KaydetStringOzel(silinecekYer, kisi, "");
    }
    public void MesajlarArsiv(String kisi, boolean arsivle){
        String arsivMesaj  = sharedPreference.GetirStringOzel(KaydedilecekTurArsiv, kisi, "");
        Gson gson = new Gson();
        List<Mesaj> arsivList;
        if (arsivMesaj.equals("")){
            arsivList = new ArrayList<>();
        }else{
            arsivList = gson.fromJson(arsivMesaj, new TypeToken<List<Mesaj>>(){}.getType());
        }
        List<Mesaj> mesajList;
        String normalMesaj = sharedPreference.GetirStringOzel(KaydedilecekTur, kisi, "");
        if (normalMesaj.equals("")){
            mesajList = new ArrayList<>();
        }else{
            mesajList = gson.fromJson(normalMesaj, new TypeToken<List<Mesaj>>(){}.getType());
        }
        if (arsivle){
            if (mesajList.size() > 0){
                arsivList.addAll(mesajList);
                mesajList.clear();
                String arsivStr = gson.toJson(arsivList);
                sharedPreference.KaydetStringOzel(KaydedilecekTurArsiv, kisi, arsivStr);
                sharedPreference.KaydetStringOzel(KaydedilecekTur, kisi, "");
            }
        }else{
            if (arsivList.size() > 0){
                mesajList.addAll(0, arsivList);
                arsivList.clear();
                String mesajlarStr = gson.toJson(mesajList);
                sharedPreference.KaydetStringOzel(KaydedilecekTur, kisi, mesajlarStr);
                sharedPreference.KaydetStringOzel(KaydedilecekTurArsiv, kisi, "");
            }
        }
    }
    public List<String> BildirimGonderilecekKisiler(){
        return sharedPreference.CokluStringGetirOzel(BildirimGonderilecekKisiler);
    }
    public void BildirimGonderilecekKisiyiEkle(String telefon){
        sharedPreference.KaydetStringOzel(BildirimGonderilecekKisiler, telefon, "gonder");
    }
    public void BildirimGonderilecekKisiyiSil(String telefon){
        sharedPreference.TemizleOzel(BildirimGonderilecekKisiler, telefon);
    }
}
