package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Kisi {
    private String ID = "";

    private String isim = "";

    private String telefon = "";

    public Kisi() {
    }

    public Kisi(String ID, String isim, String telefon) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
