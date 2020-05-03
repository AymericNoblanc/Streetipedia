package com.example.streekipedia;

import java.io.Serializable;

public class ResultWikiSearch implements Serializable {

    private Integer pageid;

    public ResultWikiSearch(Integer pageid) {
        this.pageid = pageid;
    }

    Integer getPageid() {
        return pageid;
    }
}
