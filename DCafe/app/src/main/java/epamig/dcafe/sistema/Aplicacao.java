package epamig.dcafe.sistema;

import android.app.Application;


import android.app.Application;

public class Aplicacao  extends Application {

    private String IP = /*"200.235.75.13:8080/demarcafe/";//*/"http://www.honeycode.com.br/demarcafe/";

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}