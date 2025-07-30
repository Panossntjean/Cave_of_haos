package rogue.ui;

import rogue.player.*;
import rogue.map.*;

import javax.swing.*;
import java.awt.*;

public class PlayerStatusPanel extends JPanel {
    private Player player;
    private GameWorld gameWorld;
    private JLabel statusLabel;

    public PlayerStatusPanel(Player player, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.player = player;
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(200, 200));
        statusLabel = new JLabel();
        add(statusLabel);
        updateStatus();
    }

    public void updateStatus() {
        statusLabel.setText(
            "<html>Position: (" + player.getX() + ", " + player.getY() + ")<br>" +
            "HP: " + player.getHp() + " / " + player.getMaxHp() +"<br>" +
            "Level: " + player.getLevel() + "<br>" +
            "XP: " + player.getXp() + "<br>" +
            "floor:"+ gameWorld.getCurrentFloor() + "<br>"+"</html>"
        );
    }
}