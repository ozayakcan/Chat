package com.ozayakcan.chat.Bildirimler;

public class DataMesaj {
    private String bildirimTuru;

    @SuppressWarnings("unused")
    public DataMesaj() {
    }

    public DataMesaj(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }

    @SuppressWarnings("unused")
    public String getBildirimTuru() {
        return bildirimTuru;
    }

    @SuppressWarnings("unused")
    public void setBildirimTuru(String bildirimTuru) {
        this.bildirimTuru = bildirimTuru;
    }
}
