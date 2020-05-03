package com.example.streekipedia;

public class RestWikipediaResponseInfo {

    private ResultsWikiInfo query;

    public RestWikipediaResponseInfo(ResultsWikiInfo query) {
        this.query = query;
    }

    ResultsWikiInfo getQuery() {
        return query;
    }
}