package com.example.streekipedia.presentation.model;

import java.io.Serializable;

public class ResultWikiInfo implements Serializable {

    private String extract;

    public ResultWikiInfo(String extract) {
        this.extract = extract;
    }

    public String getExtract() {
        return extract;
    }
}