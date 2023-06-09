package ru.nsu.trushkov.lab4.factory.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.lockingQueue.QueueLocking;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Storage<T> implements Supplier<T>, Consumer<T> {
    private static final Logger logger = LogManager.getLogger(Storage.class);
    private final int storeSize;
    private final QueueLocking<T> store;

    public Storage(int storeSize) {
        this.storeSize = storeSize;
        store = new QueueLocking<>(storeSize);
    }

    public int getSize() {
        return store.size();
    }

    public int getCapacity() {
        return storeSize;
    }

    @Override
    public T get() {
        try {
            return store.take();
        } catch (InterruptedException e) {
            logger.info("Caught InterruptedException, returning null");
            return null;
        }
    }

    @Override
    public void accept(T part) {
        try {
            store.put(part);
        } catch (InterruptedException ignored) {
            logger.info("Interrupted exception ignored");
        }
    }
}
