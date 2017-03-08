package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by 10183966 on 8/18/16.
 */
@JsonSerialize(using = ClientSerializer.class)
public class Client {
    private String client;
    private String count;
    private String weight;

    public Client(String client, String count, String weight) {
        this.client = client;
        this.count = count;
        this.weight = weight;
    }

    public String getClient() {
        return client;
    }

    public String getCount() {
        return count;
    }

    public String getWeight() {
        return weight;
    }
}
