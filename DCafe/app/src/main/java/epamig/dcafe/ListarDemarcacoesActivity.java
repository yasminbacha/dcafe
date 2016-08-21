package epamig.dcafe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

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
            Poligono poli = bd.selecionarPoligonoPorId(ListDemarcacoes.get(i).getPoligono_idPoligono());

            int idDemarcao = ListDemarcacoes.get(i).getIdDemarcacao();
            String ClasseOriginal = "Uso inicial: "+bd.selecionarNomeClassePorId(poli.getClassePoligono());
            String CidadePoligono = "Cidade: "+bd.selecionarCidadePorIdMapa(poli.getMapaPoligono());
            String ClasseDemarcada = "Uso demarcado: "+bd.selecionarNomeClassePorId(ListDemarcacoes.get(i).getClasse_idClasse());
            String Comentarios = "Comentários: "+ListDemarcacoes.get(i).getComentariosDemarcacao();

            String coodernadasRedesenhada = ListDemarcacoes.get(i).getCoodernadasDemarcacao();

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
}
