package ru.nsu.trushkov.lab4.factory.suppliers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.parts.Body;
public class SupplierBody extends SupplierDelay<Body> {
    private static final Logger logger = LogManager.getLogger(SupplierBody.class);

    public SupplierBody(int delay) {
        super(delay);
    }

    public Body get() {
        try {
            Thread.sleep(getDelay());
        } catch (InterruptedException e) {
            logger.info("Caught InterruptedException, returning null");
            return null;
        }
        return new Body();
    }
}
