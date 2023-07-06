package com.github.nexy.database.example.storage;

import com.github.nexy.database.annotations.StoreVoid;
import com.github.nexy.database.components.DataComponent;
import com.github.nexy.database.components.Table;
import com.github.nexy.database.constants.ComponentType;
import com.github.nexy.database.model.DatabaseModel;

import java.util.Arrays;

public class StoreClass extends DatabaseModel {

    @StoreVoid
    public void initAnyTable() {
        this.createTable(new Table(
          "master_table",
          new DataComponent("column", ComponentType.TEXT),
          new DataComponent("column2", ComponentType.TEXT)
        ));
        // do your thing here
    }

    @StoreVoid
    public void initOtherTable() {
        this.createJsonTable(new Table("helloA"));
        // do your thing here
    }

}
