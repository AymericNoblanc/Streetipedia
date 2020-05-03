package com.example.streekipedia;

import java.util.List;

public class ResultsWikiSearch {

    private List<ResultWikiSearch> search;

    public ResultsWikiSearch(List<ResultWikiSearch> search) {
        this.search = search;
    }

    List<ResultWikiSearch> getSearch() {
        return search;
    }
}
