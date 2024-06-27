package org.airly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Installation {
    private int id;
    private Param param;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}
