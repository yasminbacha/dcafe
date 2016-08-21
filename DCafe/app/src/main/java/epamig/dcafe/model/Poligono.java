package epamig.dcafe.model;

public class Poligono {
    int idPoligono;
    String idPoligonoSistema;
    String coodernadasPoligono;
    int classePoligono;
    int mapaPoligono;


    public String getIdPoligonoSistema() {
        return idPoligonoSistema;
    }

    public void setIdPoligonoSistema(String idPoligonoSistema) {
        this.idPoligonoSistema = idPoligonoSistema;
    }

    public int getIdPoligono() {
        return idPoligono;
    }

    public void setIdPoligono(int idPoligono) {
        this.idPoligono = idPoligono;
    }

    public String getCoodernadasPoligono() {
        return coodernadasPoligono;
    }

    public void setCoodernadasPoligono(String coodernadasPoligono) {
        this.coodernadasPoligono = coodernadasPoligono;
    }

    public int getClassePoligono() {
        return classePoligono;
    }

    public void setClassePoligono(int classePoligono) {
        this.classePoligono = classePoligono;
    }

    public int getMapaPoligono() {
        return mapaPoligono;
    }

    public void setMapaPoligono(int mapaPoligono) {
        this.mapaPoligono = mapaPoligono;
    }

    public String convertePoligonoParaString(Poligono poligono) {
        String poligonoString = "";
        poligonoString += "ID: " + poligono.getIdPoligono();
        poligonoString += " Coodernadas: " + poligono.getCoodernadasPoligono();
        poligonoString += " Classe: " + poligono.getClassePoligono();
        poligonoString += " Mapa: " + poligono.getMapaPoligono();

        return poligonoString;
    }
}
