package com.example.streetipedia.presentation.model;

public class RestWikipediaResponseInfo {

    private ResultsWikiInfo query;

    public RestWikipediaResponseInfo(ResultsWikiInfo query) {
        this.query = query;
    }

    public ResultsWikiInfo getQuery() {
        return query;
    }
}