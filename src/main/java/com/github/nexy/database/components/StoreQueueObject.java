package com.github.nexy.database.components;

import lombok.Getter;
import lombok.Setter;

@Getter
public class StoreQueueObject {

    private final String uniqueValue;
    private final String table;
    private final Object toStore;
    private String column;
    private boolean json;

    @Setter
    private boolean saved = false;

    public StoreQueueObject(String uniqueValue, String table, Object toStore) {
        this.uniqueValue = uniqueValue;
        this.table = table;
        this.toStore = toStore;
        this.json = true;
    }

    public StoreQueueObject(String uniqueValue, String table, String column, Object toStore) {
        this.uniqueValue = uniqueValue;
        this.table = table;
        this.column = column;
        this.json = false;
        this.toStore = toStore;
    }

}
