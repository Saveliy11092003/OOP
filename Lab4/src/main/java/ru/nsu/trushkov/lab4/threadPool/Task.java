package ru.nsu.trushkov.lab4.threadPool;

public interface Task {
    String getName();

    void performWork() throws InterruptedException;
}
