package com.example.streekipedia;

public class RestWikipediaResponseSearch {

    private ResultsWikiSearch query;

    public RestWikipediaResponseSearch(ResultsWikiSearch query) {
        this.query = query;
    }

    ResultsWikiSearch getQuery() {
        return query;
    }
}
