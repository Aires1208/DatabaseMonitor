package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/1/16.
 */

@JsonSerialize(using = DBInfoObjectsSerializer.class)
public class DBInfoObjects {
    private List<User> users;
    private List<Variable> variables;
    private List<Storage> storages;
    private Log log;
    private Database database;

    public DBInfoObjects(List<User> users, List<Variable> variables, List<Storage> storages, Log log, Database database) {
        this.users = users;
        this.variables = variables;
        this.storages = storages;
        this.log = log;
        this.database = database;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Storage> getStorages() {
        return storages;
    }

    public Log getLog() {
        return log;
    }

    public Database getDatabase() {
        return database;
    }


    public static class User {
        private String userName;
        private String host;

        public User(String userName, String host) {
            this.userName = userName;
            this.host = host;
        }

        public String getUserName() {
            return userName;
        }

        public String getHost() {
            return host;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (userName != null ? !userName.equals(user.userName) : user.userName != null) return false;
            return host != null ? host.equals(user.host) : user.host == null;

        }

        @Override
        public int hashCode() {
            int result = userName != null ? userName.hashCode() : 0;
            result = 31 * result + (host != null ? host.hashCode() : 0);
            return result;
        }
    }

    public static class Variable {
        private String variableName;
        private String variableValue;

        public Variable(String variableName, String variableValue) {
            this.variableName = variableName;
            this.variableValue = variableValue;
        }

        public String getVariableName() {
            return variableName;
        }

        public String getVariableValue() {
            return variableValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Variable variable = (Variable) o;

            if (variableName != null ? !variableName.equals(variable.variableName) : variable.variableName != null)
                return false;
            return variableValue != null ? variableValue.equals(variable.variableValue) : variable.variableValue == null;

        }

        @Override
        public int hashCode() {
            int result = variableName != null ? variableName.hashCode() : 0;
            result = 31 * result + (variableValue != null ? variableValue.hashCode() : 0);
            return result;
        }
    }

    public static class Storage {
        private String databaseName;
        private String indexLength;
        private String dataLength;

        public Storage(String databaseName, String indexLength, String dataLength) {
            this.databaseName = databaseName;
            this.indexLength = indexLength;
            this.dataLength = dataLength;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public String getIndexLength() {
            return indexLength;
        }

        public String getDataLength() {
            return dataLength;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Storage storage = (Storage) o;

            if (databaseName != null ? !databaseName.equals(storage.databaseName) : storage.databaseName != null)
                return false;
            if (indexLength != null ? !indexLength.equals(storage.indexLength) : storage.indexLength != null)
                return false;
            return dataLength != null ? dataLength.equals(storage.dataLength) : storage.dataLength == null;

        }

        @Override
        public int hashCode() {
            int result = databaseName != null ? databaseName.hashCode() : 0;
            result = 31 * result + (indexLength != null ? indexLength.hashCode() : 0);
            result = 31 * result + (dataLength != null ? dataLength.hashCode() : 0);
            return result;
        }
    }

    public static class Log {
        private String logPath;

        public Log(String logPath) {
            this.logPath = logPath;
        }

        public String getLogPath() {
            return logPath;
        }
    }

    public static class Database {
        private String upTime;
        private String version;

        public Database(String upTime, String version) {
            this.upTime = upTime;
            this.version = version;
        }

        public String getUpTime() {
            return upTime;
        }

        public String getVersion() {
            return version;
        }
    }
}
