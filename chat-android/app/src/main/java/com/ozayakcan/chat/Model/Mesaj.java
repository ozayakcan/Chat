package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Mesaj {

    String mesaj = "";
    long tarih = 0;
    long mesajDurumu = Veritabani.MesajDurumuGonderiliyor;
    boolean gonderen = true;
    boolean goruldu = false;

    public Mesaj() {
    }

    public Mesaj(String mesaj, long tarih, long mesajDurumu, boolean gonderen, boolean goruldu) {
        this.mesaj = mesaj;
        this.tarih = tarih;
        this.mesajDurumu = mesajDurumu;
        this.gonderen = gonderen;
        this.goruldu = goruldu;
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

    public void setGonderen(boolean gonderen) {
        this.gonderen = gonderen;
    }

    public boolean isGoruldu() {
        return goruldu;
    }

    public void setGoruldu(boolean goruldu) {
        this.goruldu = goruldu;
    }
}
