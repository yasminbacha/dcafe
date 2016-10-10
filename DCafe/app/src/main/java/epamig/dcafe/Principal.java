package epamig.dcafe;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
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


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap map;
    private AlertDialog alerta;
    private Spinner spCidades;
    private static int NAOSINCRONIZADO = 0;
    public ControlarBanco bd;
    int check = 0;

    private static String CIDADEINICIAL = "Lavras";
    //TODO sempre trocar

    //SINCRONIZACAO
    UserDemarcacaoTask mAuthTask = null;
    ProgressDialog mDialog;
    public static String IP = new Aplicacao().getIP();

    //--------------------------------Cores das Classes--------------------------------//
    Aplicacao ap = new Aplicacao();
    int corClasseAgua = ap.getCorClasseAgua();
    int corClasseCafe = ap.getCorClasseCafe();
    int corClasseMata = ap.getCorClasseMata();
    int corClasseOutrosUsos = ap.getCorClasseOutrosUsos();
    int corClasseAreaUrbana = ap.getCorClasseAreaUrbana();


    /****************************
     * Pegar localização atual
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        //----------------Iniciar BANCO---------------------------------------------------------//
        bd = new ControlarBanco(getBaseContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-----------------Preencher nome e email do menu lateral-----------------------------//
        Usuario usuario = getObjetoUsuarioPreferences();
        View header = navigationView.getHeaderView(0);
        TextView txtNomeUsuario = (TextView) header.findViewById(R.id.txtNomeUsuario);
        txtNomeUsuario.setText(usuario.getNomeUsuario());
        TextView txtEmailUsuario = (TextView) header.findViewById(R.id.txtEmailUsuario);
        txtEmailUsuario.setText(usuario.getEmailUsuario());


        //----------------------------Fragmento do mapa-------------------------------------------//
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //------------------------------Preencher Spinner Cidades---------------------------------//

        final List<String> Cidades = bd.ListarTodasAsCidades();
        spCidades = (Spinner) findViewById(R.id.spCidades);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Cidades);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCidades.setAdapter(spinnerArrayAdapter);
        spCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                final String Cidade = parent.getItemAtPosition(posicao).toString();
                check = check + 1;
                if (check > 1) {

                    //----------------------------Mover para cidade-------------------------------------------//
                    LatLngBounds CidadeAtual = pegarCidadeAtual(Cidade);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(CidadeAtual.getCenter(), 14));
                    //------------------------Limpar Mapa-----------------------------------------------------//
                    map.clear();
                    //----------------------------pegar a lista----------------------------------------------//
/*                    dialog = ProgressDialog.show(Principal.this, "Aguarde", "Sincronizando as áreas", false, true);
                    dialog.setCancelable(false);
                    new Thread() {
                        public void run() {
                            try {*/
                    //TODO
                    colocarPoligonosnoMapa(Cidade);
                                /*dialog.dismiss();
                            } catch (Exception e) {
                                Log.i("ERRO POLIGONOS NO MAPA", e.toString());

                            }
                        }
                    }.start();*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //-------------------------------------NAVBAR-----------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sincronizar, menu);
        return true;
    }

    //----------------------Criar botão voltar na toolbar-----------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sincronizar_demarcacao) {
            Sincronizar();
        }
        return super.onOptionsItemSelected(item);
    }

    //-----------------------------------Funções do dialogo -dermarcar uso da terra---------------//
    private void alertarCliquePoligono(String idPoligonoSistema) {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alerta, null);

        final Button btConcluir = (Button) view.findViewById(R.id.btConcluir);
        final Button btDemarcar = (Button) view.findViewById(R.id.btMarcarNovamente);

        final EditText edtComentarios = (EditText) view.findViewById(R.id.edtComentarios);

        final RadioButton rbAgua = (RadioButton) view.findViewById(R.id.rbAgua);
        final RadioButton rbArea_urbana = (RadioButton) view.findViewById(R.id.rbArea_urbana);
        final RadioButton rbCafe = (RadioButton) view.findViewById(R.id.rbCafe);
        final RadioButton rbMata = (RadioButton) view.findViewById(R.id.rbMata);
        final RadioButton rbOutros_usos = (RadioButton) view.findViewById(R.id.rbOutros_usos);
        final RadioButton rbUsoCorreto = (RadioButton) view.findViewById(R.id.rbUso_correto);

        final int idClasse = bd.SelecionaridClassePoligonoPorIdPoligonoSistema(idPoligonoSistema);
        String nomeClasseAtual = bd.selecionarNomeClassePorId(idClasse);

        final int idPoligono = bd.SelecionaridPoligonoPorIdPoligonoSistema(idPoligonoSistema);


        btConcluir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //INSERIR
                String nomeClasse = "";
                if (rbAgua.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAgua);
                } else if (rbCafe.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseCafe);
                } else if (rbArea_urbana.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAreaUrbana);
                } else if (rbMata.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseMata);
                } else if (rbOutros_usos.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseOutrosUsos);
                } else if (rbUsoCorreto.isChecked()) {
                    nomeClasse = getString(R.string.nomeUsoCorreto);
                }
                final String classe = nomeClasse;

                int idClasseSelec;
                if (classe.equals(getString(R.string.nomeUsoCorreto))) {
                    idClasseSelec = idClasse;
                } else {
                    idClasseSelec = bd.selecionarIdClasse(classe);
                }
                int idClasseSelecionada = idClasseSelec;

                //preenchendo objeto demarcação
                final Demarcacao demarcacao = new Demarcacao();
                demarcacao.setPoligono_idPoligono(idPoligono);
                demarcacao.setClasse_idClasse(idClasseSelecionada);
                demarcacao.setUsuario_idUsuario(getIdUsuarioPreferences());
                demarcacao.setComentariosDemarcacao(edtComentarios.getText().toString());
                demarcacao.setCoodernadasDemarcacao("");
                demarcacao.setStatusDemarcacao(getString(R.string.naoAvaliado));
                demarcacao.setFlagSincronizado(0);

                //Salvar demarcação
                bd.insereDemarcacao(demarcacao);
                //Mensagem de salvo com sucesso
                Toast.makeText(getApplicationContext(), "Sua demarcação de uso foi inserida com sucesso, obrigado por colaborar!", Toast.LENGTH_SHORT).show();
                //Fechar Dialogo
                alerta.dismiss();
            }
        });

        btDemarcar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //INSERIR
                String nomeClasse = "";
                if (rbAgua.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAgua);
                } else if (rbCafe.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseCafe);
                } else if (rbArea_urbana.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAreaUrbana);
                } else if (rbMata.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseMata);
                } else if (rbOutros_usos.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseOutrosUsos);
                } else if (rbUsoCorreto.isChecked()) {
                    nomeClasse = getString(R.string.nomeUsoCorreto);
                }
                final String classe = nomeClasse;

                int idClasseSelec;
                if (classe.equals(getString(R.string.nomeUsoCorreto))) {
                    idClasseSelec = idClasse;
                } else {
                    idClasseSelec = bd.selecionarIdClasse(classe);
                }
                int idClasseSelecionada = idClasseSelec;

                //preenchendo objeto demarcação
                final Demarcacao demarcacao = new Demarcacao();
                demarcacao.setPoligono_idPoligono(idPoligono);
                demarcacao.setClasse_idClasse(idClasseSelecionada);
                demarcacao.setUsuario_idUsuario(getIdUsuarioPreferences());
                demarcacao.setComentariosDemarcacao(edtComentarios.getText().toString());
                demarcacao.setStatusDemarcacao(getString(R.string.naoAvaliado));
                demarcacao.setFlagSincronizado(0);

                //Salvar demarcação
                bd.insereDemarcacao(demarcacao);
                int idDemarcacao = bd.selecionarIddaUltimaDemarcacaoInserida();

                //Fechar Dialogo
                alerta.dismiss();
                //Ir para a tela de redesenhar
                Intent intent = new Intent();
                intent.setClass(Principal.this, DemarcarPoligonoActivity.class);


                intent.putExtra("idDemarcacao", idDemarcacao);
                intent.putExtra("idPoligono", idPoligono);
                intent.putExtra("localizacao", "-");
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String ClasseExibida = nomeClasseAtual;
        if (ClasseExibida.equals(getString(R.string.nomeClasseOutrosUsos))) {
            ClasseExibida = "Outros";
        } else if (ClasseExibida.equals(getString(R.string.nomeClasseAreaUrbana))) {
            ClasseExibida = "Area Urbana";
        }
        builder.setTitle("Uso da área: " + ClasseExibida);
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }


    //-----------------------------------Funções do dialogo -dermarcar nova área e uso da terra---------------//
    private void alertarCliqueAreaEmBranco(final LatLng localizacao) {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alerta_nova_area, null);

        final Button btCancelar = (Button) view.findViewById(R.id.btCancelar);
        final Button btDemarcar = (Button) view.findViewById(R.id.btMarcarNovamente);
        final EditText edtComentarios = (EditText) view.findViewById(R.id.edtComentarios);
        final RadioButton rbAgua = (RadioButton) view.findViewById(R.id.rbAgua);
        final RadioButton rbArea_urbana = (RadioButton) view.findViewById(R.id.rbArea_urbana);
        final RadioButton rbCafe = (RadioButton) view.findViewById(R.id.rbCafe);
        final RadioButton rbMata = (RadioButton) view.findViewById(R.id.rbMata);
        final RadioButton rbOutros_usos = (RadioButton) view.findViewById(R.id.rbOutros_usos);

        btCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alerta.dismiss();
            }
        });

        btDemarcar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String nomeClasse = "";
                if (rbAgua.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAgua);
                } else if (rbCafe.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseCafe);
                } else if (rbArea_urbana.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseAreaUrbana);
                } else if (rbMata.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseMata);
                } else if (rbOutros_usos.isChecked()) {
                    nomeClasse = getString(R.string.nomeClasseOutrosUsos);
                }

                int idClasseSelec;
                idClasseSelec = bd.selecionarIdClasse(nomeClasse);

                //preenchendo objeto demarcação
                final Demarcacao demarcacao = new Demarcacao();
                demarcacao.setClasse_idClasse(idClasseSelec);
                demarcacao.setUsuario_idUsuario(getIdUsuarioPreferences());
                demarcacao.setComentariosDemarcacao(edtComentarios.getText().toString());
                demarcacao.setStatusDemarcacao(getString(R.string.naoAvaliado));
                demarcacao.setFlagSincronizado(0);
                //Salvar demarcação
                bd.insereDemarcacao(demarcacao);
                int idDemarcacao = bd.selecionarIddaUltimaDemarcacaoInserida();

                //Fechar Dialogo
                alerta.dismiss();
                //Ir para a tela de redesenhar
                Intent intent = new Intent();
                intent.setClass(Principal.this, DemarcarPoligonoActivity.class);


                intent.putExtra("idDemarcacao", idDemarcacao);
                intent.putExtra("localizacao", localizacao.toString());
                intent.putExtra("idPoligono", -1);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cadastrar nova área");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    //-----------------------------------Funções do MAPA------------------------------------------//
    @Override
    public void onMapReady(GoogleMap Mmap) {
        map = Mmap;
        map.setOnMyLocationButtonClickListener(this);
        permitirMinhaLocalizacao();
        map.setContentDescription("Google Map com poligonos");
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        //----------------------------pegar a lista----------------------------------------------//
        colocarPoligonosnoMapa(CIDADEINICIAL);
        LatLngBounds CidadeAtual = pegarCidadeAtual(CIDADEINICIAL);

        //----------------------------Mover para cidade-------------------------------------------//
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CidadeAtual.getCenter(), 14));

        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                int strokeColor = polygon.getStrokeColor() ^ 0x00ffffff;
                polygon.setStrokeColor(strokeColor);
                alertarCliquePoligono(polygon.getId());
            }
        });
        map.setOnMapLongClickListener(this);
    }

    //--------------------PERMITIR MINHA LOCALIZAÇÂO----------------------------------------//
    private void permitirMinhaLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (map != null) {
            // Access to the location has been granted to the app.
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            permitirMinhaLocalizacao();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Toast.makeText(Principal.this, MinhaCidade(), Toast.LENGTH_LONG).show();
        return false;
    }

    public String MinhaCidade() {
        double lat = -1, lng = -1;
        if (map.getMyLocation() != null) {
            lat = map.getMyLocation().getLatitude();
            lng = map.getMyLocation().getLongitude();
        }
        return "Latitude: " + lat + " Longitude: " + lng;
    }
    //-------------------------------------------------------------------------------------------//

    private void colocarPoligonosnoMapa(String cidade) {
        List<Poligono> Poligonos = bd.ListarTodosPoligonosObjetosCidade(cidade);
        int quant = Poligonos.size();
        if (quant > 0) {
            for (int i = 0; i < quant; i++) {
                List<LatLng> LatLong = criarPoligono(Poligonos.get(i).getCoodernadasPoligono());
                String Classe = bd.selecionarNomeClassePorId(Poligonos.get(i).getClassePoligono());
                int corClasse = pegarCorClasse(Classe);
                Polygon mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                        .addAll(LatLong)
                        .fillColor(corClasse)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(3)
                        .clickable(true));

                bd.alteraidPoligonoSistema(Poligonos.get(i).getIdPoligono(), mClickablePolygonWithHoles.getId());
            }
        }
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

    private LatLngBounds pegarCidadeAtual(String Cidade) {
        String coodernadaDaCidade = bd.SelecionarCoodernadaParaCidade(Cidade);
        List<LatLng> ll = criarPoligono(coodernadaDaCidade);
        LatLng LatLong = ll.get(2);

        return new LatLngBounds(LatLong, LatLong);
    }

    private List<LatLng> criarPoligono(String poligono) {
        List<LatLng> poligonoList = new ArrayList<>();
        int posicaoInicio = poligono.indexOf("((") + 2;
        int posicaoFinal = poligono.indexOf(")");
        String coodernadas = poligono.substring(posicaoInicio, posicaoFinal);
        String[] vetorCoodernadas = coodernadas.split(",");
        for (String codernadas : vetorCoodernadas) {
            String[] latlong = codernadas.split(" ");
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
/*TIREI MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_demarcacoes) {
            Intent intent = new Intent();
            intent.setClass(Principal.this, ListarDemarcacoesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sobre) {
            Intent intent = new Intent();
            intent.setClass(Principal.this, SobreActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sair) {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            finish();
            Intent intent = new Intent();
            intent.setClass(Principal.this, LoginActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        alertarCliqueAreaEmBranco(latLng);
    }

    //-------------GET ID USUARIO-----------------//
    public int getIdUsuarioPreferences() {
        //Restaura as preferencias gravadas
        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        return settings.getInt("idUsuario", 0);
    }

    //-------------GET Objeto USUARIO-----------------//
    public Usuario getObjetoUsuarioPreferences() {
        //Restaura as preferencias gravadas
        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(settings.getInt("idUsuario", 0));
        usuario.setNomeUsuario(settings.getString("nomeUsuario", ""));
        usuario.setEmailUsuario(settings.getString("emailUsuario", ""));
        usuario.setProfissaoUsuario(settings.getString("profissaoUsuario", ""));


        return usuario;
    }

    ///--------------------------------------SINCRONIZAR------------------------------------------//
    public void Sincronizar() {
        //selecionar todos as demarcações
        //enviar demarcações
        //Apagar o banco de dados
        //Baixar todas as demarcações daqioe
        //Inserir todas as demarcações
        mDialog = new ProgressDialog(Principal.this);
        mDialog.setMessage("Sincronizando os dados.");
        mDialog.setCancelable(false);
        mDialog.show();
        List<Demarcacao> ListDemarcacoes = bd.ListarTodasDemarcacoesNaoSincronizada();
        Log.i("TESTE", "TAM: " + ListDemarcacoes.size());
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
                String coodernadasRedesenhada = demarcacao.getCoodernadasDemarcacao();

                if (coodernadasRedesenhada != null && !coodernadasRedesenhada.isEmpty()) {
                    coodernadasRedesenhada = ConverteDemarcacoes(demarcacao.getCoodernadasDemarcacao());
                    demarcacao.setCoodernadasDemarcacao(coodernadasRedesenhada);
                }
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
                        JSONObject objJson = new JSONObject(json);

                        Log.i("objJson", objJson.toString());

                    } catch (JSONException e) {
                        Log.i("ERRO", e.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
           // mDialog.dismiss();
            bd.deletarTodasDemarcacoes();
            pegarTodasDemarcacoes();
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
                poligonoList = poligonoList + longii + " " + lat + ", ";
            }

            posicaoFinal = poligonoList.indexOf(",");
            String Primeiro = poligonoList.substring(0, posicaoFinal);
            poligonoList += Primeiro;

            return poligonoList;
        }

    }

    private void pegarTodasDemarcacoes() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                IP + "demarcacoesporid.php?idUsuario=" + getIdUsuarioPreferences(), new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mDialog.dismiss();

                        String erro = new Exception(throwable).getMessage();
                        Log.i("ERRO", "Erro no download: " + erro);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        getDemarcacoes(responseString);
                        Log.i("DEU certo", "It's ok");
                        mDialog.dismiss();

                    }
                });
    }

    private void getDemarcacoes(String json) {
        try {

            JSONObject objJson = new JSONObject(json);
            JSONArray demarcacaoJson = objJson.getJSONArray("demarcacao");
            for (int i = 0; i < demarcacaoJson.length(); i++) {
                JSONObject jsonDemarcacao = new JSONObject(demarcacaoJson.getString(i));
                Demarcacao demarcacaoBaixada = new Demarcacao();
                String demarcacaoCoodernadas = jsonDemarcacao.getString("coodernadasPoligono");

                if(demarcacaoCoodernadas != null && !demarcacaoCoodernadas.isEmpty() && !demarcacaoCoodernadas.equals("null")){
                   List <LatLng> latlongi = criarPoligono(jsonDemarcacao.getString("coodernadasPoligono"));
                    demarcacaoCoodernadas = latlongi.toString();

                }else{
                    demarcacaoCoodernadas = null;
                }
                demarcacaoBaixada.setUsuario_idUsuario(jsonDemarcacao.getInt("Usuario_idUsuario"));
                demarcacaoBaixada.setPoligono_idPoligono(jsonDemarcacao.getInt("Poligono_idPoligono"));
                demarcacaoBaixada.setClasse_idClasse(jsonDemarcacao.getInt("Classe_idClasse"));
                Log.i("Inserir demarcacoes", "ID CLASSE: " + demarcacaoBaixada.getClasse_idClasse());

                demarcacaoBaixada.setComentariosDemarcacao(jsonDemarcacao.getString("comentariosDemarcacao"));
                demarcacaoBaixada.setCoodernadasDemarcacao(demarcacaoCoodernadas);
                demarcacaoBaixada.setStatusDemarcacao(jsonDemarcacao.getString("statusDemarcacao"));
                demarcacaoBaixada.setFlagSincronizado(jsonDemarcacao.getInt("flagSincronizado"));
                //---------------Salvar no  Banco-------------------------------------------------//
                String resultado = bd.insereDemarcacaoCompleta(demarcacaoBaixada);
                Log.i("Inserir demarcacoes", resultado);
            }
        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
    }

    ///-------------------------------------------------------------------------------------------//
}

