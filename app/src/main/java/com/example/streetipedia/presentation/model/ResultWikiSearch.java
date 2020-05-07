package com.example.streetipedia.presentation.model;

import java.io.Serializable;

//Class create on the model of the Wikipedia search API call
public class ResultWikiSearch implements Serializable {

    private Integer pageid;

    public ResultWikiSearch(Integer pageid) {
        this.pageid = pageid;
    }

    public Integer getPageid() {
        return pageid;
    }
}
