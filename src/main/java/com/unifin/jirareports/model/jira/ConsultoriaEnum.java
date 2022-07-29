package com.unifin.jirareports.model.jira;

public enum ConsultoriaEnum {
    ACI_GROUP("Consultoria-ACI_GROUP", "jjtoledanomorales@gmail.com"),
    AN_GLOBAL("Consultoria-AN_GLOBAL", "jjtoledanomorales@gmail.com"),
    EQUALLITY("Consultoria-EQUALLITY", "jjtoledanomorales@gmail.com"),
    INFOSYST("Consultoria-INFOSYST", "jjtoledanomorales@gmail.com"),
    KODE_FREE("Consultoria-KODE_FREE", "jjtoledanomorales@gmail.com"),
    KODE_IT("Consultoria-KodeIT", "jjtoledanomorales@gmail.com"),
    SOLDETI("Consultoria-SOLDETI", "jjtoledanomorales@gmail.com"),
    WIZELINE("Consultoria-WIZELINE", "jjtoledanomorales@gmail.com"),
    TACTOS("Consultoria-TACTOS", "jjtoledanomorales@gmail.com"),
    YAXCHE("Consultoria-YAXCHE", "jjtoledanomorales@gmail.com"),
    YAXCHE_QA("Consultoria-YAXCHE_QA", "jjtoledanomorales@gmail.com"),
    AYGG("Equipo-AYGG", "jjtoledanomorales@gmail.com"), CVV("Equipo-CVV", "jjtoledanomorales@gmail.com"),
    ERG("Equipo-ERG", "jjtoledanomorales@gmail.com"), WARP("Equipo-WARP", "jjtoledanomorales@gmail.com");

    private String group;
    private String email;

    ConsultoriaEnum(String group, String email) {
        this.group = group;
        this.email = email;
    }

    public String getGroup() {
        return group;
    }

    public String getEmail() {
        return email;
    }

    public void setGroup(final String group) {
        this.group = group;
    }
}
