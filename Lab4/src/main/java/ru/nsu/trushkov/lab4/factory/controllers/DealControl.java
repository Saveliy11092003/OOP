package ru.nsu.trushkov.lab4.factory.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.parts.Car;
import ru.nsu.trushkov.lab4.factory.store.Dealer;
import ru.nsu.trushkov.lab4.threadPool.Task;
import ru.nsu.trushkov.lab4.threadPool.ThreadPool;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
public class DealControl extends Thread {
    private static final Logger logger = LogManager.getLogger(DealControl.class);

    private static final int SIZE_QUEUE = 1000;

    private final Object monitor = new Object();

    private final Supplier<Car> outputStore;
    private final ThreadPool threadPool;
    private final AtomicInteger totalSold = new AtomicInteger(0);
    private BigDecimal totalGain = BigDecimal.ZERO;

    private final Dealer dealer;
    private volatile boolean isRunning = true;

    public DealControl(Supplier<Car> outputStore, Dealer dealer, int numOfThreads) {
        this.dealer = dealer;
        this.outputStore = outputStore;

        threadPool = new ThreadPool(numOfThreads, SIZE_QUEUE);
    }

    private void addMoney(BigDecimal gain) {
        synchronized (monitor) {
            totalGain = totalGain.add(gain);
        }
    }

    public void shutdown() {
        isRunning = false;
        threadPool.shutdown();
    }

    public int getTotalSold() {
        return totalSold.intValue();
    }

    public BigDecimal getTotalGain() {
        synchronized (monitor) {
            return totalGain;
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            var car = outputStore.get();

            var task = new Task() {
                @Override
                public String getName() {
                    return "Sell a car";
                }

                @Override
                public void performWork() {
                    totalSold.incrementAndGet();
                    addMoney(dealer.sell(car));
                    logger.info("Sold car to dealer " + Thread.currentThread().getName() +  ": " + car);
                }
            };
            try {
                threadPool.addTask(task);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }
}
