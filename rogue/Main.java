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
        JFrame frame = new JFrame("Caves of Chaos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int currentFloor = 0;
        GameWorld gameWorld = new GameWorld();
        Player player = new Player(1,1);//initiate player
        
        GameMap nextMap = gameWorld.getFloor(currentFloor);
        int[] entrance = gameWorld.getEntrance(currentFloor);
        player.setPosition(entrance[0], entrance[1]);
        List<Enemy> enemies = gameWorld.getEnemies(currentFloor);

        GamePanel gamePanel = new GamePanel(nextMap, player, enemies);
        PlayerStatusPanel statusPanel = new PlayerStatusPanel(player);
        GameLogPanel logPanel = new GameLogPanel();

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

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: dy = -1; break;
                    case KeyEvent.VK_S: dy = 1; break;
                    case KeyEvent.VK_A: dx = -1; break;
                    case KeyEvent.VK_D: dx = 1; break;
                }
                int newX = player.getX() + dx;
                int newY = player.getY() + dy;
                if (newX >= 0 && newX < nextMap.getWidth() &&
                    newY >= 0 && newY < nextMap.getHeight() &&
                    nextMap.getTile(newX, newY).getType() == TileType.FLOOR) {
                    player.move(dx, dy);

                    // --- Εδώ οι εχθροί κάνουν το γύρο τους ---
                    boolean[][] floorMap = new boolean[nextMap.getWidth()][nextMap.getHeight()];
                    for (int x = 0; x < nextMap.getWidth(); x++) {
                        for (int y = 0; y < nextMap.getHeight(); y++) {
                            floorMap[x][y] = nextMap.getTile(x, y).getType() == TileType.FLOOR;
                        }
                    }
                    // Να μην περνάνε πάνω από τον παίκτη (optionally)
                    for (Enemy enemy : enemies) {
                        enemy.takeTurn(player.getX(), player.getY(), floorMap);
                    }

                    // COMBAT CHECK
                    boolean playerAttacked = false;
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy enemy = enemies.get(i);
                        if (enemy.getX() == player.getX() && enemy.getY() == player.getY()) {
                            // Simple battle: both take damage
                            player.takeDamage(3);
                            enemy.takeDamage(5);
                            logPanel.addMessage("Battle with " + enemy.getName() + "! Player -3 HP, Enemy -5 HP.");

                            if (enemy.isDead()) {
                                logPanel.addMessage(enemy.getName() + " defeated! +10 XP");
                                logPanel.addMessage( " floor you are now is" + currentFloor);
                                player.addXp(10); // Ή ό,τι XP θες ανα enemy!
                                enemies.remove(i);
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
                    

                    gamePanel.repaint();
                    statusPanel.updateStatus();
                    logPanel.addMessage("Player moved to (" + player.getX() + ", " + player.getY() + ")");
                }
                gamePanel.requestFocusInWindow();
                
                int[] exit = gameWorld.getExit(currentFloor);
                if (player.getX() == exit[0] && player.getY() == exit[1] && gameWorld.getEnemies(currentFloor).isEmpty()) {
                    if (currentFloor <= GameWorld.NUM_FLOORS - 1) {
                        
                        GameMap nextMap = gameWorld.getFloor(currentFloor);
                        int[] entrance = gameWorld.getEntrance(currentFloor);
                        player.setPosition(entrance[0], entrance[1]);                        
                        // Κάνεις update το GamePanel με το νέο map/enemies
                        // (μπορείς να φτιάξεις μέθοδο setMap/setEnemies στο GamePanel ή να κάνεις recreate το panel)
                        // update UI, log κτλ
                    } else {
                        // Τελικό επίπεδο — GAME WON!
                        logPanel.addMessage("You reached the top of the tower! Congratulations!");
                        JOptionPane.showMessageDialog(frame, "You won the game!", "Victory", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }
                
            }
        });


        
    }
}