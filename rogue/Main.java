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

        GameMap gameMap = new GameMap();

        int startX = gameMap.getWidth() / 2;
        int startY = gameMap.getHeight() / 2;
        while (gameMap.getTile(startX, startY).getType() != TileType.FLOOR) {
            startX++;
            if (startX >= gameMap.getWidth()) {
                startX = 0;
                startY++;
            }
            if (startY >= gameMap.getHeight()) break;
        }
        Player player = new Player(startX, startY);

        // --- Δημιουργία εχθρών ---
        List<Enemy> enemies = new ArrayList<>();
        Random rand = new Random();
        int numEnemies = 3;
        for (int i = 0; i < numEnemies; i++) {
            int ex, ey;
            boolean conflict;
            do {
                ex = rand.nextInt(gameMap.getWidth());
                ey = rand.nextInt(gameMap.getHeight());
                final int fex = ex, fey = ey;
                conflict = enemies.stream().anyMatch(e -> e.getX() == fex && e.getY() == fey);
            } while (
                gameMap.getTile(ex, ey).getType() != TileType.FLOOR ||
                (ex == player.getX() && ey == player.getY()) ||
                conflict
            );
            enemies.add(new ShadowSoldier(ex, ey));
        }

        GamePanel gamePanel = new GamePanel(gameMap, player, enemies);
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

        gamePanel.addKeyListener(new KeyAdapter()) {
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
                if (newX >= 0 && newX < gameMap.getWidth() &&
                    newY >= 0 && newY < gameMap.getHeight() &&
                    gameMap.getTile(newX, newY).getType() == TileType.FLOOR) {
                    player.move(dx, dy);

                    // --- Εδώ οι εχθροί κάνουν το γύρο τους ---
                    boolean[][] floorMap = new boolean[gameMap.getWidth()][gameMap.getHeight()];
                    for (int x = 0; x < gameMap.getWidth(); x++) {
                        for (int y = 0; y < gameMap.getHeight(); y++) {
                            floorMap[x][y] = gameMap.getTile(x, y).getType() == TileType.FLOOR;
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
                                logPanel.addMessage(enemy.getName() + " defeated!");
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
                    if (enemy.isDead()) {
                        logPanel.addMessage(enemy.getName() + " defeated! +10 XP");
                        player.addXp(10); // Ή ό,τι XP θες ανα enemy!
                        enemies.remove(i);
                        i--;
                    }


                    gamePanel.repaint();
                    statusPanel.updateStatus();
                    logPanel.addMessage("Player moved to (" + player.getX() + ", " + player.getY() + ")");
                }
                gamePanel.requestFocusInWindow();
            }
        });

        if (enemies.isEmpty()) {
            logPanel.addMessage("You cleared this floor! Moving to next...");

            // Δημιουργία νέου map
            GameMap newMap = new GameMap();

            // Βρες νέο spawn για τον παίκτη
            int nx = newMap.getWidth() / 2;
            int ny = newMap.getHeight() / 2;
            while (newMap.getTile(nx, ny).getType() != TileType.FLOOR) {
                nx++;
                if (nx >= newMap.getWidth()) {
                    nx = 0;
                    ny++;
                }
                if (ny >= newMap.getHeight()) break;
            }
            player.setPosition(nx, ny);

            // Νέοι εχθροί
            List<Enemy> newEnemies = new ArrayList<>();
            Random rand = new Random();
            int numEnemies = 3 + player.getLevel(); // Περισσότεροι σε κάθε floor!
            for (int i = 0; i < numEnemies; i++) {
                int ex, ey;
                boolean conflict;
                do {
                    ex = rand.nextInt(newMap.getWidth());
                    ey = rand.nextInt(newMap.getHeight());
                    final int fex = ex, fey = ey;
                    conflict = newEnemies.stream().anyMatch(e -> e.getX() == fex && e.getY() == fey);
                } while (
                    newMap.getTile(ex, ey).getType() != TileType.FLOOR ||
                    (ex == player.getX() && ey == player.getY()) ||
                    conflict
                );
                newEnemies.add(new ShadowSoldier(ex, ey));
            }

            // Κάνε update όλα τα references!
            // (προσοχή: θα πρέπει το GamePanel να έχει setMap/setEnemies, ή κάνε recreate)
            frame.removeKeyListener(frame.getKeyListeners()[0]); // αφαιρείς παλιό keylistener αν είχε
            GamePanel newPanel = new GamePanel(newMap, player, newEnemies);
            // update references
            gamePanel = newPanel;
            enemies.clear();
            enemies.addAll(newEnemies);
            // βάζεις το νέο panel στη θέση του παλιού
            rootPanel.remove(0);
            rootPanel.add(gamePanel, BorderLayout.CENTER);
            frame.validate();
            frame.repaint();
            // add τον keylistener πάλι:
            gamePanel.setFocusable(true);
            gamePanel.requestFocusInWindow();
            gamePanel.addKeyListener(...); // Ξαναγράψε το keyListener block ή βάλε το σε μέθοδο
            logPanel.addMessage("New dungeon floor!");
        }

        
    }
}