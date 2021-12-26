package com.ozayakcan.chat.Bildirimler;

public class DataArama{
    private String bildirimTuru;
    private String kisi;
    private String kamera;

    @SuppressWarnings("unused")
    public DataArama() {
    }

    public DataArama(String bildirimTuru, String kisi, String kamera) {
        this.bildirimTuru = bildirimTuru;
        this.kisi = kisi;
        this.kamera = kamera;
    }

    @SuppressWarnings("unused")
    public String getBildirimTuru() {
        return bildirimTuru;
    }

    @SuppressWarnings("unused")
    public void setBildirimTuru(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }

    @SuppressWarnings("unused")
    public String getKisi() {
        return kisi;
    }

    @SuppressWarnings("unused")
    public void setKisi(String kisi) {
        this.kisi = kisi;
    }

    @SuppressWarnings("unused")
    public String getKamera() {
        return kamera;
    }

    @SuppressWarnings("unused")
    public void setKamera(String kamera) {
        this.kamera = kamera;
    }
}
