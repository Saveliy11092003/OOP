package ru.nsu.trushkov.lab4.factory.gui;

import java.io.IOException;

public class FactoryGUI {
    public static void start() throws IOException {
        WindowFactory window = new WindowFactory();
        window.start();
    }
}
