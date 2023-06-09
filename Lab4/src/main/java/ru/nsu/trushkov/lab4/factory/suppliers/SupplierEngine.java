package ru.nsu.trushkov.lab4.factory.suppliers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.parts.Engine;

public class SupplierEngine extends SupplierDelay<Engine> {
    private static final Logger logger = LogManager.getLogger(SupplierEngine.class);

    public SupplierEngine(int delay) {
        super(delay);
    }

    public Engine get() {
        try {
            Thread.sleep(getDelay());
        } catch (InterruptedException e) {
            logger.info("Caught InterruptedException, returning null");
            return null;
        }
        return new Engine();
    }
}
