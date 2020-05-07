package com.example.streetipedia.presentation.model;

import java.io.Serializable;

//Class create on the model of the Wikipedia info API call
public class ResultWikiInfo implements Serializable {

    private String extract;

    public ResultWikiInfo(String extract) {
        this.extract = extract;
    }

    public String getExtract() {
        return extract;
    }
}