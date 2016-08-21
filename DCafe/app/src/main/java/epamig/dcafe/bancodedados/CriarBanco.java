package epamig.dcafe.bancodedados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriarBanco extends SQLiteOpenHelper {


    private static final String NOME_BANCO = "mapa.db";
    private static final int VERSAO = 1;

    public CriarBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlClasse = "CREATE TABLE classe(" +
                "idClasse integer primary key autoincrement," +
                "nomeClasse text," +
                "corClasse text" +
                ");";
        String sqlMapa = "CREATE TABLE mapa(" +
                "idMapa integer primary key autoincrement," +
                "nomeMapa text," +
                "cidadeMapa text" +
                ");";

        String sqlPoligono = "CREATE TABLE poligono(" +
                "idPoligono integer primary key autoincrement," +
                "coodernadasPoligono text," +
                "idPoligonoSistema text," +
                "Classe_idClasse integer not null," +
                "Mapa_idMapa integer not null," +
                "FOREIGN KEY(Classe_idClasse) REFERENCES classe(idClasse)," +
                "FOREIGN KEY(Mapa_idMapa) REFERENCES mapa(idMapa)" +
                ");";

        String sqlDemarcacao = "CREATE TABLE demarcacao(" +
                "idDemarcacao integer primary key autoincrement," +
                "Usuario_idUsuario integer not null," +
                "Poligono_idPoligono integer not null," +
                "Classe_idClasse integer not null," +
                "comentariosDemarcacao text," +
                "coodernadasDemarcacao text," +
                "statusDemarcacao text"+
                ");";
        db.execSQL(sqlClasse);
        db.execSQL(sqlMapa);
        db.execSQL(sqlPoligono);
        db.execSQL(sqlDemarcacao);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Classe;" +
                "DROP TABLE IF EXISTS Mapa;" +
                "DROP TABLE IF EXISTS Poligono;" +
                "DROP TABLE IF EXISTS Demarcacao;"+
                "DROP TABLE IF EXISTS Usuario;");
        onCreate(db);
    }
}
