package com.github.nexy.database.components.constructor;

import lombok.Getter;

import java.io.File;

@Getter
public class ConnectionData {

    private final long createdIn;
    private String host;
    private String database;
    private String username;
    private String password;

    private File path;

    public ConnectionData(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.createdIn = System.currentTimeMillis();
    }

    public ConnectionData(File path) {
        this.path = path;
        this.createdIn = System.currentTimeMillis();
    }
}
