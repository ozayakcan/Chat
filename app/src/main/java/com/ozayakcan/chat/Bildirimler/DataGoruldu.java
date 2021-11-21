package com.ozayakcan.chat.Bildirimler;

public class DataGoruldu {
    private String bildirimTuru;
    private String kisi;

    @SuppressWarnings("unused")
    public DataGoruldu() {
    }

    public DataGoruldu(String bildirimTuru, String kisi) {
        this.bildirimTuru = bildirimTuru;
        this.kisi = kisi;
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
}
