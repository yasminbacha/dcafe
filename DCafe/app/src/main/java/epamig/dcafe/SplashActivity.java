package epamig.dcafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private AlertDialog alerta;
    private static  int TEMPO = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean logado = logado();
        boolean conectado = verificaConexao();

        if (logado == true) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, Principal.class);
                    startActivity(intent);
                }
            }, TEMPO);
        } else {
            if (conectado == true) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                        Intent intent = new Intent();
                        intent.setClass(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, TEMPO);
            } else {
                alertaConexaoFalhou();
            }
        }
    }

    public boolean logado() {
        //Restaura as preferencias gravadas
        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        String emailUsuario = settings.getString("emailUsuario", "");

        if (emailUsuario.isEmpty())
            return false;
        else
            return true;
    }

    public boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    private void alertaConexaoFalhou() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sem conexão com a internet");
        builder.setMessage("Para o primeiro uso é necessário internet. Conecte seu celular na internet e prossiga");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alerta = builder.create();
        alerta.show();
    }
}
