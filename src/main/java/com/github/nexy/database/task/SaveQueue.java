package com.github.nexy.database.task;

import com.github.nexy.database.StorageBuilder;
import com.github.nexy.database.components.Database;
import com.github.nexy.database.components.StoreQueueObject;
import lombok.Getter;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class SaveQueue {

    private final Runnable queueService;
    private final Queue<StoreQueueObject> storeQueueObjects = new ConcurrentLinkedQueue<>();

    public SaveQueue(int seconds, StorageBuilder storageBuilder) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        this.queueService = () -> {
            Database singletonDatabase = storageBuilder.getSingletonDatabase();

            if (storeQueueObjects.isEmpty()) return;
            if (singletonDatabase == null) return;

            Iterator<StoreQueueObject> currentQueue = storeQueueObjects.iterator();

            while (currentQueue.hasNext()) {
                StoreQueueObject storeQueueObject = currentQueue.next();

                if (storeQueueObject.isJson()) {
                    singletonDatabase.setWithJson(
                      storeQueueObject.getUniqueValue(),
                      storeQueueObject.getToStore(),
                      storeQueueObject.getTable()
                    );
                    storeQueueObject.setSaved(true);
                    currentQueue.remove();
                    return;
                }

                singletonDatabase.set(
                  storeQueueObject.getUniqueValue(),
                  storeQueueObject.getColumn(),
                  storeQueueObject.getToStore(),
                  storeQueueObject.getTable()
                );
                storeQueueObject.setSaved(true);
                currentQueue.remove();
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(this.queueService, 0, seconds, TimeUnit.SECONDS);
    }
}
