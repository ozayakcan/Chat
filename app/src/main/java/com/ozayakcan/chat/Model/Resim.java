package com.ozayakcan.chat.Model;

import android.net.Uri;

public class Resim {
    public String konum;

    public Resim() {
    }

    public Resim(String konum) {
        this.konum = konum;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }
}
