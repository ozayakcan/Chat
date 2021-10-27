package com.ozayakcan.chat.Model;

public class Mesajlar {

    public Kullanici kullanici;

    public Mesaj mesaj;

    public String isim;

    public long okumamisMesaj;

    public Mesajlar() {
    }

    public Mesajlar(Kullanici kullanici, Mesaj mesaj, String isim, long okumamisMesaj) {
        this.kullanici = kullanici;
        this.mesaj = mesaj;
        this.isim = isim;
        this.okumamisMesaj = okumamisMesaj;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Mesaj getMesaj() {
        return mesaj;
    }

    public void setMesaj(Mesaj mesaj) {
        this.mesaj = mesaj;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public long getOkumamisMesaj() {
        return okumamisMesaj;
    }

    public void setOkumamisMesaj(long okumamisMesaj) {
        this.okumamisMesaj = okumamisMesaj;
    }
}
