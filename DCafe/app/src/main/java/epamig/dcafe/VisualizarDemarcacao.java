package epamig.dcafe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import epamig.dcafe.bancodedados.ControlarBanco;
import epamig.dcafe.model.Demarcacao;
import epamig.dcafe.model.Poligono;
import epamig.dcafe.sistema.Aplicacao;

public class VisualizarDemarcacao extends AppCompatActivity implements OnMapReadyCallback {

    public ControlarBanco bd;
    int idDemarcacao;
    int idPoligono = -1;


    TextView txtClasseOriginalPoligono;
    TextView txtCidadePoligono;
    TextView txtClasseDemarcadaPoligono;
    TextView txtComentariosPoligono;
    TextView txtPoligonoRedesenhado;
    RadioGroup rgOriginalDemarcado;
    RadioButton rbOriginal;
    RadioButton rbDemarcado;


    LinearLayout layoutRadio;

    private GoogleMap map;

    Poligono poligonoOriginal;
    Demarcacao demarcacao;
    List<LatLng> coodernadasPoligonoDemarcado;

    //--------------------------------Cores das Classes--------------------------------//
    Aplicacao ap = new Aplicacao();
    int corClasseAgua = ap.getCorClasseAgua();
    int corClasseCafe = ap.getCorClasseCafe();
    int corClasseMata = ap.getCorClasseMata();
    int corClasseOutrosUsos = ap.getCorClasseOutrosUsos();
    int corClasseAreaUrbana = ap.getCorClasseAreaUrbana();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_demarcacao);

        bd = new ControlarBanco(getBaseContext());

        txtClasseOriginalPoligono = (TextView) findViewById(R.id.txtClasseOriginalPoligonoVis);
        txtClasseDemarcadaPoligono = (TextView) findViewById(R.id.txtClasseDemarcadaPoligonoVis);
        txtCidadePoligono = (TextView) findViewById(R.id.txtCidadePoligonoVis);
        txtComentariosPoligono = (TextView) findViewById(R.id.txtComentariosPoligonoVis);
        txtPoligonoRedesenhado = (TextView) findViewById(R.id.txtPoligonoRedesenhado);
        rgOriginalDemarcado = (RadioGroup) findViewById(R.id.rgOriginalDemarcado);
        rbOriginal = (RadioButton) findViewById(R.id.rbOriginalVis);
        rbDemarcado = (RadioButton) findViewById(R.id.rbDemarcadoVis);
        layoutRadio = (LinearLayout) findViewById(R.id.linearRadios);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idDemarcacao = -1;
            } else {
                idDemarcacao = extras.getInt("idDemarcacao");
            }
        } else {
            idDemarcacao = (int) savedInstanceState.getSerializable("idDemarcacao");
        }

        demarcacao = bd.selecionarDemarcacaoPorId(idDemarcacao);
        String Comentarios = "Comentários: " + demarcacao.getComentariosDemarcacao();
        String coodernadasRedesenhada = demarcacao.getCoodernadasDemarcacao();

        idPoligono = demarcacao.getPoligono_idPoligono();
        if(idPoligono > 0) {
            poligonoOriginal = bd.selecionarPoligonoPorId(idPoligono);
            String ClasseOriginal = "Uso inicial: " + bd.selecionarNomeClassePorId(poligonoOriginal.getClassePoligono());
            String coodernadasOriginal = poligonoOriginal.getCoodernadasPoligono();
            txtClasseOriginalPoligono.setText(ClasseOriginal);
            String CidadePoligono = "Cidade: " + bd.selecionarCidadePorIdMapa(poligonoOriginal.getMapaPoligono());
            txtCidadePoligono.setText(CidadePoligono);
        }else{
            txtClasseOriginalPoligono.setText("Nova área");
            txtCidadePoligono.setText("Cidade: Lavras");
        }

        String ClasseDemarcada = "Uso demarcado: " + bd.selecionarNomeClassePorId(demarcacao.getClasse_idClasse());
        if (coodernadasRedesenhada != null && !coodernadasRedesenhada.isEmpty() && idPoligono>0) {
        } else {
            layoutRadio.setVisibility(LinearLayout.GONE);
        }

        txtClasseDemarcadaPoligono.setText(ClasseDemarcada);

        txtComentariosPoligono.setText(Comentarios);


        //----------------------------Fragmento do mapa-------------------------------------------//
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        rgOriginalDemarcado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                int index = rgOriginalDemarcado.indexOfChild(rb);
                switch (index) {
                    case 0: // first button
                        map.clear();
                        ColocarPoligonoOriginalMapa();
                        break;
                    case 1: // secondbutton
                        //map.clear();
                        ColocarPoligonoRedemarcadoMapa();
                        break;
                }
            }
        });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setContentDescription("Google Map com poligonos");
        map.setMapType(map.MAP_TYPE_HYBRID);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //
        if(idPoligono > 0) {
            ColocarPoligonoOriginalMapa();
        }else{
            ColocarPoligonoRedemarcadoMapa();
        }
    }

    public void ColocarPoligonoOriginalMapa() {
        //----------------------------colocar poligono no mapa------------------------------------//
        List<LatLng> LatLong = criarPoligono(poligonoOriginal.getCoodernadasPoligono());
        String Classe = bd.selecionarNomeClassePorId(poligonoOriginal.getClassePoligono());
        int corClasse = pegarCorClasse(Classe);
        Polygon mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                .addAll(LatLong)
                .fillColor(corClasse)
                .strokeColor(Color.BLACK)
                .strokeWidth(3)
                .clickable(true));

        LatLng ll = LatLong.get(2);
        LatLngBounds latLongPoligono = new LatLngBounds(ll, ll);
        //----------------------------Mover para cidade-------------------------------------------//
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLongPoligono.getCenter(), 16));
    }


    public void ColocarPoligonoRedemarcadoMapa() {
        //----------------------------colocar poligono no mapa------------------------------------//
        List<LatLng> LatLong = criarPoligonoRedesenhado(demarcacao.getCoodernadasDemarcacao());

        String Classe = bd.selecionarNomeClassePorId(demarcacao.getClasse_idClasse());
        int corClasse = pegarCorClasse(Classe);

        Polygon mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                .addAll(LatLong)
                .fillColor(corClasse)
                .strokeColor(Color.BLACK)
                .strokeWidth(3)
                .clickable(true));

        LatLng ll = LatLong.get(2);
        LatLngBounds latLongPoligono = new LatLngBounds(ll, ll);
        //----------------------------Mover para cidade-------------------------------------------//
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLongPoligono.getCenter(), 16));
    }

    private int pegarCorClasse(String Classe) {
        int corClasse = -1;
        if (Classe.equals(getString(R.string.nomeClasseAgua))) {
            corClasse = corClasseAgua;
        } else if (Classe.equals(getString(R.string.nomeClasseAreaUrbana))) {
            corClasse = corClasseAreaUrbana;
        } else if (Classe.equals(getString(R.string.nomeClasseCafe))) {
            corClasse = corClasseCafe;
        } else if (Classe.equals(getString(R.string.nomeClasseOutrosUsos))) {
            corClasse = corClasseOutrosUsos;
        } else if (Classe.equals(getString(R.string.nomeClasseMata))) {
            corClasse = corClasseMata;
        }
        return corClasse;
    }

    private List<LatLng> criarPoligono(String poligono) {
        List<LatLng> poligonoList = new ArrayList<>();
        int posicaoInicio = poligono.indexOf("((") + 2;
        int posicaoFinal = poligono.indexOf(")");
        String coodernadas = poligono.substring(posicaoInicio, posicaoFinal);
        String[] vetorCoodernadas = coodernadas.split(",");
        for (int i = 0; i < vetorCoodernadas.length; i++) {
            String[] latlong = vetorCoodernadas[i].split(" ");
            Double longi = Double.parseDouble(latlong[0]);
            Double lat = Double.parseDouble(latlong[1]);
            poligonoList.add(new LatLng(lat, longi));
        }
        return poligonoList;
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
}