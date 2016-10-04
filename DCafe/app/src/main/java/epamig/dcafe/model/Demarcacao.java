package epamig.dcafe.model;

public class Demarcacao {
    int idDemarcacao;
    int Usuario_idUsuario;
    int Poligono_idPoligono;
    int Classe_idClasse;
    String comentariosDemarcacao;
    String coodernadasDemarcacao;
    String statusDemarcacao;
    int flagSincronizado;

    public Demarcacao() {
    }
/*
    public Demarcacao(int idDemarcacao, int usuario_idUsuario, int poligono_idPoligono, int classe_idClasse, String comentariosDemarcacao, String coodernadasDemarcacao, String statusDemarcacao) {
        this.idDemarcacao = idDemarcacao;
        Usuario_idUsuario = usuario_idUsuario;
        Poligono_idPoligono = poligono_idPoligono;
        Classe_idClasse = classe_idClasse;
        this.comentariosDemarcacao = comentariosDemarcacao;
        this.coodernadasDemarcacao = coodernadasDemarcacao;
        this.statusDemarcacao = statusDemarcacao;
    }*/

 /*   public Demarcacao(int usuario_idUsuario, int poligono_idPoligono, int classe_idClasse, String comentariosDemarcacao, String coodernadasDemarcacao, String statusDemarcacao, int flagSincronizado) {
        Usuario_idUsuario = usuario_idUsuario;
        Poligono_idPoligono = poligono_idPoligono;
        Classe_idClasse = classe_idClasse;
        this.comentariosDemarcacao = comentariosDemarcacao;
        this.coodernadasDemarcacao = coodernadasDemarcacao;
        this.statusDemarcacao = statusDemarcacao;
        this.flagSincronizado = flagSincronizado;
    }*/


    public Demarcacao(int idDemarcacao, int usuario_idUsuario, int poligono_idPoligono, int classe_idClasse, String comentariosDemarcacao, String coodernadasDemarcacao, String statusDemarcacao, int flagSincronizado) {
        this.idDemarcacao = idDemarcacao;
        Usuario_idUsuario = usuario_idUsuario;
        Poligono_idPoligono = poligono_idPoligono;
        Classe_idClasse = classe_idClasse;
        this.comentariosDemarcacao = comentariosDemarcacao;
        this.coodernadasDemarcacao = coodernadasDemarcacao;
        this.statusDemarcacao = statusDemarcacao;
        this.flagSincronizado = flagSincronizado;
    }

    public void setFlagSincronizado(int flagSincronizado) {
        this.flagSincronizado = flagSincronizado;
    }

    public int getIdDemarcacao() {
        return idDemarcacao;
    }

    public void setIdDemarcacao(int idDemarcacao) {
        this.idDemarcacao = idDemarcacao;
    }

    public int getUsuario_idUsuario() {
        return Usuario_idUsuario;
    }

    public void setUsuario_idUsuario(int usuario_idUsuario) {
        Usuario_idUsuario = usuario_idUsuario;
    }

    public int getPoligono_idPoligono() {
        return Poligono_idPoligono;
    }

    public void setPoligono_idPoligono(int poligono_idPoligono) {
        Poligono_idPoligono = poligono_idPoligono;
    }

    public int getClasse_idClasse() {
        return Classe_idClasse;
    }

    public void setClasse_idClasse(int classe_idClasse) {
        Classe_idClasse = classe_idClasse;
    }

    public String getComentariosDemarcacao() {
        return comentariosDemarcacao;
    }

    public void setComentariosDemarcacao(String comentariosDemarcacao) {
        this.comentariosDemarcacao = comentariosDemarcacao;
    }

    public String getCoodernadasDemarcacao() {
        return coodernadasDemarcacao;
    }

    public void setCoodernadasDemarcacao(String coodernadasDemarcacao) {
        this.coodernadasDemarcacao = coodernadasDemarcacao;
    }

    public String getStatusDemarcacao() {
        return statusDemarcacao;
    }

    public void setStatusDemarcacao(String statusDemarcacao) {
        this.statusDemarcacao = statusDemarcacao;
    }
}
