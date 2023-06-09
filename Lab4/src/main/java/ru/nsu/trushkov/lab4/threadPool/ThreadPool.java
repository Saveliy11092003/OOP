package ru.nsu.trushkov.lab4.threadPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.lockingQueue.QueueLocking;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class ThreadPool {
    private final static Logger logger = LogManager.getLogger();

    private final BlockingQueue<Task> queue;
    private final Set<TaskWorker> workers = new HashSet<>();
    private volatile boolean isRunning = true;

    public ThreadPool(int threadCount, int queueSize) {
        queue = new ArrayBlockingQueue<>(queueSize);
        for (int i = 0; i < threadCount; i++) {
            var tw = new TaskWorker();
            tw.setName(Integer.toString(i));
            tw.start();
            workers.add(tw);
        }
    }

    public void addTask(Task command) throws InterruptedException {
        if (!isRunning) {
            throw new RejectedExecutionException();
        }
        try {
            queue.put(command);
        } catch (InterruptedException e) {
            logger.error(e);
            throw e;
        }
    }

    public void shutdown() {
        isRunning = false;
        interruptAll();
        for (var t : workers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("double interrupt detected");
            }
        }
    }

    public void interruptAll() {
        if (isRunning) throw new RuntimeException("can't join interrupt threads");
        for (var t : workers) {
            t.interrupt();
        }
    }

    private final class TaskWorker extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    var task = queue.take();
                    logger.info("Executing task '" + task.getName() + "'");
                    task.performWork();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
