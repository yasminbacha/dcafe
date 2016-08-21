package epamig.dcafe.model;

public class Classe {
    int idClasse;
    String nomeClasse;
    String corClasse;


    public Classe(int idClasse, String nomeClasse, String corClasse) {
        this.idClasse = idClasse;
        this.nomeClasse = nomeClasse;
        this.corClasse = corClasse;
    }

    public Classe(String nomeClasse, String corClasse) {
        this.nomeClasse = nomeClasse;
        this.corClasse = corClasse;
    }

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }

    public String getNomeClasse() {
        return nomeClasse;
    }

    public void setNomeClasse(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public String getCorClasse() {
        return corClasse;
    }

    public void setCorClasse(String corClasse) {
        this.corClasse = corClasse;
    }
}
