package epamig.dcafe.sistema;

import android.app.Application;


import android.app.Application;

public class Aplicacao  extends Application {

    private String IP = "http://www.honeycode.com.br/demarcafe/";
    ////"http://192.168.0.100:8080/demarcafe/";
    //IP;
            //"192.168.0.100";
    //IP ETHERNET = "200.235.75.13";
    // "192.168.56.1";//"200.235.88.74";// //200.235.85.211"; //WIFI http://200.235.85.211/ //ETHERNET";


    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}