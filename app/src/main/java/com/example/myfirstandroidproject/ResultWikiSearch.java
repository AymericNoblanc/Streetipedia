package com.example.myfirstandroidproject;

import java.io.Serializable;

public class ResultWikiSearch implements Serializable {

    private String title;
    private Integer pageid;
    private String snippet;

    public ResultWikiSearch(String title, Integer pageid, String snippet) {
        this.title = title;
        this.pageid = pageid;
        this.snippet = snippet;
    }

    String getTitle() {
        return title;
    }

    Integer getPageid() {
        return pageid;
    }

    String getSnippet() {
        return snippet;
    }
}
