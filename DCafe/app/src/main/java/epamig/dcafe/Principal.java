package epamig.dcafe;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

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
import epamig.dcafe.model.Poligono;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap map;
    private AlertDialog alerta;


    //--------------------------------Nome das Classes------------------------------------//
    private static String nomeClasseAgua = "Agua";
    private static String nomeClasseAreaUrbana = "Area_urbana";
    private static String nomeClasseCafe = "Cafe";
    private static String nomeClasseMata = "Mata";
    private static String nomeClasseOutrosUsos = "Outros_usos";

    //--------------------------------Cores das Classes--------------------------------//
    private static int corClasseAgua = Color.argb(50, 0, 0, 255);
    private static int corClasseCafe = Color.argb(50, 255, 0, 0);
    private static int corClasseMata = Color.argb(50, 0, 255, 0);
    private static int corClasseOutrosUsos = Color.argb(50, 255, 255, 0);
    private static int corClasseAreaUrbana = Color.argb(50, 225, 61, 255);

    //--------------------------------Latitude das cidades--------------------------------//
    public static LatLngBounds SAOLOURENCO = new LatLngBounds(
            new LatLng(-22.15315891534319, -45.03161494543304),
            new LatLng(-22.15315891534319, -45.03161494543304));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //----------------------------Fragmento do mapa-------------------------------------------//
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //-----------------------------------Funções do dialogo -dermarcar uso da terra---------------//
    private void alertarCliquePoligono(String idPoligonoSistema) {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alerta, null);
        final Button btDemarcar = (Button) view.findViewById(R.id.btEnviar);
        final RadioButton rbAgua = (RadioButton) view.findViewById(R.id.rbAgua);
        final RadioButton rbArea_urbana = (RadioButton) view.findViewById(R.id.rbArea_urbana);
        final RadioButton rbCafe = (RadioButton) view.findViewById(R.id.rbCafe);
        final RadioButton rbMata = (RadioButton) view.findViewById(R.id.rbMata);
        final RadioButton rbOutros_usos = (RadioButton) view.findViewById(R.id.rbOutros_usos);

        ControlarBanco bd = new ControlarBanco(getBaseContext());
        int idClasse = bd.SelecionaridClassePoligonoPorIdPoligonoSistema(idPoligonoSistema);
        String nomeClasseAtual = bd.selecionarNomeClassePorId(idClasse);
        final int idPoligono = bd.SelecionaridPoligonoPorIdPoligonoSistema(idPoligonoSistema);

        btDemarcar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //TODO: salvar demarcação
                String nomeClasse = "";
                if (rbAgua.isChecked()) {
                    nomeClasse = nomeClasseAgua;
                } else if (rbCafe.isChecked()) {
                    nomeClasse = nomeClasseCafe;
                } else if (rbArea_urbana.isChecked()) {
                    nomeClasse = nomeClasseAreaUrbana;
                } else if (rbMata.isChecked()) {
                    nomeClasse = nomeClasseMata;
                } else if (rbOutros_usos.isChecked()) {
                    nomeClasse = nomeClasseOutrosUsos;
                }
                Toast.makeText(getApplicationContext(), "Classe:"+nomeClasse+" IdPoligono: "+idPoligono, Toast.LENGTH_SHORT).show();
                alerta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (nomeClasseAtual.equals(nomeClasseOutrosUsos)) {
            nomeClasseAtual = "Outros";
        } else if (nomeClasseAtual.equals(nomeClasseAreaUrbana)) {
            nomeClasseAtual = "Area Urbana";
        }
        builder.setTitle("Uso da área: " + nomeClasseAtual);
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    //-----------------------------------Funções do MAPA------------------------------------------//
    @Override
    public void onMapReady(GoogleMap Mmap) {
        map = Mmap;
        map.setContentDescription("Google Map com poligonos");
        map.setMapType(map.MAP_TYPE_HYBRID);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        //----------------------------pegar a lista por Classe------------------------------------//
        ControlarBanco bd = new ControlarBanco(getBaseContext());
        List<Poligono> Poligonos = bd.ListarTodosPoligonosObjetos();
        int quant = Poligonos.size();

        for (int i = 0; i < quant; i++) {
            List<LatLng> LatLong = criarPoligono(Poligonos.get(i).getCoodernadasPoligono());
            String Classe = bd.selecionarNomeClassePorId(Poligonos.get(i).getClassePoligono());
            int corClasse = -1;
            if (Classe.equals(nomeClasseAgua)) {
                corClasse = corClasseAgua;
            } else if (Classe.equals(nomeClasseAreaUrbana)) {
                corClasse = corClasseAreaUrbana;
            } else if (Classe.equals(nomeClasseCafe)) {
                corClasse = corClasseCafe;
            } else if (Classe.equals(nomeClasseOutrosUsos)) {
                corClasse = corClasseOutrosUsos;
            } else if (Classe.equals(nomeClasseMata)) {
                corClasse = corClasseMata;
            }

            Polygon mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                    .addAll(LatLong)
                    .fillColor(corClasse)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(3)
                    .clickable(true));

            bd.alteraidPoligonoSistema(Poligonos.get(i).getIdPoligono(), mClickablePolygonWithHoles.getId());
        }
        //TODO: Fazer um select de cidade e mover para tal
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SAOLOURENCO.getCenter(), 15));

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                int strokeColor = polygon.getStrokeColor() ^ 0x00ffffff;
                polygon.setStrokeColor(strokeColor);
                alertarCliquePoligono(polygon.getId());
            }
        });
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

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    //-----------------------------------Funções do sistemas android------------------------------//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
