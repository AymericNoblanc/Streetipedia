package com.example.myfirstandroidproject;

public class RestWikipediaResponseInfo {

    private ResultsWikiInfo query;

    public RestWikipediaResponseInfo(ResultsWikiInfo query) {
        this.query = query;
    }

    ResultsWikiInfo getQuery() {
        return query;
    }
}