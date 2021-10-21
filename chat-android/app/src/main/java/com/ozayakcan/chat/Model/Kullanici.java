package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Utils.Veritabani;

public class Kullanici {

    private String ID = "";

    private String isim = "";

    private String profilResmi = Veritabani.VarsayilanDeger;

    private String telefon = "";

    private String hakkimda = "";

    public Kullanici() {
    }

    public Kullanici(String ID, String isim, String profilResmi, String telefon, String hakkimda) {
        this.ID = ID;
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
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
}
