package epamig.dcafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import epamig.dcafe.bancodedados.ControlarBanco;
import epamig.dcafe.model.Poligono;
import epamig.dcafe.sistema.Aplicacao;

public class SincronizacaoActivity extends AppCompatActivity {

    ProgressDialog mDialog;
    public static String IP = new Aplicacao().getIP();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Sincronizando os dados. Isso pode levar alguns minutos.");
        mDialog.setCancelable(false);

        //chamarClassePrincipal();
        inserirClassesEMapas();
        pegarValoresWebService();
    }

    //---------------------------Função Assíncrona que pega os valores do Webservice----------------//
    private void pegarValoresWebService() {
        AsyncHttpClient client = new AsyncHttpClient();
        mDialog.show();
        client.get(
                 IP + "poligonos.php", new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mDialog.dismiss();
                        //TODO mensagem de erro
                        String erro = new Exception(throwable).getMessage();
                        Log.i("ERRO", "Erro no download: "+erro);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        getPoligonos(responseString);
                        Log.i("DEU certo", "It's ok");
                        mDialog.dismiss();
                        chamarClassePrincipal();

                    }
                });
    }

    private void chamarClassePrincipal() {
        finish();
        Intent intent = new Intent();
        intent.setClass(SincronizacaoActivity.this, Principal.class);
        startActivity(intent);

    }

    //---------------------------BD Inserindo Classes e Mapas-------------------------------------//
    private void inserirClassesEMapas() {
        ControlarBanco bd = new ControlarBanco(getBaseContext());
        String resultado = bd.insereClasse("Agua", "blue");
        resultado = bd.insereClasse("Cafe", "red");
        resultado = bd.insereClasse("Mata", "green");
        resultado = bd.insereClasse("Outros_usos", "yellow");
        resultado = bd.insereClasse("Area_urbana", "fuchsia");
        /*-------------Mapas------------------------------------*/
        resultado = bd.insereMapa("baepusodis", "Baependi");
        resultado = bd.insereMapa("braso_usodi", "Brazopolis");
        resultado = bd.insereMapa("cachusodis", "Cachoeira de Minas");
        resultado = bd.insereMapa("cambusodis", "Cambuquira");
        resultado = bd.insereMapa("campausodis", "Campanha");
        resultado = bd.insereMapa("carmusodis", "Carmo de Minas");
        resultado = bd.insereMapa("caxamusodis", "Caxambu");
        resultado = bd.insereMapa("converdis", "Conceicao do Rio Verde");
        resultado = bd.insereMapa("concusodis", "Conceicao das Pedras");
        resultado = bd.insereMapa("cristusodis", "Cristina");
        resultado = bd.insereMapa("domusodis", "Dom vicoso");
        resultado = bd.insereMapa("helusodis", "Heliodora");
        resultado = bd.insereMapa("jesuusodis", "Jesuania");
        resultado = bd.insereMapa("lambusodis", "Lambari");
        resultado = bd.insereMapa("natusodis", "Natercia");
        resultado = bd.insereMapa("olimpusodis", "Olimpio Noronha");
        resultado = bd.insereMapa("parais_usodi", "Paraisopolis");
        resultado = bd.insereMapa("pedusodis", "Pedralva");
        resultado = bd.insereMapa("pirusodis", "Piranguinho");
        resultado = bd.insereMapa("pousousodis", "Pouso Alto");
        resultado = bd.insereMapa("sanusodis", "Santa Rita do Sapucai");
        resultado = bd.insereMapa("saousodis", "Sao Goncalo do Sapucai");
        resultado = bd.insereMapa("solouusodis", "Sao Lourenco");
        resultado = bd.insereMapa("saosebusodis", "Sao Sebastiao da Bela Vista");
        resultado = bd.insereMapa("soleusodis", "Soledado de Minas");
    }

    //---------------------------BD Inserindo Polignos--------------------------------------------//
    private void getPoligonos(String jsonString) {
        try {
            JSONObject objJson = new JSONObject(jsonString);
            JSONArray poligonosJson = objJson.getJSONArray("poligono");
            ControlarBanco bd = new ControlarBanco(getBaseContext());

            for (int i = 0; i < poligonosJson.length(); i++) {
                JSONObject jsonPoligono = new JSONObject(poligonosJson.getString(i));
                Poligono objetoPoligono = new Poligono();

                //--------------Pegar o id do mapa e Classe---------------------------------------//
                int idClasse = bd.selecionarIdClasse(jsonPoligono.getString("Classe"));
                int idMapa = bd.selecionarIdMapaNomeMapa(jsonPoligono.getString("Mapa"));

                //--------------Pegar o id do mapa e Classe---------------------------------------//
                objetoPoligono.setCoodernadasPoligono(jsonPoligono.getString("Coodernadas"));
                objetoPoligono.setClassePoligono(idClasse);
                objetoPoligono.setMapaPoligono(idMapa);

                //---------------Salvar no  Banco-------------------------------------------------//
                bd.inserePoligono(objetoPoligono);

            }
        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
    }

}
