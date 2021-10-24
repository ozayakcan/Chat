package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Kullanici {

    private String ID = "";

    private String isim = "";

    private String profilResmi = Veritabani.VarsayilanDeger;

    private String telefon = "";

    private String hakkimda = "";

    private long kayitZamani = 0;

    public Kullanici() {
        kayitZamani = System.currentTimeMillis();
    }

    public Kullanici(String ID, String isim, String telefon, String hakkimda) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        kayitZamani = System.currentTimeMillis();
    }
    public Kullanici(String ID, String isim, String telefon, String hakkimda, long kayitZamani) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
    }

    public Kullanici(String ID, String isim, String profilResmi, String telefon, String hakkimda, long kayitZamani) {
        this.ID = ID;
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
    }

    public String getID() {
        return ID;
    }

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

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    public void setHakkimda(String hakkimda) {
        this.hakkimda = hakkimda;
    }

    public long getKayitZamani() {
        return kayitZamani;
    }

    public void setKayitZamani(long kayitZamani) {
        this.kayitZamani = kayitZamani;
    }
}
