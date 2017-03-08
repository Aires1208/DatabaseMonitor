package com.zte.ums.oespaas.mysql.bean;

/**
 * Created by 10183966 on 8/17/16.
 */
public class DbBuilder {
    private String dbName;
    private String dbHost;
    private String osNeId;
    private String dbNeId;
    private String collectTime;
    private String dbType;
    private String status;
    private boolean health;
    private long queries;
    private String timeSpent;
    private String cpu;

    public DbBuilder DbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public DbBuilder DbHost(String dbHost) {
        this.dbHost = dbHost;
        return this;
    }

    public DbBuilder OsNeId(String osNeId) {
        this.osNeId = osNeId;
        return this;
    }

    public DbBuilder DbNeId(String dbNeId) {
        this.dbNeId = dbNeId;
        return this;
    }

    public DbBuilder CollectTime(String collectTime) {
        this.collectTime = collectTime;
        return this;
    }

    public DbBuilder Status(String status) {
        this.status = status;
        return this;
    }

    public DbBuilder DbType(String dbType) {
        this.dbType = dbType;
        return this;
    }

    public DbBuilder Health(boolean health) {
        this.health = health;
        return this;
    }

    public DbBuilder Queries(long queries) {
        this.queries = queries;
        return this;

    }

    public DbBuilder TimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
        return this;
    }

    public DbBuilder Cpu(String cpu) {
        this.cpu = cpu;
        return this;
    }

    public Db build() {
        Db db = new Db();
        db.setDbName(dbName);
        db.setDbHost(dbHost);
        db.setOsNeId(osNeId);
        db.setDbNeId(dbNeId);
        db.setCollectTime(collectTime);
        db.setStatus(status);
        db.setType(dbType);
        db.setHealth(health);
        db.setQueries(queries);
        db.setTimeSpent(timeSpent);
        db.setCpu(cpu);

        return db;
    }
}
