package epamig.dcafe.sistema;

import android.app.Application;


import android.app.Application;
import android.graphics.Color;

public class Aplicacao  extends Application {

    private String IP = "http://www.honeycode.com.br/demarcafe/";//http://200.235.75.13/demarcafe/";

    //http://www.honeycode.com.br/demarcafe/";
    ////200.235.75.13
    //200.235.75.13
    //192.168.1.4
    //http://www.honeycode.com.br/demarcafe/
    private static int corClasseAgua = Color.argb(50, 0, 0, 255);
    private static int corClasseCafe = Color.argb(50, 255, 0, 0);
    private static int corClasseMata = Color.argb(50, 0, 255, 0);
    private static int corClasseOutrosUsos = Color.argb(50, 255, 255, 0);
    private static int corClasseAreaUrbana = Color.argb(50, 0, 0, 0);//61, 61, 61

    public String getIP() {
        return IP;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }

    public static int getCorClasseAgua() {
        return corClasseAgua;
    }

    public static void setCorClasseAgua(int corClasseAgua) {
        Aplicacao.corClasseAgua = corClasseAgua;
    }

    public static int getCorClasseCafe() {
        return corClasseCafe;
    }

    public static void setCorClasseCafe(int corClasseCafe) {
        Aplicacao.corClasseCafe = corClasseCafe;
    }

    public static int getCorClasseMata() {
        return corClasseMata;
    }

    public static void setCorClasseMata(int corClasseMata) {
        Aplicacao.corClasseMata = corClasseMata;
    }

    public static int getCorClasseOutrosUsos() {
        return corClasseOutrosUsos;
    }

    public static void setCorClasseOutrosUsos(int corClasseOutrosUsos) {
        Aplicacao.corClasseOutrosUsos = corClasseOutrosUsos;
    }

    public static int getCorClasseAreaUrbana() {
        return corClasseAreaUrbana;
    }

    public static void setCorClasseAreaUrbana(int corClasseAreaUrbana) {
        Aplicacao.corClasseAreaUrbana = corClasseAreaUrbana;
    }
}