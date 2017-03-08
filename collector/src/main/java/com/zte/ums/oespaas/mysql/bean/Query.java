package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by 10183966 on 8/18/16.
 */
@JsonSerialize(using = QuerySerializer.class)
public class Query {
    private String query;
    private String timeSpent;
    private String weight;

    public Query(String query, String timeSpent, String weight) {
        this.query = query;
        this.timeSpent = timeSpent;
        this.weight = weight;
    }

    public String getQuery() {
        return query;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public String getWeight() {
        return weight;
    }
}
