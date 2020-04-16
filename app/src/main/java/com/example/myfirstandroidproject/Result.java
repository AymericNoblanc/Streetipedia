package com.example.myfirstandroidproject;

import java.io.Serializable;

public class Result implements Serializable {

    private String title;
    private Integer pageid;
    private String snippet;

    public String getTitle() {
        return title;
    }

    public Integer getPageid() {
        return pageid;
    }

    public String getSnippet() {
        return snippet;
    }
}
