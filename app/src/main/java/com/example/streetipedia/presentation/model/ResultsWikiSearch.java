package com.example.streetipedia.presentation.model;

import java.util.List;

//Class create on the model of the Wikipedia search API call
public class ResultsWikiSearch {

    private List<ResultWikiSearch> search;

    public ResultsWikiSearch(List<ResultWikiSearch> search) {
        this.search = search;
    }

    public List<ResultWikiSearch> getSearch() {
        return search;
    }
}
