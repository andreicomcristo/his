package br.com.his.patient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "his.cadsus")
public class CadsusProperties {

    private boolean enabled;
    private String tokenUrl = "https://ehr-auth.saude.gov.br/api/osb/token";
    private String supplierUrl = "https://servicos.saude.gov.br/cadsus/v2/PDQSupplierJWT";
    private String certPath;
    private String certPassword;
    private String certType = "PKCS12";
    private String sslProtocol = "TLS";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getSupplierUrl() {
        return supplierUrl;
    }

    public void setSupplierUrl(String supplierUrl) {
        this.supplierUrl = supplierUrl;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }
}
