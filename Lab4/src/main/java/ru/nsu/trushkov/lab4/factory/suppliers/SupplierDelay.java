package ru.nsu.trushkov.lab4.factory.suppliers;

import java.util.function.Supplier;
public abstract class SupplierDelay<T> implements Supplier<T> {
    private volatile int delay;

    public void setDelay(int newDelay) {
        delay = newDelay;
    }

    public int getDelay() {
        return delay;
    }

    public SupplierDelay(int delay) {
        this.delay = delay;
    }

}
