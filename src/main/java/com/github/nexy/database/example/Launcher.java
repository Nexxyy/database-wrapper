package com.github.nexy.database.example;

import com.github.nexy.database.StorageBuilder;
import com.github.nexy.database.components.Database;
import com.github.nexy.database.constants.DatabaseType;
import com.github.nexy.database.example.component.PlayerAccount;
import com.github.nexy.database.example.storage.StoreClass;

import java.io.File;

public class Launcher {

    public static void main(String[] args) {
        Database database = new StorageBuilder(DatabaseType.SQLITE)
          .setConnectionData(new File(System.getProperty("user.dir")))
          .useSingleThreadQueue(10)
          .selectDatabaseModel(new StoreClass())
          .build();

        database.createAsyncWithJson("AAAAAAAAAA", "helloA").thenAccept((voidAction) -> {
            database.saveLater("AAAAAAAAAA", new PlayerAccount("DevNexy", 1000), "helloA");
        });
    }

}
