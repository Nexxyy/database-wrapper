package com.github.nexy.database.components;

import com.github.nexy.database.constants.TableType;
import lombok.Getter;

import java.util.List;

@Getter
public class Table {

    private final String name;
    private final TableType tableType;
    private DataComponent[] tableComponents;

    public Table(String name, DataComponent... tableComponents) {
        this.name = name;
        this.tableType = TableType.DEFAULT;
        this.tableComponents = tableComponents;
    }

    public Table(String name) {
        this.name = name;
        this.tableType = TableType.JSON;
    }

}
