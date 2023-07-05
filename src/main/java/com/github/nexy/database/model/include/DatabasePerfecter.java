package com.github.nexy.database.model.include;

import java.util.concurrent.ExecutionException;

public interface DatabasePerfecter {

    void createJsonTable(String tableName);

    void createWithJson(String uniqueValue, String table);

    <T> T getWithJson(String unique, Class<T> tClass, String table) throws ExecutionException, InterruptedException;

    void setWithJson(String uniqueValue, Object toStore, String table);

}
