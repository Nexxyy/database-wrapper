package com.github.nexy.database.factory;

import com.github.nexy.database.components.constructor.ConnectionData;
import com.github.nexy.database.constants.DatabaseType;
import com.github.nexy.database.factory.entity.Factory;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

@Getter
public class ConnectionProvider extends Factory {

    private final DatabaseType databaseType;
    private Connection connection;
    private boolean opened;
    private ConnectionData connectionData;
    private File databaseFilePath;

    public ConnectionProvider(@NonNull DatabaseType databaseType, ConnectionData connectionData) {
        this.databaseType = databaseType;
        this.connectionData = connectionData;
    }

    public ConnectionProvider(@NonNull DatabaseType databaseType, File databaseFilePath) {
        this.databaseType = databaseType;
        this.databaseFilePath = databaseFilePath;
        this.mkdirDatabaseFolder();
        this.createDatabaseFile();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void openConnection() {
        switch (databaseType) {
            case SQLITE:
                try {
                    Class.forName("org.sqlite.JDBC");
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseFilePath);
                    this.opened = true;
                } catch (Exception var) {
                    this.opened = false;
                    System.out.println("Unable to open a SQLITE connection, please check the code! Exception: " + var.getClass().getName());
                }
                break;
            case MYSQL:
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String databaseUrl = "jdbc:mysql://" + connectionData.getHost() + ":3306/" + connectionData.getDatabase() + "?autoReconnect=true";
                    this.connection = DriverManager.getConnection(databaseUrl, connectionData.getUsername(), connectionData.getPassword());
                } catch (Exception var) {
                    this.opened = false;
                    System.out.println("Unable to open a MYSQL connection, please check the code! Exception: " + var.getClass().getName());
                    var.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (Exception var) {
            System.out.println("It was not possible to close a connection to the database, due to the exception: " + var.getClass().getName());
            var.printStackTrace();
        }
    }

    @Override
    public void mkdirDatabaseFolder() {
        if (databaseType == DatabaseType.SQLITE) {
            final File directory = new File(databaseFilePath + "/storage");

            if (!(directory.exists())) {
                try {
                    directory.mkdirs();
                } catch (Exception var) {
                    System.out.println("Unable to create storage directory due to exception: " + var.getClass().getName());
                }
            }
        }
    }

    @Override
    public void createDatabaseFile() {
        if (databaseType == DatabaseType.SQLITE) {
            this.databaseFilePath = new File(databaseFilePath + "/storage", "database.db");

            if (!(this.databaseFilePath.exists())) {
                try {
                    this.databaseFilePath.createNewFile();
                } catch (Exception var) {
                    System.out.println("Unable to create storage file by exception: " + var.getClass().getName());
                }
            }
        }
    }
}
