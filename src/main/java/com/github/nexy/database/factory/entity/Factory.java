package com.github.nexy.database.factory.entity;

import java.sql.Connection;

public abstract class Factory {

    public abstract Connection getConnection();

    public abstract void openConnection();

    public abstract void close();

    public abstract void mkdirDatabaseFolder();

    public abstract void createDatabaseFile();

}
