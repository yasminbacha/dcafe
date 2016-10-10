package epamig.dcafe;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import epamig.dcafe.adaptadorCustomizado.CustomAdapterListaDemarcacoes;
import epamig.dcafe.bancodedados.ControlarBanco;
import epamig.dcafe.model.Demarcacao;
import epamig.dcafe.model.Poligono;

public class ListarDemarcacoesActivity extends AppCompatActivity {

    public ControlarBanco bd;

    private ListView lista;

    int[] IdDemarcacaoList;
    String[] ClasseOriginalList;
    String[] ClasseDemarcadaList;
    String[] CidadeList;
    String[] ComentariosList;
    String[] RedesenhoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_demarcacoes);

        //Preencher Lista
        bd = new ControlarBanco(getBaseContext());
        List<Demarcacao> ListDemarcacoes = bd.ListarTodasDemarcacoes();
        int tamanhoLista = ListDemarcacoes.size();

        IdDemarcacaoList = new int[tamanhoLista];
        ClasseOriginalList = new String[tamanhoLista];
        ClasseDemarcadaList = new String[tamanhoLista];
        CidadeList = new String[tamanhoLista];
        ComentariosList = new String[tamanhoLista];
        RedesenhoList = new String[tamanhoLista];

        for (int i = 0; i < tamanhoLista; i++) {
            String ClasseOriginal;
            String CidadePoligono;
            String coodernadasRedesenhada = ListDemarcacoes.get(i).getCoodernadasDemarcacao();
            int idPoligono = ListDemarcacoes.get(i).getPoligono_idPoligono();
            if(idPoligono > 0) {
                Poligono poli = bd.selecionarPoligonoPorId(ListDemarcacoes.get(i).getPoligono_idPoligono());
                ClasseOriginal = "Uso inicial: " + bd.selecionarNomeClassePorId(poli.getClassePoligono());
                CidadePoligono = "Cidade: " + bd.selecionarCidadePorIdMapa(poli.getMapaPoligono());
            }else{
                ClasseOriginal = "Nova área";
                List<LatLng> list = criarPoligonoRedesenhado(coodernadasRedesenhada);
                double latitude = list.get(0).latitude;
                double longitude = list.get(0).longitude;
                String cidade = "";
                try {
                    cidade = buscarCidade(latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CidadePoligono = ("Cidade: "+cidade);
            }
            int idDemarcao = ListDemarcacoes.get(i).getIdDemarcacao();
            Log.i("LOG", "ID: "+idDemarcao);
            String ClasseDemarcada = "Uso demarcado: "+bd.selecionarNomeClassePorId(ListDemarcacoes.get(i).getClasse_idClasse());
            String Comentarios = "Comentários: "+ListDemarcacoes.get(i).getComentariosDemarcacao();


            if(coodernadasRedesenhada != null && !coodernadasRedesenhada.isEmpty()){
                coodernadasRedesenhada = "Área foi demarcada novamente";
            }else{
                coodernadasRedesenhada = "Área não foi demarcada";
            }


            IdDemarcacaoList[i] = idDemarcao;
            ClasseDemarcadaList[i] = String.valueOf(ClasseDemarcada);
            ClasseOriginalList[i] = String.valueOf(ClasseOriginal);
            CidadeList[i] = String.valueOf(CidadePoligono);
            ComentariosList[i] = String.valueOf(Comentarios);
            RedesenhoList[i] = coodernadasRedesenhada;
        }


        lista = (ListView) findViewById(R.id.lvdemarcacoes);
        lista.setAdapter(new CustomAdapterListaDemarcacoes(ListarDemarcacoesActivity.this,IdDemarcacaoList, ClasseOriginalList,
                ClasseDemarcadaList, CidadeList, ComentariosList, RedesenhoList));

        //--------Criar seta para voltar---------------------------------------------------------//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //----------------------Criar botão voltar na toolbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<LatLng> criarPoligonoRedesenhado(String poligono) {
        List<LatLng> poligonoList = new ArrayList<>();
        int posicaoInicio = poligono.indexOf("(");
        int posicaoFinal = poligono.indexOf("]");
        String coodernadas = poligono.substring(posicaoInicio, posicaoFinal);
        String[] vetorCoodernadas = coodernadas.split(", lat/lng: ");
        for (int i = 0; i < vetorCoodernadas.length; i++) {
            Log.i("coodernadas", vetorCoodernadas[i]);
            String[] latlong = vetorCoodernadas[i].split(",");
            String lati = latlong[0].substring(latlong[0].indexOf("(") + 1);
            String longi = latlong[1].substring(0, latlong[1].indexOf(")"));
            Double longii = Double.parseDouble(longi);
            Double lat = Double.parseDouble(lati);
            poligonoList.add(new LatLng(lat, longii));
        }
        return poligonoList;
    }

    public String buscarCidade(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses.size() >0){
            address = addresses.get(0);
        }
        return  address.getLocality();
    }
}
