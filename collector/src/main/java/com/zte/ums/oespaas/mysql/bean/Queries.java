package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/7/16.
 */
@JsonSerialize(using = QueriesSerializer.class)
public class Queries {
    private List<Query> queryList;

    public Queries(List<Query> queryList) {
        this.queryList = queryList;
    }

    public List<Query> getQueryList() {
        return queryList;
    }
}
