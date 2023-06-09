package ru.nsu.trushkov.lab4.factory.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.parts.Accessory;
import ru.nsu.trushkov.lab4.factory.parts.Body;
import ru.nsu.trushkov.lab4.factory.parts.Car;
import ru.nsu.trushkov.lab4.factory.parts.Engine;
import ru.nsu.trushkov.lab4.threadPool.Task;
import ru.nsu.trushkov.lab4.threadPool.ThreadPool;

import java.util.function.Consumer;
import java.util.function.Supplier;
public class BuildControl extends Thread {
    private static final Logger logger = LogManager.getLogger(BuildControl.class);

    private static final int SIZE_QUEUE = 1000;

    private final Supplier<Body> carBodyStore;
    private final Supplier<Engine> carEngineStore;
    private final Supplier<Accessory> carAccessoryStore;
    private final Consumer<Car> outputStore;
    private final ThreadPool threadPool;
    private final Object ObjectPause = new Object();
    private volatile boolean isRun = true;
    private volatile boolean pause = false;

    public BuildControl(Supplier<Body> carBodyStore, Supplier<Engine> carEngineStore,
                        Supplier<Accessory> carAccessoryStore, Consumer<Car> outputStore, int numOfBuilders) {
        this.carBodyStore = carBodyStore;
        this.carEngineStore = carEngineStore;
        this.carAccessoryStore = carAccessoryStore;
        this.outputStore = outputStore;

        threadPool = new ThreadPool(numOfBuilders, SIZE_QUEUE);
    }

    public boolean isPaused() {
        return pause;
    }


    public void shutdown() {
        isRun = false;
        threadPool.shutdown();
    }

    public void continueProduction() {
        if (!pause) throw new RuntimeException("double continue detected");
        pause = false;
        synchronized (ObjectPause) {
            ObjectPause.notifyAll();
        }
    }

    public void pauseProduction() {
        if (pause) throw new RuntimeException("double pause detected");
        pause = true;
    }


    @Override
    public void run() {
        while (isRun) {
            var body = carBodyStore.get();
            var engine = carEngineStore.get();
            var accessory = carAccessoryStore.get();

            var task = new Task() {
                @Override
                public String getName() {
                    return "Build a car";
                }

                @Override
                public void performWork() {
                    var car = new Car(body, engine, accessory);
                    outputStore.accept(car);
                }
            };

            try {
                threadPool.addTask(task);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            if (pause) {
                synchronized (ObjectPause) {
                    try {
                        do {
                            ObjectPause.wait();
                        } while (pause);
                    } catch (InterruptedException ignored) {
                        logger.info("Interrupted exception ignored");
                    }
                }
            }
        }
    }
}
