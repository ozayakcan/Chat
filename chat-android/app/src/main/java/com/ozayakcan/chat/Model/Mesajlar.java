package com.ozayakcan.chat.Model;

public class Mesajlar {
    String isim = "";
    String profilResmi = "";
    String gonderen = "";
    String alan = "";
    String sonMesaj = "";
    long okunmamiMesajSayisi = 0;
    String tarih = "";
    String gonderilmeDurumu = "";

    public Mesajlar() {
    }

    public Mesajlar(String isim, String profilResmi, String gonderen, String alan, String sonMesaj, long okunmamiMesajSayisi, String tarih, String gonderilmeDurumu) {
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.gonderen = gonderen;
        this.alan = alan;
        this.sonMesaj = sonMesaj;
        this.okunmamiMesajSayisi = okunmamiMesajSayisi;
        this.tarih = tarih;
        this.gonderilmeDurumu = gonderilmeDurumu;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getProfilResmi() {
        return profilResmi;
    }

    public void setProfilResmi(String profilResmi) {
        this.profilResmi = profilResmi;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getAlan() {
        return alan;
    }

    public void setAlan(String alan) {
        this.alan = alan;
    }

    public String getSonMesaj() {
        return sonMesaj;
    }

    public long getOkunmamiMesajSayisi() {
        return okunmamiMesajSayisi;
    }

    public void setOkunmamiMesajSayisi(long okunmamiMesajSayisi) {
        this.okunmamiMesajSayisi = okunmamiMesajSayisi;
    }

    public void setSonMesaj(String sonMesaj) {
        this.sonMesaj = sonMesaj;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getGonderilmeDurumu() {
        return gonderilmeDurumu;
    }

    public void setGonderilmeDurumu(String gonderilmeDurumu) {
        this.gonderilmeDurumu = gonderilmeDurumu;
    }
}