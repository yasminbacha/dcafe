package epamig.dcafe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import epamig.dcafe.bancodedados.ControlarBanco;
import epamig.dcafe.model.Demarcacao;
import epamig.dcafe.model.Poligono;
import epamig.dcafe.model.Usuario;
import epamig.dcafe.sistema.Aplicacao;

public class SincronizarDemarcacoes {
/*
    UserDemarcacaoTask mAuthTask = null;

    ProgressDialog mDialog;
    public static String IP = new Aplicacao().getIP();

    Context context;
    public SincronizarDemarcacoes(Context context){
        this.context = context;
        Sincronizar();
    }
    public void Sincronizar() {

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Sincronizando os dados.");
        mDialog.setCancelable(false);
        mDialog.show();
        ControlarBanco bd = new ControlarBanco(context);

        List<Demarcacao> ListDemarcacoes = bd.ListarTodasDemarcacoes();

        mAuthTask = new UserDemarcacaoTask(ListDemarcacoes);
        mAuthTask.execute((Void) null);
    }


    public class UserDemarcacaoTask extends AsyncTask<Void, Void, Boolean> {

        List<Demarcacao> Demarcacaos;
        String url = IP + "inserirdemarcacao.php";

        UserDemarcacaoTask(List<Demarcacao> ListDemarcacaos) {
            Demarcacaos = ListDemarcacaos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            for (Demarcacao demarcacao : Demarcacaos) {
                String coodernadas = ConverteDemarcacoes(demarcacao.getCoodernadasDemarcacao());
                demarcacao.setCoodernadasDemarcacao(coodernadas);
                ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
                valores.add(new BasicNameValuePair("Usuario_idUsuario", "" + demarcacao.getUsuario_idUsuario()));
                valores.add(new BasicNameValuePair("Poligono_idPoligono", "" + demarcacao.getPoligono_idPoligono()));
                valores.add(new BasicNameValuePair("Classe_idClasse", "" + demarcacao.getClasse_idClasse()));
                valores.add(new BasicNameValuePair("comentariosDemarcacao", demarcacao.getComentariosDemarcacao()));
                valores.add(new BasicNameValuePair("coodernadasDemarcacao", demarcacao.getCoodernadasDemarcacao()));
                valores.add(new BasicNameValuePair("statusDemarcacao", demarcacao.getStatusDemarcacao()));

                try {
                    post.setEntity(new UrlEncodedFormEntity(valores));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    HttpResponse resposta = client.execute(post);
                    String json = EntityUtils.toString(resposta.getEntity());
                    try {
                        //TODO apagar o banco
                        ControlarBanco bd = new ControlarBanco(context);
                        bd.deletarTodasDemarcacoes();

                        JSONObject objJson = new JSONObject(json);
                        JSONArray demarcacaoJson = objJson.getJSONArray("demarcacao");

                        for (int i = 0; i <demarcacaoJson.length(); i++) {
                            JSONObject jsonDemarcacao = new JSONObject(demarcacaoJson.getString(i));
                            Demarcacao demarcacaoBaixada = new Demarcacao();

                            demarcacaoBaixada.setUsuario_idUsuario(jsonDemarcacao.getInt("Usuario_idUsuario"));
                            demarcacaoBaixada.setPoligono_idPoligono(jsonDemarcacao.getInt("Poligono_idPoligono"));
                            demarcacaoBaixada.setClasse_idClasse(jsonDemarcacao.getInt("Classe_idClasse"));
                            demarcacaoBaixada.setComentariosDemarcacao(jsonDemarcacao.getString("comentariosDemarcacao"));
                            demarcacaoBaixada.setCoodernadasDemarcacao(jsonDemarcacao.getString("coodernadasPoligono"));
                            demarcacaoBaixada.setStatusDemarcacao(jsonDemarcacao.getString("statusDemarcacao"));
                            demarcacaoBaixada.setFlagSincronizado(jsonDemarcacao.getInt("flagSincronizado"));
                            //---------------Salvar no  Banco-------------------------------------------------//
                            bd.insereDemarcacao(demarcacaoBaixada);
                        }
                    } catch (JSONException e) {
                        Log.i("ERRO", e.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mDialog.dismiss();

                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        private String ConverteDemarcacoes(String poligono) {
            String poligonoList = "";

            int posicaoInicio = poligono.indexOf("(");
            int posicaoFinal = poligono.indexOf("]");
            String coodernadas = poligono.substring(posicaoInicio, posicaoFinal);
            String[] vetorCoodernadas = coodernadas.split(", lat/lng: ");
            for (int i = 0; i < vetorCoodernadas.length; i++) {
                String[] latlong = vetorCoodernadas[i].split(",");
                String lati = latlong[0].substring(latlong[0].indexOf("(") + 1);
                String longi = latlong[1].substring(0, latlong[1].indexOf(")"));
                Double longii = Double.parseDouble(longi);
                Double lat = Double.parseDouble(lati);
                poligonoList = poligonoList + longii + " "+lat+", ";
            }

            posicaoFinal = poligonoList.indexOf(",");
            String Primeiro = poligonoList.substring(0, posicaoFinal);
            poligonoList += Primeiro;

            return poligonoList;
        }

    }
*/
}

//
//TODO  selecionar todos as demarcações
//TODO enviar demarcações
//Apagar o banco de dados
//Baixar todas as demarcações daqioe
//Inserir todas as demarcações
