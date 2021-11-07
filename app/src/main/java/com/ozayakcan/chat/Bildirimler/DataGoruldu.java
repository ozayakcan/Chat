package com.ozayakcan.chat.Bildirimler;

public class DataGoruldu {
    private String bildirimTuru;
    private String kisi;

    public DataGoruldu() {
    }

    public DataGoruldu(String bildirimTuru, String kisi) {
        this.bildirimTuru = bildirimTuru;
        this.kisi = kisi;
    }

    public String getBildirimTuru() {
        return bildirimTuru;
    }

    public void setBildirimTuru(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }

    public String getKisi() {
        return kisi;
    }

    public void setKisi(String kisi) {
        this.kisi = kisi;
    }
}
