package ru.nsu.trushkov.lab4.factory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadProduction<T> extends Thread {
    private final Supplier<T> supplier;
    private final Consumer<T> store;
    private volatile boolean isRunning = true;

    public ThreadProduction(Supplier<T> supplier, Consumer<T> store) {
        this.store = store;
        this.supplier = supplier;
    }

    public void shutdown() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            var res = supplier.get();
            if (res == null) {
                continue;
            }
            store.accept(res);
        }
    }
}
