package com.github.nexy.database;

import com.github.nexy.database.annotations.StoreVoid;
import com.github.nexy.database.components.Database;
import com.github.nexy.database.components.constructor.ConnectionData;
import com.github.nexy.database.constants.DatabaseType;
import com.github.nexy.database.factory.ConnectionProvider;
import com.github.nexy.database.model.DatabaseModel;
import com.github.nexy.database.task.SaveQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Getter
public class StorageBuilder {

    private final DatabaseType databaseType;
    private Database singletonDatabase;
    private ConnectionData connectionData;
    private SaveQueue saveQueue;
    private DatabaseModel databaseModel;

    public StorageBuilder setConnectionData(String host, String database, String username, String password) {
        this.connectionData = new ConnectionData(host, database, username, password);
        return this;
    }

    public StorageBuilder setConnectionData(File pathBase) {
        this.connectionData = new ConnectionData(pathBase);
        return this;
    }

    public StorageBuilder useSingleThreadQueue(int seconds) {
        this.saveQueue = new SaveQueue(seconds, this);
        return this;
    }

    public StorageBuilder selectDatabaseModel(DatabaseModel savable) {
        this.databaseModel = savable;
        Method[] declaredMethods = databaseModel.getClass().getDeclaredMethods();

        if (declaredMethods.length == 0) {
            System.out.println("ERROR! Could not initialize class " + databaseModel.getClass().getName() + " because it has no method that will start a query");
            return this;
        }

        Arrays.stream(declaredMethods).forEach(savableMethod -> {
            StoreVoid annotation = savableMethod.getAnnotation(StoreVoid.class);
            if (annotation == null) return;

            savableMethod.setAccessible(true);
            try {
                savableMethod.invoke(databaseModel);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("ERROR! There was a problem initializing method " + savableMethod.getName() + ", please check the code provider and ask for repair");
            }
        });

        return this;
    }

    public Database build() {
        final AtomicReference<ConnectionProvider> atomicConnection = new AtomicReference<>();

        switch (databaseType) {
            case SQLITE:
                atomicConnection.set(new ConnectionProvider(
                  this.databaseType,
                  this.connectionData.getPath()
                ));
                break;
            case MYSQL:
                atomicConnection.set(new ConnectionProvider(
                  this.databaseType,
                  this.connectionData
                ));
                break;
        }

        if (atomicConnection.get() == null)
            throw new NullPointerException("The connection provider failed in opening... plase check the code");

        atomicConnection.get().openConnection();
        this.singletonDatabase = new Database(atomicConnection.get().getConnection(), databaseModel.getTables(), saveQueue);
        return this.singletonDatabase;
    }

}