package epamig.dcafe.model;

public class Usuario {
    int  idUsuario;
    String nomeUsuario;
    String emailUsuario;
    String profissaoUsuario;
    String senhaUsuario;
    String tipoUsuario;
    int sucesso;

    public int getSucesso() {
        return sucesso;
    }

    public Usuario(String nomeUsuario, String emailUsuario, String profissaoUsuario, String senhaUsuario) {
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.profissaoUsuario = profissaoUsuario;
        this.senhaUsuario = senhaUsuario;
    }

    public Usuario() {
    }

    public void setSucesso(int sucesso) {
        this.sucesso = sucesso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getProfissaoUsuario() {
        return profissaoUsuario;
    }

    public void setProfissaoUsuario(String profissaoUsuario) {
        this.profissaoUsuario = profissaoUsuario;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
