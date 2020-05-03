package com.example.streekipedia;

import java.io.Serializable;

public class ResultWikiInfo implements Serializable {

    private String extract;

    public ResultWikiInfo(String extract) {
        this.extract = extract;
    }

    String getExtract() {
        return extract;
    }
}