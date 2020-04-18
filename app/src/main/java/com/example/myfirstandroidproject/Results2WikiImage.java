package com.example.myfirstandroidproject;


import com.google.gson.annotations.SerializedName;

public class Results2WikiImage {

    @SerializedName("rf")
    public ResultWikiImage pagesid;

    public ResultWikiImage getPagesid() {
        return pagesid;
    }
}