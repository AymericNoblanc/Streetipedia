package com.example.streetipedia.presentation.model;

import java.util.List;

//Class create on the model of the Wikipedia info API call
public class ResultsWikiInfo {

    private List<ResultWikiInfo> pages;

    public ResultsWikiInfo(List<ResultWikiInfo> pages) {
        this.pages = pages;
    }

    public List<ResultWikiInfo> getPages() {
        return pages;
    }
}
