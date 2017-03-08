package com.zte.ums.oespaas.mysql.bean.hbase;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

public class StatusBean extends AbstractBean {
    //neId == dbNeId
    private String questions;
    private String threads_running;
    private String com_select;
    private String up_time;
    private String vars;

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getThreads_running() {
        return threads_running;
    }

    public void setThreads_running(String threads_running) {
        this.threads_running = threads_running;
    }

    public String getCom_select() {
        return com_select;
    }

    public void setCom_select(String com_select) {
        this.com_select = com_select;
    }

    public String getUp_time() {
        return up_time;
    }

    public void setUp_time(String up_time) {
        this.up_time = up_time;
    }

    public String getVars() {
        return vars;
    }

    public void setVars(String vars) {
        this.vars = vars;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getQuestions());
        buffer.putPrefixedString(this.getThreads_running());
        buffer.putPrefixedString(this.getCom_select());
        buffer.putPrefixedString(this.getUp_time());
        buffer.putPrefixedString(this.getVars());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.questions = buffer.readPrefixedString();
        this.threads_running = buffer.readPrefixedString();
        this.com_select = buffer.readPrefixedString();
        this.up_time = buffer.readPrefixedString();
        this.vars = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "StatusBean{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "questions='" + questions + '\'' +
                ", threads_running='" + threads_running + '\'' +
                ", com_select='" + com_select + '\'' +
                ", up_time='" + up_time + '\'' +
                ", vars='" + vars + '\'' +
                '}';
    }
}
