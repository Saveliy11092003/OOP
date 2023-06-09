package ru.nsu.trushkov.lab4.factory.gui;

import ru.nsu.trushkov.lab4.factory.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

public class WindowFactory extends JFrame {
    private static final int DELAY_VARIETY = 10;

    private Factory factory;

    private JLabel totalSoldLabel;
    private JLabel totalGainLabel;
    private JLabel buildStateLabel;

    private JLabel carPriceLabel;
    private JLabel carBodyStoreSizeLabel;
    private JLabel carEngineStoreSizeLabel;
    private JLabel carAccessoryStoreSizeLabel;

    public WindowFactory() {
        super("Factory");
    }

    private interface SliderListener {
        void valueChanged(int newValue);
    }

    public void start() throws IOException {
        factory = new Factory();
        factory.start();

        SwingUtilities.invokeLater(this::init);
    }
    private Box createSlider(Integer startingDelay, String description, SliderListener listener) {
        var panel = new Box(BoxLayout.Y_AXIS);

        int min = 0;
        int max = startingDelay * DELAY_VARIETY;
        var slider = new JSlider(min, max, startingDelay);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(min, new JLabel(Integer.toString(min)));
        labelTable.put(max, new JLabel(Integer.toString(max)));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);

        var label = new JLabel(description + startingDelay);

        slider.addChangeListener(e -> {
            int value = ((JSlider) e.getSource()).getValue();
            label.setText(description + value);
            listener.valueChanged(value);
        });

        panel.add(label);
        panel.add(slider);

        return panel;
    }
    private void init() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        var mainPanel = new JPanel();

        mainPanel.setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();

        setResizable(false);

        getContentPane().add(mainPanel);

        {
            var storageStatePanel = new BorderPanel("Storage state");
            gbc.gridx = 0;
            gbc.gridy = 0;
            mainPanel.add(storageStatePanel, gbc);

            carBodyStoreSizeLabel = new JLabel();
            storageStatePanel.addIn(carBodyStoreSizeLabel);

            carEngineStoreSizeLabel = new JLabel();
            storageStatePanel.addIn(carEngineStoreSizeLabel);

            carAccessoryStoreSizeLabel = new JLabel();
            storageStatePanel.addIn(carAccessoryStoreSizeLabel);
        }

        {
            var factoryStatePanel = new BorderPanel("Factory state");
            gbc.gridx = 1;
            gbc.gridy = 0;
            mainPanel.add(factoryStatePanel, gbc);

            carPriceLabel = new JLabel();
            factoryStatePanel.addIn(carPriceLabel);

            totalSoldLabel = new JLabel();
            factoryStatePanel.addIn(totalSoldLabel);

            totalGainLabel = new JLabel();
            factoryStatePanel.addIn(totalGainLabel);

            buildStateLabel = new JLabel();
            factoryStatePanel.addIn(buildStateLabel);
        }

        {
            var factoryControlPanel = new BorderPanel("Factory controls");
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 1;
            mainPanel.add(factoryControlPanel, gbc);

            factoryControlPanel.addIn(createSlider(factory.getCarBodySupplierDelay(), "Car body delay: ", (int value) -> factory.setCarBodySupplierDelay(value)));


            factoryControlPanel.addIn(createSlider(factory.getCarEngineSupplierDelay(), "Car engine delay: ", (int value) -> factory.setCarEngineSupplierDelay(value)));


            factoryControlPanel.addIn(createSlider(factory.getCarAccessorySupplierDelay(), "Car accessory delay: ", (int value) -> factory.setCarAccessorySupplierDelay(value)));
        }

        var timer = new Timer(50, (ActionEvent e) -> updateInformation());
        timer.start();

        updateInformation();
        pack();
        setVisible(true);
    }

    private void updateInformation() {
        totalSoldLabel.setText("Total sold: " + factory.getTotalSold());
        totalGainLabel.setText("Total gain: " + new DecimalFormat("#0.#").format(factory.getTotalGain()));
        buildStateLabel.setText("Build state: " + (factory.isBuildingPaused() ? "paused" : "running"));
        carPriceLabel.setText("Car price: " + new DecimalFormat("#0.##").format(factory.getDealerCarPrice()));
        carBodyStoreSizeLabel.setText("Car body storage size: " + factory.getCarBodyStoreSize() + " / " + factory.getCarBodyStoreCapacity());
        carEngineStoreSizeLabel.setText("Car engine storage size: " + factory.getCarEngineStoreSize() + " / " + factory.getCarEngineStoreCapacity());
        carAccessoryStoreSizeLabel.setText("Car accessory storage size: " + factory.getCarAccessoryStoreSize() + " / " + factory.getCarAccessoryStoreCapacity());
    }

    @Override
    public void dispose() {
        factory.shutdown();
        System.exit(0);
    }
}
