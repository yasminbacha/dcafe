package epamig.dcafe.model;

public class Mapa {
    int idMapa;
    String nomeMapa;
    String cidadeMapa;

    public Mapa(int idMapa, String nomeMapa, String cidadeMapa) {
        this.idMapa = idMapa;
        this.nomeMapa = nomeMapa;
        this.cidadeMapa = cidadeMapa;
    }

    public Mapa(String nomeMapa, String cidadeMapa) {
        this.nomeMapa = nomeMapa;
        this.cidadeMapa = cidadeMapa;
    }

    public int getIdMapa() {
        return idMapa;
    }

    public void setIdMapa(int idMapa) {
        this.idMapa = idMapa;
    }

    public String getNomeMapa() {
        return nomeMapa;
    }

    public void setNomeMapa(String nomeMapa) {
        this.nomeMapa = nomeMapa;
    }

    public String getCidadeMapa() {
        return cidadeMapa;
    }

    public void setCidadeMapa(String cidadeMapa) {
        this.cidadeMapa = cidadeMapa;
    }
}
