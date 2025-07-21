package rogue.ui;

import rogue.player.*;

import javax.swing.*;
import java.awt.*;

public class PlayerStatusPanel extends JPanel {
    private Player player;
    private JLabel statusLabel;

    public PlayerStatusPanel(Player player) {
        this.player = player;
        setLayout(new GridLayout(3, 1));
        setPreferredSize(new Dimension(200, 60));
        statusLabel = new JLabel();
        add(statusLabel);
        updateStatus();
    }

    public void updateStatus() {
        statusLabel.setText(
            "<html>Position: (" + player.getX() + ", " + player.getY() + ")<br>" +
            "HP: " + player.getHp() + " / " + player.getMaxHp() + "</html>"
            "Level: " + player.getLevel() + "<br>" +
            "XP: " + player.getXp() + "</html>"
        );
    }
}