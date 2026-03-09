package br.com.his.assistencial.model;

import br.com.his.access.model.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leito")
public class Leito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_leito_id", nullable = false)
    private TipoLeito tipoLeito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_leito_id")
    private PerfilLeito perfilLeito;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(length = 255)
    private String descricao;

    @Column(name = "recebe_ps", nullable = false)
    private boolean recebePs;

    @Column(name = "permite_destino_definitivo", nullable = false)
    private boolean permiteDestinoDefinitivo;

    @Column(nullable = false)
    private boolean assistencial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "natureza_operacional_id", nullable = false)
    private NaturezaOperacionalLeito naturezaOperacional;

    @Column(nullable = false)
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public TipoLeito getTipoLeito() {
        return tipoLeito;
    }

    public void setTipoLeito(TipoLeito tipoLeito) {
        this.tipoLeito = tipoLeito;
    }

    public PerfilLeito getPerfilLeito() {
        return perfilLeito;
    }

    public void setPerfilLeito(PerfilLeito perfilLeito) {
        this.perfilLeito = perfilLeito;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isRecebePs() {
        return recebePs;
    }

    public void setRecebePs(boolean recebePs) {
        this.recebePs = recebePs;
    }

    public boolean isAssistencial() {
        return assistencial;
    }

    public void setAssistencial(boolean assistencial) {
        this.assistencial = assistencial;
    }

    public NaturezaOperacionalLeito getNaturezaOperacional() {
        return naturezaOperacional;
    }

    public void setNaturezaOperacional(NaturezaOperacionalLeito naturezaOperacional) {
        this.naturezaOperacional = naturezaOperacional;
    }

    public boolean isPermiteDestinoDefinitivo() {
        return permiteDestinoDefinitivo;
    }

    public void setPermiteDestinoDefinitivo(boolean permiteDestinoDefinitivo) {
        this.permiteDestinoDefinitivo = permiteDestinoDefinitivo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
