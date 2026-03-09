package br.com.his.admin.dto;

public class PacienteCatalogoItem {

    private Long id;
    private String principal;
    private String secundario;
    private String extra;
    private String relacionamento;
    private String relacionamentoSecundario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPrincipal() { return principal; }
    public void setPrincipal(String principal) { this.principal = principal; }
    public String getSecundario() { return secundario; }
    public void setSecundario(String secundario) { this.secundario = secundario; }
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
    public String getRelacionamento() { return relacionamento; }
    public void setRelacionamento(String relacionamento) { this.relacionamento = relacionamento; }
    public String getRelacionamentoSecundario() { return relacionamentoSecundario; }
    public void setRelacionamentoSecundario(String relacionamentoSecundario) { this.relacionamentoSecundario = relacionamentoSecundario; }
}
