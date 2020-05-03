package com.example.streekipedia;

import java.util.List;

public class ResultsWikiInfo {

    private List<ResultWikiInfo> pages;

    public ResultsWikiInfo(List<ResultWikiInfo> pages) {
        this.pages = pages;
    }

    List<ResultWikiInfo> getPages() {
        return pages;
    }
}
