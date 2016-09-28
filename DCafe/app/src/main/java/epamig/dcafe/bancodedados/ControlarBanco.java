package epamig.dcafe.bancodedados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import epamig.dcafe.model.Demarcacao;
import epamig.dcafe.model.Poligono;

public class ControlarBanco {

    private SQLiteDatabase db;
    private CriarBanco banco;

    public ControlarBanco(Context context) {
        banco = new CriarBanco(context);
    }

    /*-------------------------------------------------------------------------------------------*/
    /*---------------------------------------INSERÇÕES-------------------------------------------*/
    /*-------------------------------------------------------------------------------------------*/
    public String inserePoligono(String coodernadasPoligono, int Classe_idClasse, int Mapa_idMapa) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put("coodernadasPoligono", coodernadasPoligono);
        valores.put("Classe_idClasse", Classe_idClasse);
        valores.put("Mapa_idMapa", Mapa_idMapa);

        resultado = db.insert("poligono", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir poligono";
        else
            return "Poligono Inserido com sucesso";

    }

    public String inserePoligono(Poligono objetoPoligono) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put("coodernadasPoligono", objetoPoligono.getCoodernadasPoligono());
        valores.put("Classe_idClasse", objetoPoligono.getClassePoligono());
        valores.put("Mapa_idMapa", objetoPoligono.getMapaPoligono());

        resultado = db.insert("poligono", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir poligono";
        else
            return "Poligono Inserido com sucesso";

    }

    public String insereMapa(String nomeMapa, String cidadeMapa) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put("nomeMapa", nomeMapa);
        valores.put("cidadeMapa", cidadeMapa);

        resultado = db.insert("mapa", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir mapa";
        else
            return "Mapa Inserido com sucesso";

    }

    public String insereMapaSeNaoexistir(String nomeMapa, String cidadeMapa) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();

        int idMapa = selecionarIdMapaNomeMapa(nomeMapa);
        if (idMapa == -1) {
            return "Já está inserido";
        } else {
            valores = new ContentValues();
            valores.put("nomeMapa", nomeMapa);
            valores.put("cidadeMapa", cidadeMapa);

            resultado = db.insert("mapa", null, valores);
            db.close();

            if (resultado == -1)
                return "Erro ao inserir mapa";
            else
                return "Mapa Inserido com sucesso";
        }
    }

    public String insereClasse(String nomeClasse, String corClasse) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put("nomeClasse", nomeClasse);
        valores.put("corClasse", corClasse);

        resultado = db.insert("classe", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir classe";
        else
            return "Classe Inserido com sucesso";
    }

    public String insereClasseSeNaoExistir(String nomeClasse, String corClasse) {

        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();

        int idClasse = selecionarIdClasse(nomeClasse);
        if (idClasse == -1) {
            return "Já está inserido";
        } else {
            valores = new ContentValues();
            valores.put("nomeClasse", nomeClasse);
            valores.put("corClasse", corClasse);

            resultado = db.insert("classe", null, valores);
            db.close();

            if (resultado == -1)
                return "Erro ao inserir classe";
            else
                return "Classe Inserido com sucesso";
        }
    }

    public String insereDemarcacao(Demarcacao demarcacao) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put("Usuario_idUsuario", demarcacao.getUsuario_idUsuario());
        valores.put("Poligono_idPoligono", demarcacao.getPoligono_idPoligono());
        valores.put("Classe_idClasse", demarcacao.getClasse_idClasse());
        valores.put("comentariosDemarcacao", demarcacao.getComentariosDemarcacao());
        valores.put("statusDemarcacao", demarcacao.getStatusDemarcacao());

        resultado = db.insert("demarcacao", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir demarcação";
        else
            return "Demarcação inserida com sucesso";
    }

    /*-------------------------------------------------------------------------------------------*/
    /*---------------------------------------ALTERAÇÕES-------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public void alteraidPoligonoSistema(int idPoligono, String idPoligonoSistema) {
        ContentValues valores;
        String where;

        db = banco.getWritableDatabase();

        where = "IdPoligono" + "=" + idPoligono;
        valores = new ContentValues();
        valores.put("idPoligonoSistema", idPoligonoSistema);

        db.update("Poligono", valores, where, null);
        db.close();
    }

    public void alteracoodernadasDemarcacao(int idDemarcacao, String coodernadasDemarcacao) {
        ContentValues valores;
        String where;

        db = banco.getWritableDatabase();

        where = "idDemarcacao" + "=" + idDemarcacao;
        valores = new ContentValues();
        valores.put("coodernadasDemarcacao", coodernadasDemarcacao);

        db.update("demarcacao", valores, where, null);
        db.close();
    }

    /*-------------------------------------------------------------------------------------------*/
    /*---------------------------------------SELEÇÕES--------------------------------------------*/
    /*-------------------------------------------------------------------------------------------*/
    public Demarcacao selecionarDemarcacaoPorId(int idDemarcacao) {
        String query = "SELECT * FROM demarcacao where idDemarcacao =" + idDemarcacao;

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int usuario_idUsuario = cursor.getInt(cursor.getColumnIndex("Usuario_idUsuario"));
        int poligono_idPoligono = cursor.getInt(cursor.getColumnIndex("Poligono_idPoligono"));
        int classe_idClasse = cursor.getInt(cursor.getColumnIndex("Classe_idClasse"));
        String comentariosDemarcacao = cursor.getString(cursor.getColumnIndex("comentariosDemarcacao"));
        String coodernadasDemarcacao = cursor.getString(cursor.getColumnIndex("coodernadasDemarcacao"));
        String statusDemarcacao = cursor.getString(cursor.getColumnIndex("statusDemarcacao"));

        Demarcacao demarcacao = new Demarcacao(idDemarcacao, usuario_idUsuario, poligono_idPoligono, classe_idClasse, comentariosDemarcacao, coodernadasDemarcacao, statusDemarcacao);

        cursor.close();
        return demarcacao;
    }

    public int selecionarIddaUltimaDemarcacaoInserida() {
        String query = "SELECT max(idDemarcacao) as idDemarcacao FROM demarcacao";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int idDemarcacao = cursor.getInt(cursor.getColumnIndex("idDemarcacao"));

        cursor.close();
        return idDemarcacao;
    }

    public int selecionarClasseDaDemarcacaoPorIdDemarcacao(int idDermacacao) {
        String query = "SELECT Classe_idClasse FROM demarcacao where idDemarcacao =" + idDermacacao;

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        int classe_idClasse = cursor.getInt(cursor.getColumnIndex("Classe_idClasse"));

        cursor.close();
        return classe_idClasse;
    }

    public int selecionarIdClasse(String nomeClasse) {
        String sql = "SELECT idClasse FROM classe WHERE nomeClasse='" + nomeClasse + "' LIMIT 1";
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            int idClasse = cursor.getInt(cursor.getColumnIndex("idClasse"));
            cursor.close();
            return idClasse;
        }
    }

    public int selecionarIdMapaNomeMapa(String nomeMapa) {
        String sql = "SELECT idMapa FROM mapa WHERE nomeMapa='" + nomeMapa + "' LIMIT 1";
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            int idMapa = cursor.getInt(cursor.getColumnIndex("idMapa"));
            cursor.close();
            return idMapa;
        }
    }

    public int selecionarIdMapaCidadeMapa(String cidadeMapa) {
        String sql = "SELECT idMapa FROM mapa WHERE cidadeMapa='" + cidadeMapa + "' LIMIT 1";
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            int idMapa = cursor.getInt(cursor.getColumnIndex("idMapa"));
            cursor.close();
            return idMapa;
        }
    }

    public String selecionarNomeClassePorId(int idClasse) {
        String sql = "SELECT nomeClasse FROM classe WHERE idClasse=" + idClasse;
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return "não encontrado";
        } else {
            String nomeClasse = cursor.getString(cursor.getColumnIndex("nomeClasse"));
            cursor.close();
            return nomeClasse;
        }
    }

    public String selecionarCidadePorIdMapa(int idMapa) {
        String sql = "SELECT cidadeMapa FROM mapa WHERE idMapa=" + idMapa;
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return "não encontrado";
        } else {
            String cidadeMapa = cursor.getString(cursor.getColumnIndex("cidadeMapa"));
            cursor.close();
            return cidadeMapa;
        }
    }

    public Poligono selecionarPoligonoPorId(int idPoligono) {
        String sql = "SELECT * FROM poligono WHERE idPoligono=" + idPoligono;
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {

            String coodernadasPoligono = cursor.getString(cursor.getColumnIndex("coodernadasPoligono"));
            int idClasse = cursor.getInt(cursor.getColumnIndex("Classe_idClasse"));
            int idMapa = cursor.getInt(cursor.getColumnIndex("Mapa_idMapa"));

            Poligono poligono = new Poligono();
            poligono.setCoodernadasPoligono(coodernadasPoligono);
            poligono.setClassePoligono(idClasse);
            poligono.setMapaPoligono(idMapa);
            cursor.close();

            return poligono;
        }
    }

    public int SelecionaridPoligonoPorIdPoligonoSistema(String idPoligonoSistema) {
        String sql = "SELECT idPoligono FROM poligono WHERE idPoligonoSistema='" + idPoligonoSistema + "'";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            int idPoligono = cursor.getInt(cursor.getColumnIndex("idPoligono"));
            cursor.close();
            return idPoligono;
        }
    }

    public int SelecionaridClassePoligonoPorIdPoligonoSistema(String idPoligonoSistema) {
        String sql = "SELECT Classe_idClasse FROM poligono WHERE idPoligonoSistema='" + idPoligonoSistema + "'";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            int idClasse = cursor.getInt(cursor.getColumnIndex("Classe_idClasse"));
            cursor.close();
            return idClasse;
        }
    }

    public String SelecionarCoodernadaParaCidade(String Cidade) {
        int idMapa = selecionarIdMapaCidadeMapa(Cidade);
        int idClasse = selecionarIdClasse("Cafe");
        String sql = "SELECT coodernadasPoligono FROM poligono WHERE Mapa_idMapa=" + idMapa + " and Classe_idClasse =" + idClasse + " LIMIT 10";
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return "Nao encontrado";
        } else {
            String coodernadaPoligono = cursor.getString(cursor.getColumnIndex("coodernadasPoligono"));
            cursor.close();
            return coodernadaPoligono;
        }
    }

    /*-------------------------------------------------------------------------------------------*/
    /*---------------------------------------LISTAGEM--------------------------------------------*/
    /*-------------------------------------------------------------------------------------------*/
    public List<String> ListarTodosPoligonos() {
        List<String> ListPoligonos = new ArrayList<>();
        String query = "SELECT coodernadasPoligono FROM poligono";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.i("Quantidade BANCO", "q: " + cursor.getCount());

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String poligono = cursor.getString(cursor.getColumnIndex("coodernadasPoligono"));
            ListPoligonos.add(poligono);
            cursor.moveToNext();
        }
        cursor.close();
        return ListPoligonos;
    }

    public List<Poligono> ListarTodosPoligonosObjetos() {
        List<Poligono> ListPoligonos = new ArrayList<>();

        String query = "SELECT * FROM poligono Where Classe_idClasse = 3 ORDER BY Classe_idClasse DESC";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.i("Quantidade BANCO", "q: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Poligono poligono = new Poligono();
            poligono.setIdPoligono(cursor.getInt(cursor.getColumnIndex("idPoligono")));
            poligono.setCoodernadasPoligono(cursor.getString(cursor.getColumnIndex("coodernadasPoligono")));
            poligono.setClassePoligono(cursor.getInt(cursor.getColumnIndex("Classe_idClasse")));
            poligono.setMapaPoligono(cursor.getInt(cursor.getColumnIndex("Mapa_idMapa")));
            ListPoligonos.add(poligono);

            cursor.moveToNext();
        }
        cursor.close();
        return ListPoligonos;
    }

    public List<Poligono> ListarTodosPoligonosObjetosCidade(String Cidade) {

        int idMapa = selecionarIdMapaCidadeMapa(Cidade);
        List<Poligono> ListPoligonos = new ArrayList<>();
        String query = "SELECT * FROM poligono WHERE Mapa_idMapa = " + idMapa + " ORDER BY Classe_idClasse DESC";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.i("Quantidade BANCO", "q: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Poligono poligono = new Poligono();
            poligono.setIdPoligono(cursor.getInt(cursor.getColumnIndex("idPoligono")));
            poligono.setCoodernadasPoligono(cursor.getString(cursor.getColumnIndex("coodernadasPoligono")));
            poligono.setClassePoligono(cursor.getInt(cursor.getColumnIndex("Classe_idClasse")));
            poligono.setMapaPoligono(cursor.getInt(cursor.getColumnIndex("Mapa_idMapa")));
            ListPoligonos.add(poligono);

            cursor.moveToNext();
        }
        cursor.close();
        return ListPoligonos;
    }

    public Poligono PegarPoligono(int idPoligono) {

        String query = "SELECT * FROM poligono WHERE idPoligono = " + idPoligono;

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Poligono poligono = new Poligono();
        poligono.setIdPoligono(cursor.getInt(cursor.getColumnIndex("idPoligono")));
        poligono.setCoodernadasPoligono(cursor.getString(cursor.getColumnIndex("coodernadasPoligono")));
        poligono.setClassePoligono(cursor.getInt(cursor.getColumnIndex("Classe_idClasse")));
        poligono.setMapaPoligono(cursor.getInt(cursor.getColumnIndex("Mapa_idMapa")));

        cursor.close();
        return poligono;
    }

    public List<String> ListarPoligonosPorClasse(String nomeClasse) {
        List<String> ListPoligonos = new ArrayList<>();
        int idClasse = selecionarIdClasse(nomeClasse);
        String query = "SELECT coodernadasPoligono FROM poligono WHERE Classe_idClasse=" + idClasse;

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {  // moves the cursor to the first row in the result set...
            String poligono = cursor.getString(cursor.getColumnIndex("coodernadasPoligono"));
            ListPoligonos.add(poligono);
        }

        cursor.close();

        return ListPoligonos;
    }

    public List<String> ListarTodasAsCidades() {
        List<String> ListCidades = new ArrayList<>();
        //String query = "SELECT cidadeMapa FROM mapa ORDER BY cidadeMapa";
        String query = "SELECT m.cidadeMapa from poligono as p inner join mapa as m on m.idMapa = p.Mapa_idMapa group by m.cidadeMapa";
        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String cidade = cursor.getString(cursor.getColumnIndex("cidadeMapa"));
            ListCidades.add(cidade);
            cursor.moveToNext();
        }
        cursor.close();
        return ListCidades;
    }

    public List<Demarcacao> ListarTodasDemarcacoes() {
        List<Demarcacao> ListDemarcacoes = new ArrayList<>();
        String query = "SELECT * FROM demarcacao group by Poligono_idPoligono";

        db = banco.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.i("Quantidade BANCO", "q: " + cursor.getCount());

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            int idDemarcacao = cursor.getInt(cursor.getColumnIndex("idDemarcacao"));
            int usuario_idUsuario = cursor.getInt(cursor.getColumnIndex("Usuario_idUsuario"));
            int poligono_idPoligono = cursor.getInt(cursor.getColumnIndex("Poligono_idPoligono"));
            int classe_idClasse = cursor.getInt(cursor.getColumnIndex("Classe_idClasse"));
            String comentariosDemarcacao = cursor.getString(cursor.getColumnIndex("comentariosDemarcacao"));
            String coodernadasDemarcacao = cursor.getString(cursor.getColumnIndex("coodernadasDemarcacao"));
            String statusDemarcacao = cursor.getString(cursor.getColumnIndex("statusDemarcacao"));


            Demarcacao demarcacao = new Demarcacao(idDemarcacao, usuario_idUsuario, poligono_idPoligono, classe_idClasse, comentariosDemarcacao, coodernadasDemarcacao, statusDemarcacao);

            ListDemarcacoes.add(demarcacao);
            cursor.moveToNext();
        }
        cursor.close();
        return ListDemarcacoes;
    }


}
