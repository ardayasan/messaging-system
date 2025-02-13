package org.yasanarda.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientCreatorImpl implements ClientCreator {
    private JButton button;
    public ClientCreatorImpl() {
        button = new JButton("Create New Client GUI");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientGUI();
            }
        });
    }
    @Override
    public JButton getButton() {
        return button;
    }
}
