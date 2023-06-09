package ru.nsu.trushkov.lab4.factory.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.trushkov.lab4.factory.store.Dealer;

import java.math.BigDecimal;

public class PriceControl extends Thread {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final static int OK_PRICE = 500;

    private final Dealer dealer;
    private final FactoryProductionControlAdapter adapter;
    private static final int ONE_SEC = 1000;

    public PriceControl(Dealer dealer, FactoryProductionControlAdapter adapter) {
        this.dealer = dealer;
        this.adapter = adapter;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            var carPrice = dealer.getCarPrice();
            if (isCarPriceOk(carPrice)) {
                logger.info(carPrice);
                if (adapter.isPaused()) {
                    logger.info("Car price is ok: " + carPrice + "; starting production back...");
                    adapter.continueProduction();
                }
            } else if (!adapter.isPaused()) {
                logger.info("Car price is NOT ok: " + carPrice + "; stopping production...");
                adapter.pauseProduction();
            }

            try {
                Thread.sleep(ONE_SEC);
            } catch (InterruptedException e) {
                logger.warn(e);
                break;
            }
        }
    }

    private boolean isCarPriceOk(BigDecimal carPrice) {
        if ((carPrice.intValue() <= OK_PRICE +100) && (carPrice.intValue() >= OK_PRICE -100)) {
            return true;
        }
        return false;
    }

    public interface FactoryProductionControlAdapter {
        boolean isPaused();

        void pauseProduction();

        void continueProduction();
    }
}
