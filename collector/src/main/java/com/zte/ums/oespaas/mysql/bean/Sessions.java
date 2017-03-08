package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/7/16.
 */
@JsonSerialize(using = SessionsSerializer.class)
public class Sessions {
    private List<Session> sessionList;

    public Sessions(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    public List<Session> getSessionList() {
        return sessionList;
    }
}
