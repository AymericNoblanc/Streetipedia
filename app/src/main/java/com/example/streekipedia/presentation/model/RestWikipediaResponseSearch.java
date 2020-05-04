package com.example.streekipedia.presentation.model;

public class RestWikipediaResponseSearch {

    private ResultsWikiSearch query;

    public RestWikipediaResponseSearch(ResultsWikiSearch query) {
        this.query = query;
    }

    public ResultsWikiSearch getQuery() {
        return query;
    }
}
