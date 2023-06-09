package ru.nsu.trushkov.lab4.factory.suppliers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.parts.Accessory;

public class SupplierAccessory extends SupplierDelay<Accessory> {
    private static final Logger logger = LogManager.getLogger(SupplierAccessory.class);

    public SupplierAccessory(int delay) {
        super(delay);
    }

    public Accessory get() {
        try {
            Thread.sleep(getDelay());
        } catch (InterruptedException e) {
            logger.info("Caught InterruptedException, returning null");
            return null;
        }
        return new Accessory();
    }
}
