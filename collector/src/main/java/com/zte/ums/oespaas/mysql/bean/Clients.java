package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/7/16.
 */
@JsonSerialize(using = ClientsSerializer.class)
public class Clients {
    private List<Client> clientList;

    public Clients(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Client> getClientList() {
        return clientList;
    }
}
