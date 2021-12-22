package com.ozayakcan.chat.Ozellik;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ozayakcan.chat.Model.Mesaj;

import java.util.ArrayList;
import java.util.List;

public class MesajFonksiyonlari {

    private final Context mContext;

    public MesajFonksiyonlari(Context context){
        this.mContext = context;
    }

    public static MesajFonksiyonlari getInstance(Context context){
        return new MesajFonksiyonlari(context);
    }

    public static final String KaydedilecekTur= "MESAJLAR";
    public static final String KaydedilecekTurArsiv= "ARSIVMESAJLAR";
    public static final String BildirimGonderilecekKisiler = "BILDIRIMGONDERILECEKKISI";
    public Mesaj MesajiKaydet(String key, String kisi, String mesaj, long mesajDurumu, boolean gonderen){
        List<Mesaj> mesajList;
        Mesaj mesaj1 = new Mesaj(key, mesaj, System.currentTimeMillis(), mesajDurumu, gonderen, false, false, 0);
        Gson gson = new Gson();
        String mesajlar = SharedPreference.getInstance(mContext).GetirStringOzel(KaydedilecekTur, kisi, "");
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
        SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTur, kisi, kaydedilecekMesaj);
        return mesaj1;
    }
    public List<Mesaj> MesajlariGetir(String kisi, String getirilecekyer){
        List<Mesaj> mesajList;
        Gson gson = new Gson();
        String mesajlar = SharedPreference.getInstance(mContext).GetirStringOzel(getirilecekyer, kisi, "");
        if (mesajlar.equals("")){
            mesajList = new ArrayList<>();
        }else{
            mesajList = gson.fromJson(mesajlar, new TypeToken<List<Mesaj>>(){}.getType());
        }
        return mesajList;
    }
    public List<String> CokKisiliMesajlariGetir(String gosterilecekyer){
        return SharedPreference.getInstance(mContext).CokluStringGetirOzel(gosterilecekyer);
    }
    public void MesajDuzenle(String kisi, List<Mesaj> mesajList){
        Gson gson = new Gson();
        String kaydedilecekMesaj = gson.toJson(mesajList);
        kaydedilecekMesaj = DegiskenleriDuzenle(kaydedilecekMesaj);
        SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTur,kisi, kaydedilecekMesaj);
    }
    public void MesajDuzenle(String kisi, String duzenlenecekYer, List<Mesaj> mesajList){
        Gson gson = new Gson();
        String kaydedilecekMesaj = gson.toJson(mesajList);
        kaydedilecekMesaj = DegiskenleriDuzenle(kaydedilecekMesaj);
        SharedPreference.getInstance(mContext).KaydetStringOzel(duzenlenecekYer,kisi, kaydedilecekMesaj);
    }
    private String DegiskenleriDuzenle(String mesaj){
        return mesaj.replace("\"tarihGoster\":true", "\"tarihGoster\":false").replace("\"secildi\":true", "\"secildi\":false");
    }
    public void MesajlariSil(String kisi, String silinecekYer){
        SharedPreference.getInstance(mContext).KaydetStringOzel(silinecekYer, kisi, "");
    }
    public void MesajSil(String kisi, String silinecekYer, int sira){
        List<Mesaj> mesajlar = MesajlariGetir(kisi, silinecekYer);
        mesajlar.remove(sira);
        MesajDuzenle(kisi, silinecekYer, mesajlar);
    }
    public void MesajlarArsiv(String kisi, boolean arsivle){
        String arsivMesaj  = SharedPreference.getInstance(mContext).GetirStringOzel(KaydedilecekTurArsiv, kisi, "");
        Gson gson = new Gson();
        List<Mesaj> arsivList;
        if (arsivMesaj.equals("")){
            arsivList = new ArrayList<>();
        }else{
            arsivList = gson.fromJson(arsivMesaj, new TypeToken<List<Mesaj>>(){}.getType());
        }
        List<Mesaj> mesajList;
        String normalMesaj = SharedPreference.getInstance(mContext).GetirStringOzel(KaydedilecekTur, kisi, "");
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
                SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTurArsiv, kisi, arsivStr);
                SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTur, kisi, "");
            }
        }else{
            if (arsivList.size() > 0){
                mesajList.addAll(0, arsivList);
                arsivList.clear();
                String mesajlarStr = gson.toJson(mesajList);
                SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTur, kisi, mesajlarStr);
                SharedPreference.getInstance(mContext).KaydetStringOzel(KaydedilecekTurArsiv, kisi, "");
            }
        }
    }
    public List<String> BildirimGonderilecekKisiler(){
        return SharedPreference.getInstance(mContext).CokluStringGetirOzel(BildirimGonderilecekKisiler);
    }
    public void BildirimGonderilecekKisiyiEkle(String telefon){
        SharedPreference.getInstance(mContext).KaydetStringOzel(BildirimGonderilecekKisiler, telefon, "gonder");
    }
    public void BildirimGonderilecekKisiyiSil(String telefon){
        SharedPreference.getInstance(mContext).TemizleOzel(BildirimGonderilecekKisiler, telefon);
    }
}
