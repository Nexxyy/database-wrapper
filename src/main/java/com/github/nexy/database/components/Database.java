package com.github.nexy.database.components;

import com.github.nexy.database.constants.ComponentType;
import com.github.nexy.database.model.include.DatabasePerfecter;
import com.github.nexy.database.model.include.StandardActions;
import com.github.nexy.database.model.include.sync.DatabaseAsync;
import com.github.nexy.database.task.SaveQueue;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Database implements DatabasePerfecter, DatabaseAsync, StandardActions {

    private final Connection connection;
    private final SaveQueue saveQueue;

    public Database(Connection connection, List<Table> preCreation, SaveQueue saveQueue) {
        this.connection = connection;
        this.saveQueue = saveQueue;
        preCreation.forEach(table -> {
            switch (table.getTableType()) {
                case DEFAULT:
                    this.createTable(table.getName(), table.getTableComponents());
                    break;
                case JSON:
                    this.createJsonTable(table.getName());
                    break;
            }
        });
    }

    @Override
    public void createJsonTable(String tableName) {
        this.createTable(tableName, new DataComponent("json", ComponentType.LONGTEXT));
    }

    @Override
    public void createWithJson(String uniqueValue, String table) {
        this.create(uniqueValue, table);
    }

    @Override
    public <T> T getWithJson(String uniqueValue, Class<T> tClass, String table) throws ExecutionException, InterruptedException {
        Object jsonObject = this.getAsync(uniqueValue, "json", table).get();
        return new Gson().fromJson(jsonObject.toString(), tClass);
    }

    @Override
    public void setWithJson(String uniqueValue, Object toStore, String table) {
        this.set(uniqueValue, "json", new Gson().toJson(toStore), table);
    }

    @Override
    public void createTable(String tableName, DataComponent... dataComponents) {
        try {
            String createTableQuery = String.format("create table if not exists `%s`(`unique_value` varchar(512) primary key, ", tableName);

            for (int index = 0; index < dataComponents.length; index++) {
                createTableQuery = createTableQuery.concat(
                  dataComponents[index].adjuster(index)
                );
            }

            createTableQuery = createTableQuery + ");";
            System.out.println(createTableQuery);
            this.autoClosablePreparedStatement(createTableQuery);
        } catch (Exception var) {
            System.out.println("ERROR! Could not create table " + tableName + " by exception: " + var.getClass().getName());
            var.printStackTrace();
        }
    }

    @Override
    public void create(String uniqueValue, String table) {
        try {
            this.autoClosablePreparedStatement(String.format("insert into %s(`unique_value`) values('%s');", table, uniqueValue));
        } catch (Exception var) {
            System.out.println("ERROR! Could not create a row with unique value " + uniqueValue + " because of exception: " + var.getClass().getSimpleName());
            var.printStackTrace();
        }
    }

    @Override
    public Object get(String uniqueValue, String column, String table) {
        try {
            String getQuery = String.format("select * from %s where unique_value = '%s';", table, uniqueValue);
            System.out.println(getQuery);
            PreparedStatement preparedStatement = this.connection.prepareStatement(getQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            Object object = new Object();

            if (resultSet != null && resultSet.next()) object = resultSet.getObject(column);
            assert resultSet != null;

            resultSet.close();
            preparedStatement.close();

            return object;
        } catch (Exception var) {
            System.out.println("ERROR! Could not fetch a value from the database with the unique value " + uniqueValue);
            var.printStackTrace();
        }
        return null;
    }

    @Override
    public void set(String uniqueValue, String column, Object toStore, String table) {
        try {
            String setQuery = String.format("update %s set `%s` = '%s' where unique_value = '%s';",
              table, column, toStore, uniqueValue
            );

            this.autoClosablePreparedStatement(setQuery);
        } catch (Exception var) {
            System.out.println("ERROR! Could not make a database change with the unique value " + uniqueValue);
            var.printStackTrace();
        }
    }

    @Override
    public void delete(String uniqueValue, String table) {
        try {
            String deleteRowQuery = String.format("delete from %s where unique_value = '%s';",
              table, uniqueValue
            );

            this.autoClosablePreparedStatement(deleteRowQuery);
        } catch (Exception var) {
            System.out.println("ERROR! Could not delete a value from the database whose unique value equals " + uniqueValue);
            var.printStackTrace();
        }
    }

    @Override
    public List<String> eachPrimaryKey(String table) {
        try {
            List<String> uniqueValues = new ArrayList<>();
            String eachQuery = "select unique_value from " + table + ";";

            PreparedStatement preparedStatement = this.connection.prepareStatement(eachQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
                uniqueValues.add(resultSet.getString("unique_value"));

            resultSet.close();
            preparedStatement.close();
            return uniqueValues;
        } catch (Exception var) {
            System.out.println("ERROR! Could not fetch all unique values from table " + table);
            var.printStackTrace();
        }
        return null;
    }

    @Override
    public StoreQueueObject saveLater(String uniqueValue, Object jsonObject, String table) {
        StoreQueueObject storeQueueObject = new StoreQueueObject(uniqueValue, table, jsonObject);
        this.saveQueue.getStoreQueueObjects().add(storeQueueObject);
        return storeQueueObject;
    }

    @Override
    public StoreQueueObject saveLater(String uniqueValue, String column, Object object, String table) {
        StoreQueueObject storeQueueObject = new StoreQueueObject(uniqueValue, table, column, object);
        this.saveQueue.getStoreQueueObjects().add(storeQueueObject);
        return storeQueueObject;
    }

    @Override
    public CompletableFuture<List<String>> eachAsyncPrimaryKey(String table) {
        return CompletableFuture.supplyAsync(() -> this.eachPrimaryKey(table));
    }

    @Override
    public CompletableFuture<Void> createAsyncTable(String tableName, DataComponent... dataComponents) {
        return CompletableFuture.runAsync(() -> this.createTable(tableName, dataComponents));
    }

    @Override
    public CompletableFuture<Void> createAsync(String uniqueValue, String table) {
        return CompletableFuture.runAsync(() -> this.create(uniqueValue, table));
    }

    @Override
    public CompletableFuture<Object> getAsync(String uniqueValue, String column, String table) {
        return CompletableFuture.supplyAsync(() -> this.get(uniqueValue, column, table));
    }

    @Override
    public CompletableFuture<Void> setAsync(String uniqueValue, String column, Object toStore, String table) {
        return CompletableFuture.runAsync(() -> this.set(uniqueValue, column, toStore, table));
    }

    @Override
    public CompletableFuture<Void> createAsyncJsonTable(String tableName) {
        return CompletableFuture.runAsync(() -> this.createJsonTable(tableName));
    }

    @Override
    public CompletableFuture<Void> createAsyncWithJson(String uniqueValue, String table) {
        return CompletableFuture.runAsync(() -> this.createWithJson(uniqueValue, table));
    }

    @Override
    public <T> CompletableFuture<T> getAsyncWithJson(String uniqueValue, Class<T> tClass, String table) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.getWithJson(uniqueValue, tClass, table);
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("THREAD ERROR -> Não foi possível pegar um valor async com json!");
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setAsyncWithJson(String uniqueValue, Object toStore, String table) {
        return CompletableFuture.runAsync(() -> this.setWithJson(uniqueValue, toStore, table));
    }

    private void autoClosablePreparedStatement(String query) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
        preparedStatement.close();
    }
}
