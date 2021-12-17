package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Kullanici {

    private String ID = "";

    private String isim = "";

    private boolean onlineDurumu = false;

    private String profilResmi = Veritabani.VarsayilanDeger;

    private String telefon = "";

    private String hakkimda = "";

    private long kayitZamani;

    private long sonGorulme = 0;

    private String fcmToken = "0";

    private boolean bildirimDurumu = true;

    private boolean bildirimSesi = true;

    private boolean bildirimOncelik = true;

    private long bildirimTitresim = 0;

    public Kullanici() {
        kayitZamani = System.currentTimeMillis();
        sonGorulme = System.currentTimeMillis();
    }

    public Kullanici(String ID, String isim, String telefon, String hakkimda, boolean onlineDurumu) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        kayitZamani = System.currentTimeMillis();
        this.onlineDurumu = onlineDurumu;
    }
    public Kullanici(String ID, String isim, String telefon, String hakkimda,
                     long kayitZamani, boolean onlineDurumu) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
        this.onlineDurumu = onlineDurumu;
    }

    public Kullanici(String ID, String isim, String profilResmi, String telefon,
                     String hakkimda, long kayitZamani, boolean onlineDurumu,
                     long sonGorulme, String fcmToken, boolean bildirimDurumu,
                     boolean bildirimSesi, boolean bildirimOncelik, long bildirimTitresim) {
        this.ID = ID;
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
        this.onlineDurumu = onlineDurumu;
        this.sonGorulme = sonGorulme;
        this.fcmToken = fcmToken;
        this.bildirimDurumu = bildirimDurumu;
        this.bildirimSesi = bildirimSesi;
        this.bildirimOncelik = bildirimOncelik;
        this.bildirimTitresim = bildirimTitresim;
    }

    public String getID() {
        return ID;
    }

    @SuppressWarnings("unused")
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public boolean isOnlineDurumu() {
        return onlineDurumu;
    }

    @SuppressWarnings("unused")
    public void setOnlineDurumu(boolean onlineDurumu) {
        this.onlineDurumu = onlineDurumu;
    }

    public String getProfilResmi() {
        return profilResmi;
    }

    public void setProfilResmi(String profilResmi) {
        this.profilResmi = profilResmi;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    @SuppressWarnings("unused")
    public void setHakkimda(String hakkimda) {
        this.hakkimda = hakkimda;
    }

    @SuppressWarnings("unused")
    public long getKayitZamani() {
        return kayitZamani;
    }

    @SuppressWarnings("unused")
    public void setKayitZamani(long kayitZamani) {
        this.kayitZamani = kayitZamani;
    }

    @SuppressWarnings("unused")
    public long getSonGorulme() {
        return sonGorulme;
    }

    @SuppressWarnings("unused")
    public void setSonGorulme(long sonGorulme) {
        this.sonGorulme = sonGorulme;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    @SuppressWarnings("unused")
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isBildirimDurumu() {
        return bildirimDurumu;
    }

    public void setBildirimDurumu(boolean bildirimDurumu) {
        this.bildirimDurumu = bildirimDurumu;
    }

    public boolean isBildirimSesi() {
        return bildirimSesi;
    }

    public void setBildirimSesi(boolean bildirimSesi) {
        this.bildirimSesi = bildirimSesi;
    }

    public boolean isBildirimOncelik() {
        return bildirimOncelik;
    }

    public void setBildirimOncelik(boolean bildirimOncelik) {
        this.bildirimOncelik = bildirimOncelik;
    }

    public long getBildirimTitresim() {
        return bildirimTitresim;
    }

    public void setBildirimTitresim(long bildirimTitresim) {
        this.bildirimTitresim = bildirimTitresim;
    }
}
