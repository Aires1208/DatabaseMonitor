package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/7/16.
 */
@JsonSerialize(using = DbsSerializer.class)
public class Dbs {
    private List<Db> dbList;

    public Dbs(List<Db> dbList) {
        this.dbList = dbList;
    }

    public List<Db> getDbList() {
        return dbList;
    }
}
