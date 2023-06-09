package ru.nsu.trushkov.lab4.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.nsu.trushkov.lab4.factory.controllers.BuildControl;
import ru.nsu.trushkov.lab4.factory.controllers.DealControl;
import ru.nsu.trushkov.lab4.factory.controllers.PriceControl;

import ru.nsu.trushkov.lab4.factory.parts.Accessory;
import ru.nsu.trushkov.lab4.factory.parts.Body;
import ru.nsu.trushkov.lab4.factory.parts.Car;
import ru.nsu.trushkov.lab4.factory.parts.Engine;

import ru.nsu.trushkov.lab4.factory.store.Dealer;
import ru.nsu.trushkov.lab4.factory.store.Storage;

import ru.nsu.trushkov.lab4.factory.suppliers.SupplierAccessory;
import ru.nsu.trushkov.lab4.factory.suppliers.SupplierBody;
import ru.nsu.trushkov.lab4.factory.suppliers.SupplierEngine;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class Factory {
    private final static Logger logger = LogManager.getLogger(Factory.class);

    private final static String propertiesFilename = "/factory.properties";

    private final SupplierBody carBodySupplier;
    private final SupplierEngine carEngineSupplier;
    private final SupplierAccessory carAccessorySupplier;

    private final Storage<Body> carBodyStore;
    private final Storage<Engine> carEngineStore;
    private final Storage<Accessory> carAccessoryStore;
    private final Storage<Car> carStore;

    private final ThreadProduction<Body> carBodyProductionThread;
    private final ThreadProduction<Engine> carEngineProductionThread;
    private final ThreadProduction<Accessory> carAccessoryProductionThread;

    private final BuildControl carBuildController;
    private final DealControl carDealController;
    private final PriceControl carPriceController;

    private final Dealer dealer;

    public Factory() throws IOException {
        var properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(propertiesFilename));
        } catch (IOException e) {
            logger.error("Can't load " + propertiesFilename);
            throw e;
        }

        carStore = new Storage<>(Integer.parseInt((String) properties.get("CAR.STORE_SIZE")));

        carBodySupplier = new SupplierBody(Integer.parseInt((String) properties.get("CAR_BODY.DELAY")));
        carBodyStore = new Storage<>(Integer.parseInt((String) properties.get("CAR_BODY.STORE_SIZE")));
        carBodyProductionThread = new ThreadProduction<>(carBodySupplier, carBodyStore);

        carEngineSupplier = new SupplierEngine(Integer.parseInt((String) properties.get("CAR_ENGINE.DELAY")));
        carEngineStore = new Storage<>(Integer.parseInt((String) properties.get("CAR_ENGINE.STORE_SIZE")));
        carEngineProductionThread = new ThreadProduction<>(carEngineSupplier, carEngineStore);

        carAccessorySupplier = new SupplierAccessory(Integer.parseInt((String) properties.get("CAR_ACCESSORY.DELAY")));
        carAccessoryStore = new Storage<>(Integer.parseInt((String) properties.get("CAR_ACCESSORY.STORE_SIZE")));
        carAccessoryProductionThread = new ThreadProduction<>(carAccessorySupplier, carAccessoryStore);

        dealer = new Dealer();
        int numOfDealers = Integer.parseInt((String) properties.get("DEALER.NUM"));

        int numOfBuilders = Integer.parseInt((String) properties.get("WORKER.NUM"));

        logger.info("Stores, suppliers and threads created");

        carBuildController = new BuildControl(carBodyStore, carEngineStore, carAccessoryStore, carStore, numOfBuilders);
        carPriceController = new PriceControl(dealer, new PriceControl.FactoryProductionControlAdapter() {
            @Override
            public boolean isPaused() {
                return carBuildController.isPaused();
            }

            @Override
            public void pauseProduction() {
                carBuildController.pauseProduction();
            }

            @Override
            public void continueProduction() {
                carBuildController.continueProduction();
            }
        });
        carDealController = new DealControl(carStore, dealer, numOfDealers);

        logger.info("Controllers created");
    }

    public void start() {
        carBodyProductionThread.start();
        carEngineProductionThread.start();
        carAccessoryProductionThread.start();

        logger.info("Production threads started");

        carBuildController.start();
        carDealController.start();
        carPriceController.start();

        logger.info("Controllers started");
    }
    public void shutdown() {
        logger.info("Shutdown issued");

        carBodyProductionThread.shutdown();
        carEngineProductionThread.shutdown();
        carAccessoryProductionThread.shutdown();

        carBuildController.shutdown();
        carDealController.shutdown();
    }

    public int getCarBodyStoreSize() {
        return carBodyStore.getSize();
    }

    public int getCarBodyStoreCapacity() {
        return carBodyStore.getCapacity();
    }

    public int getCarBodySupplierDelay() {
        return carBodySupplier.getDelay();
    }

    public void setCarBodySupplierDelay(int delay) {
        carBodySupplier.setDelay(delay);
    }

    public int getCarEngineStoreSize() {
        return carEngineStore.getSize();
    }

    public int getCarEngineStoreCapacity() {
        return carEngineStore.getCapacity();
    }

    public int getCarEngineSupplierDelay() {
        return carEngineSupplier.getDelay();
    }

    public void setCarEngineSupplierDelay(int delay) {
        carEngineSupplier.setDelay(delay);
    }

    public int getCarAccessoryStoreSize() {
        return carAccessoryStore.getSize();
    }

    public int getCarAccessoryStoreCapacity() {
        return carAccessoryStore.getCapacity();
    }

    public int getCarAccessorySupplierDelay() {
        return carAccessorySupplier.getDelay();
    }

    public void setCarAccessorySupplierDelay(int delay) {
        carAccessorySupplier.setDelay(delay);
    }

    public BigDecimal getDealerCarPrice() {
        return dealer.getCarPrice();
    }

    public int getTotalSold() {
        return carDealController.getTotalSold();
    }

    public BigDecimal getTotalGain() {
        return carDealController.getTotalGain();
    }


    public boolean isBuildingPaused() {
        return carBuildController.isPaused();
    }
}
