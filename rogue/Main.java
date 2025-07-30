package rogue;

import rogue.map.*;
import rogue.player.*;
import rogue.ui.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import rogue.enemies.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Welcome fellow wonderer to the Caves of Chaos");
        //int currentFloor = 0;
        GameWorld gameWorld = new GameWorld();
        Player player = gameWorld.getPlayer();

        GamePanel gamePanel = new GamePanel(gameWorld);
        GameLogPanel logPanel = new GameLogPanel();
        PlayerStatusPanel statusPanel = new PlayerStatusPanel(player, gameWorld);

        JFrame frame = new JFrame("Caves of Chaos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(gamePanel, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statusPanel, BorderLayout.NORTH);
        rightPanel.add(logPanel, BorderLayout.CENTER);
        rootPanel.add(rightPanel, BorderLayout.EAST);

        frame.setContentPane(rootPanel);
        frame.pack();
        frame.setVisible(true);
        

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gameWorld.getCurrentMap().updateVisibility(player.getX(), player.getY(), 6);
        gamePanel.repaint();
        

        //player = new Player(1,1);//initiate player
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                Player player = gameWorld.getPlayer();
                GameMap currentMap = gameWorld.getCurrentMap();
                List<Enemy> enemies = gameWorld.getCurrentEnemies();                
                //int currentFloor  = gameWorld.getCurrentFloor();                
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: dy = -1; break;
                    case KeyEvent.VK_S: dy = 1; break;
                    case KeyEvent.VK_A: dx = -1; break;
                    case KeyEvent.VK_D: dx = 1; break;
                    case KeyEvent.VK_SPACE: dx = 0; break;
                }
                int newX = player.getX() + dx;
                int newY = player.getY() + dy;
                if (newX >= 0 && newX < currentMap.getWidth() &&
                    newY >= 0 && newY < currentMap.getHeight() &&
                    currentMap.getTile(newX, newY).getType() == TileType.FLOOR) {

                    player.move(dx, dy);
                    currentMap.updateVisibility(player.getX(), player.getY(), 6);

                    // --- Enemies take their turn---
                    boolean[][] floorMap = new boolean[currentMap.getWidth()][currentMap.getHeight()];
                    for (int x = 0; x < currentMap.getWidth(); x++) {
                        for (int y = 0; y < currentMap.getHeight(); y++) {
                            floorMap[x][y] = currentMap.getTile(x, y).getType() == TileType.FLOOR;
                        }
                    }
                    // enemies don't step on player (optional)
                    for (Enemy enemy : enemies) {
                        enemy.takeTurn(player.getX(), player.getY(), floorMap);
                    }

                    // COMBAT CHECK
                    boolean playerAttacked = false;
                    
                        for (int i = 0; i < enemies.size(); i++) {
                            Enemy enemy = enemies.get(i);
                            if (enemy.getX() == player.getX() && enemy.getY() == player.getY()) {
                                // Simple battle: enemies hit always , player hit with action space 
                                //----------------------------------Will fix this later
                                player.takeDamage(enemy.getDmg());
                                // HIT WITH SPACE BAR 
                                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                                    enemy.takeDamage(player.getDmg());
                                    logPanel.addMessage("Battle with " + enemy.getName() + "! Player -"+ enemy.getDmg() +"HP, Enemy -"+ player.getDmg() +" HP.");
                                    }
                                if (enemy.isDead()) {
                                    
                                    int enemy_xp;
                                    enemy_xp = enemy.getXp();
                                    logPanel.addMessage(enemy.getName() + " defeated!" + enemy_xp +"XP");
                                    player.addXp(enemy_xp); // Ή ό,τι XP θες ανα enemy!
                                    gameWorld.getCurrentEnemies().remove(i);
                                    i--; // VERY important: avoid skipping next enemy
                                }
                                playerAttacked = true;
                            }
                        }
                    
                    statusPanel.updateStatus();

                    if (player.isDead()) {
                        logPanel.addMessage("Game Over! The player died!");
                        JOptionPane.showMessageDialog(frame, "Game Over! You died!", "Game Over", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                    
                    //currentMap.updateVisibility(player.getX(), player.getY(), 6);
                    gamePanel.repaint();
                    statusPanel.updateStatus();                    
                    //logPanel.addMessage("Player moved to (" + player.getX() + ", " + player.getY() + ")");
                }
                gamePanel.requestFocusInWindow();
                
                if (gameWorld.isPlayerOnExit()){                    
                    boolean floorUp = gameWorld.moveToNextFloor();                    
                    if(floorUp){
                        //gameWorld.moveToNextFloor();                        
                        gamePanel.refresh();
                        statusPanel.updateStatus();                        
                        logPanel.addMessage("You advanced to floor -" + gameWorld.getCurrentFloor());
                    } else {
                        logPanel.addMessage("You reached the top of the tower! Congratulations!");
                        JOptionPane.showMessageDialog(frame, "You won the game!", "Victory", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }

                if (gameWorld.isPlayerOnEntrance()) {
                    boolean floorDown = gameWorld.moveToPreviousFloor();
                    if (floorDown) {
                        //gameWorld.moveToPreviousFloor();
                        gamePanel.refresh();
                        statusPanel.updateStatus();
                        logPanel.addMessage("You returned to floor " + gameWorld.getCurrentFloor());
                    }
                }
                
                // --- Focus always on gamePanel                
                gamePanel.requestFocusInWindow();
                
            }
            
        });
        logPanel.addMessage("Game started. Good luck!");
    }
}