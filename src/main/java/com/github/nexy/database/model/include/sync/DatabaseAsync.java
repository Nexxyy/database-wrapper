package com.github.nexy.database.model.include.sync;

import com.github.nexy.database.components.DataComponent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DatabaseAsync {

    CompletableFuture<List<String>> eachAsyncPrimaryKey(String table);

    CompletableFuture<Void> createAsyncTable(String tableName, DataComponent... dataComponents);

    CompletableFuture<Void> createAsync(String uniqueValue, String table);

    CompletableFuture<Object> getAsync(String uniqueValue, String column, String table);

    CompletableFuture<Void> setAsync(String uniqueValue, String column, Object toStore, String table);

    // Perfecter Extension

    CompletableFuture<Void> createAsyncJsonTable(String tableName);

    CompletableFuture<Void> createAsyncWithJson(String uniqueValue, String table);

    <T> CompletableFuture<T> getAsyncWithJson(String uniqueValue, Class<T> tClass, String table);

    CompletableFuture<Void> setAsyncWithJson(String uniqueValue, Object toStore, String table);

}
