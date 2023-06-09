package ru.nsu.trushkov.lab4.factory.gui;

import javax.swing.border.Border;
import java.awt.*;
import javax.swing.*;

public class BorderPanel extends JPanel {

    private final JPanel innerPanel;
    private final Border border;

    public void addIn(Component comp) {
        innerPanel.add(comp);
    }

    public BorderPanel(String title) {
        super();

        border = BorderFactory.createTitledBorder(title);

        setBorder(border);

        innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        add(innerPanel);
    }
}
