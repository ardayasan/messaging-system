package org.yasanarda.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientCreator {
    private JButton button;

    public ClientCreator() {
        button = new JButton("Create New Client GUI");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientGUI();
            }
        });
    }

    public JButton getButton() {
        return button;
    }
}
