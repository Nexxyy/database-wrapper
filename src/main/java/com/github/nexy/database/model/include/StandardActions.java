package com.github.nexy.database.model.include;

import com.github.nexy.database.components.DataComponent;
import com.github.nexy.database.components.StoreQueueObject;

import java.util.List;

public interface StandardActions {

    void createTable(String tableName, DataComponent... dataComponents);

    void create(String uniqueValue, String table);

    Object get(String uniqueValue, String column, String table);

    void set(String uniqueValue, String column, Object toStore, String table);

    void delete(String uniqueValue, String table);

    List<String> eachPrimaryKey(String table);

    StoreQueueObject saveLater(String uniqueValue, Object jsonObject, String table);

    StoreQueueObject saveLater(String uniqueValue, String column, Object object, String table);

}
