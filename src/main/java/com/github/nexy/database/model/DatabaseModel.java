package com.github.nexy.database.model;

import com.github.nexy.database.components.Table;
import javafx.scene.control.Tab;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DatabaseModel {

    private final List<Table> tables = new ArrayList<>();

    public void createTable(Table table) {
        tables.add(table);
    }

    public void createJsonTable(Table table) {
        tables.add(table);
    }

}
