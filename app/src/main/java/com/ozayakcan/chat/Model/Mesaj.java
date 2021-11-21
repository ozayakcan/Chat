package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Mesaj {

    String mesajKey = "";
    String mesaj = "";
    long tarih = 0;
    long mesajDurumu = Veritabani.MesajDurumuGonderiliyor;
    boolean gonderen = true;
    boolean goruldu = false;
    boolean tarihGoster = false;

    @SuppressWarnings("unused")
    public Mesaj() {
    }

    public Mesaj(String mesajKey, String mesaj, long tarih, long mesajDurumu, boolean gonderen, boolean goruldu, boolean tarihGoster) {
        this.mesajKey = mesajKey;
        this.mesaj = mesaj;
        this.tarih = tarih;
        this.mesajDurumu = mesajDurumu;
        this.gonderen = gonderen;
        this.goruldu = goruldu;
        this.tarihGoster = tarihGoster;
    }

    @SuppressWarnings("unused")
    public String getMesajKey() {
        return mesajKey;
    }

    public void setMesajKey(String mesajKey) {
        this.mesajKey = mesajKey;
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

    public long getMesajDurumu() {
        return mesajDurumu;
    }

    public void setMesajDurumu(long mesajDurumu) {
        this.mesajDurumu = mesajDurumu;
    }

    public boolean isGonderen() {
        return gonderen;
    }

    @SuppressWarnings("unused")
    public void setGonderen(boolean gonderen) {
        this.gonderen = gonderen;
    }

    public boolean isGoruldu() {
        return goruldu;
    }

    public void setGoruldu(boolean goruldu) {
        this.goruldu = goruldu;
    }

    public boolean isTarihGoster() {
        return tarihGoster;
    }

    public void setTarihGoster(boolean tarihGoster) {
        this.tarihGoster = tarihGoster;
    }
}
