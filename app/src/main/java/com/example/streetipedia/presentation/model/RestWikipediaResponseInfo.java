package com.example.streetipedia.presentation.model;

//Class create on the model of the Wikipedia info API call
public class RestWikipediaResponseInfo {

    private ResultsWikiInfo query;

    public RestWikipediaResponseInfo(ResultsWikiInfo query) {
        this.query = query;
    }

    public ResultsWikiInfo getQuery() {
        return query;
    }
}