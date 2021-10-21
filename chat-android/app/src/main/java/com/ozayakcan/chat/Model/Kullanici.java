package com.ozayakcan.chat.Model;

public class Kullanici {

    private String ID;

    private String isim;

    private String profiResmi;

    private String telefon;

    private String hakkimda;

    public Kullanici() {

    }

    public Kullanici(String ID, String isim, String profiResmi, String telefon, String hakkimda) {
        this.ID = ID;
        this.isim = isim;
        this.profiResmi = profiResmi;
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

    public String getProfiResmi() {
        return profiResmi;
    }

    public void setProfiResmi(String profiResmi) {
        this.profiResmi = profiResmi;
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
