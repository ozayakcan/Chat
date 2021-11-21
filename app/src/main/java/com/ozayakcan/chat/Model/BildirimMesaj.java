package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class BildirimMesaj implements Comparable<BildirimMesaj>{
    private String ID = "";

    private String isim = "";

    private String profilResmi = Veritabani.VarsayilanDeger;

    private String telefon = "";

    private String mesaj = "";

    private long tarih = 0;

    private long mesajSayisi = 0;

    @SuppressWarnings("unused")
    public BildirimMesaj() {
    }

    public BildirimMesaj(String ID, String isim, String profilResmi, String telefon, String mesaj, long tarih, long mesajSayisi) {
        this.ID = ID;
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.telefon = telefon;
        this.mesaj = mesaj;
        this.tarih = tarih;
        this.mesajSayisi = mesajSayisi;
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

    public String getProfilResmi() {
        return profilResmi;
    }

    public void setProfilResmi(String profilResmi) {
        this.profilResmi = profilResmi;
    }

    public String getTelefon() {
        return telefon;
    }

    @SuppressWarnings("unused")
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public long getTarih() {
        return tarih;
    }

    @SuppressWarnings("unused")
    public void setTarih(long tarih) {
        this.tarih = tarih;
    }

    public long getMesajSayisi() {
        return mesajSayisi;
    }

    @SuppressWarnings("unused")
    public void setMesajSayisi(long mesajSayisi) {
        this.mesajSayisi = mesajSayisi;
    }

    @Override
    public int compareTo(BildirimMesaj o) {
        return Long.compare(o.getTarih(), this.getTarih());
    }
}
