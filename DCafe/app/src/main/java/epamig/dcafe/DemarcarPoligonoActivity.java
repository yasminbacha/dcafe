package epamig.dcafe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import epamig.dcafe.bancodedados.ControlarBanco;
import epamig.dcafe.model.Poligono;
import epamig.dcafe.sistema.Aplicacao;

public class DemarcarPoligonoActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    List<LatLng> criandoPoligono;
    int idPoligono;
    int idDemarcacao;
    LatLng localizacao;
    String localizacaoStr;
    public ControlarBanco bd;
    private GoogleMap map;
    public boolean FlagPoligonoVisualizado = false;
    public boolean FlagPoligonoDemarcado = false;


    //--------------------------------Cores das Classes--------------------------------//
    Aplicacao ap = new Aplicacao();
    int corClasseAgua = ap.getCorClasseAgua();
    int corClasseCafe = ap.getCorClasseCafe();
    int corClasseMata = ap.getCorClasseMata();
    int corClasseOutrosUsos = ap.getCorClasseOutrosUsos();
    int corClasseAreaUrbana = ap.getCorClasseAreaUrbana();

    private static int corBranca = Color.argb(30, 225, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demarcar_poligono);
        //--------Criar seta para voltar---------------------------------------------------------//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idPoligono = -1;
                idDemarcacao = -1;
                localizacaoStr = "-";
            } else {
                localizacaoStr = extras.getString("localizacao");
                idPoligono = extras.getInt("idPoligono");
                idDemarcacao = extras.getInt("idDemarcacao");
            }
        } else {
            localizacaoStr = (String) savedInstanceState.getSerializable("localizacao");
            idPoligono = (int) savedInstanceState.getSerializable("idPoligono");
            idDemarcacao = (int) savedInstanceState.getSerializable("idDemarcacao");

        }

        bd = new ControlarBanco(getBaseContext());
        //----------------------------Fragmento do mapa-------------------------------------------//
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaRedemarcar);
        mapFragment.getMapAsync(this);

        criandoPoligono = new ArrayList<>();
    }

    public LatLng converteStringParaLocalizacao(String loc) {
        int posicaoInicio = loc.indexOf("(")+1;
        int posicaoFinal = loc.indexOf(")");
        loc = loc.substring(posicaoInicio, posicaoFinal);

        String[] latLng = loc.split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);
        LatLng local = new LatLng(latitude, longitude);

        return local;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salvar, menu);
        return true;
    }

    //----------------------Criar botão voltar na toolbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_mostrar_demarcacao) {
            if (FlagPoligonoDemarcado == false) {
                Toast.makeText(DemarcarPoligonoActivity.this, "Marque pelo menos um ponto para criar uma nova área.", Toast.LENGTH_LONG).show();
            } else {
                map.clear();
                Polygon mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                        .addAll(criandoPoligono)
                        .fillColor(corBranca)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(3)
                        .clickable(true));
                FlagPoligonoVisualizado = true;
            }
        } else if (id == R.id.action_salvar_demarcacao) {
            if (FlagPoligonoVisualizado == false) {
                Toast.makeText(DemarcarPoligonoActivity.this, "Clique no botão de Visualizar antes de salvar  ", Toast.LENGTH_LONG).show();
            } else {
                bd.alteracoodernadasDemarcacao(idDemarcacao, criandoPoligono.toString());
                Toast.makeText(DemarcarPoligonoActivity.this, "Sua demarcação de uso foi inserida com sucesso, obrigado por colaborar!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Marker marcador;
        marcador = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Ponto do poligono")
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        FlagPoligonoDemarcado = true;
        criandoPoligono.add(latLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setContentDescription("Google Map com poligonos");
        map.setMapType(map.MAP_TYPE_HYBRID);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if (idPoligono != -1) {
            //Alterar área
            colocarPoligononoMapa();
        } else {
            localizacao = converteStringParaLocalizacao(localizacaoStr);
            Log.i("TESTE", localizacao.toString());
            //Criar nova área
            LatLngBounds latLongPoligono = new LatLngBounds(localizacao, localizacao);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLongPoligono.getCenter(), 16));

        }
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);

    }

    public void colocarPoligononoMapa() {
        Poligono poligono = bd.PegarPoligono(idPoligono);
        int idClasse = bd.selecionarClasseDaDemarcacaoPorIdDemarcacao(idDemarcacao);
        List<LatLng> LatLong = criarPoligono(poligono.getCoodernadasPoligono());
        String Classe = bd.selecionarNomeClassePorId(idClasse);
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
}
