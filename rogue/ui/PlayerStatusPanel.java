package rogue.ui;

import rogue.player.*;
import rogue.map.*;
import rogue.items.Weapon; 

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
        setPreferredSize(new Dimension(200, 400));
        statusLabel = new JLabel();
        add(statusLabel);
        updateStatus();
    }

    public void updateStatus() {
        int floor = (gameWorld.getCurrentFloor() <= 0) 
                ? gameWorld.getCurrentFloor() 
                : -gameWorld.getCurrentFloor();
        Weapon w = player.getEquippedWeapon();
        String weaponName = (w != null) ? w.getName() : "None";
                
        statusLabel.setText(
            "<html>Name: " + player.getName() + "<br> " +
            "Class: " + player.getPlayerClass() + "<br> " +
            "Weapon: " + weaponName + "<br>" +
            "Damage: " + player.getDmg() + "<br>" + 
            "HP: " + player.getHp() + " / " + player.getMaxHp() +"<br>" +
            "MP: " + player.getMp() + " / " + player.getMaxMp() +"<br>" +
            "Level: " + player.getLevel() + "<br>" +
            "XP: " + player.getXp() + "<br>" +
            "floor: "+ floor + "<br>"+
            player.getPotionSummary() + "<br>" + "</html>"
        );
    }
}