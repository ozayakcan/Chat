package com.ozayakcan.chat.Bildirimler;

public class DataMesaj {
    private String bildirimTuru;

    public DataMesaj() {
    }

    public DataMesaj(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }

    public String getBildirimTuru() {
        return bildirimTuru;
    }

    public void setBildirimTuru(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }
}
