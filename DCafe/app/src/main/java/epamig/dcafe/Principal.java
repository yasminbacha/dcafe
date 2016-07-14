package epamig.dcafe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import epamig.dcafe.model.Classe;
import epamig.dcafe.model.Mapa;
import epamig.dcafe.model.Poligono;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public List<Poligono> poligonos;
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    Polygon mClickablePolygonWithHoles;
    Polygon mMutablePolygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //INICIO -- Fragmento do mapa
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //FIM -- Fragmento do mapa

        //INICIO -- PEGANDO VALORES DO WEBSERVICE
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                "http://192.168.217.1/dcafeconverterdados/poligonos.php", new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        poligonos = getPoligonos(responseString);
                        Log.i("JSON ", "TESTE");
                        for (Poligono poligono : poligonos) {
                            Log.i("JSON ", poligono.convertePoligonoParaString(poligono));
                        }
                    }
                });
        //FIM -- PEGANDO VALORES DO WEBSERVICE

    }

    private List<Poligono> getPoligonos(String jsonString) {
        List<Poligono> poligonos = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(jsonString);
            JSONArray poligonosJson = objJson.getJSONArray("poligono");
            for (int i = 0; i < poligonosJson.length(); i++) {
                JSONObject jsonPoligono = new JSONObject(poligonosJson.getString(i));
                Poligono objetoPoligono = new Poligono();

                Classe objetoClasse = new Classe(jsonPoligono.getString("nomeClasse"), jsonPoligono.getString("corClasse"));
                Mapa objetoMapa = new Mapa(jsonPoligono.getString("nomeMapa"), jsonPoligono.getString("cidadeMapa"));

                objetoPoligono.setIdPoligono(jsonPoligono.getInt("idPoligono"));
                objetoPoligono.setCoodernadasPoligono(jsonPoligono.getString("coodernadasPoligono"));
                objetoPoligono.setClassePoligono(objetoClasse);
                objetoPoligono.setMapaPoligono(objetoMapa);

                poligonos.add(objetoPoligono);
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return poligonos;
    }

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

    @Override
    public void onMapReady(GoogleMap map) {
        map.setContentDescription("Google Map com poligonos");

        mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(new LatLng(-20, 130), 5, 5))
                .addHole(createRectangle(new LatLng(-22, 128), 1, 1))
                .addHole(createRectangle(new LatLng(-18, 133), 0.5, 1.5))
                .fillColor(Color.CYAN)
                .strokeColor(Color.BLUE)
                .strokeWidth(5)
                .clickable(true));

        PolygonOptions options = new PolygonOptions()
                .addAll(createRectangle(SYDNEY, 5, 8))
                .clickable(true);

        int fillColor = Color.HSVToColor( new float[]{ 1.f, 1.f, 1.f } );
        mMutablePolygon = map.addPolygon(options
                .strokeWidth(2)
                .strokeColor(Color.BLACK)
                .fillColor(fillColor));

        map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(new LatLng(-27, 140), 10, 7))
                .fillColor(Color.WHITE)
                .strokeColor(Color.BLACK));

        map.moveCamera(CameraUpdateFactory.newLatLng(SYDNEY));

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                int strokeColor = polygon.getStrokeColor() ^ 0x00ffffff;
                polygon.setStrokeColor(strokeColor);
            }
        });
    }

    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

}
