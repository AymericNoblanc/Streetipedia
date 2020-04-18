package com.example.myfirstandroidproject;

import java.io.Serializable;

public class ResultWikiInfo implements Serializable {

    private String title;
    private String extract;

    public ResultWikiInfo(String title, String extract) {
        this.title = title;
        this.extract = extract;
    }

    String getTitle() {
        return title;
    }

    String getExtract() {
        return extract;
    }
}