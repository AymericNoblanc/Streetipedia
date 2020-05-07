package com.example.streetipedia.presentation.model;

//Class create on the model of the Wikipedia search API call
public class RestWikipediaResponseSearch {

    private ResultsWikiSearch query;

    public RestWikipediaResponseSearch(ResultsWikiSearch query) {
        this.query = query;
    }

    public ResultsWikiSearch getQuery() {
        return query;
    }
}
