package com.example.streekipedia.presentation.model;

import java.io.Serializable;

public class ResultWikiSearch implements Serializable {

    private Integer pageid;

    public ResultWikiSearch(Integer pageid) {
        this.pageid = pageid;
    }

    public Integer getPageid() {
        return pageid;
    }
}
