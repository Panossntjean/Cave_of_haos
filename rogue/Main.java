package rogue;

import rogue.map.*;
import rogue.player.*;
import rogue.ui.*;
import rogue.items.*;


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
        
        
        
        gamePanel.addKeyListener(new KeyAdapter() {
            int key; 
            @Override
            public void keyPressed(KeyEvent e) {
                GameMap currentMap = gameWorld.getCurrentMap(); // these i have to import them on SetupCurrentFloor
                List<Enemy> enemies = gameWorld.getCurrentEnemies();//  and here 

                //Player player = gameWorld.getPlayer();
                boolean[][] floorMap = generateFloorMap(gameWorld.getCurrentMap());
                //int currentFloor  = gameWorld.getCurrentFloor();                
                
                

                key = e.getKeyCode() ;
                //move player WSAD
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D ){
                    player.move(key,currentMap);
                    currentMap.updateVisibility(player.getX(), player.getY(), 6);
                    //maybe check the tile if has item
                    //spawn enemies  
                    gameWorld.trySpawnEnemy(player.getLevel());                  
                }

                // --- Enemies take their turn---
                    for (Enemy enemy : enemies) {
                        enemy.takeTurn(player.getX(), player.getY(), floorMap);
                        //logPanel.addMessage("careful there is a "+enemy.getName()+" nearby");
                    }
                                
                // COMBAT CHECK
                boolean playerAttacked = false;
                    
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy enemy = enemies.get(i);
                            
                        if (Math.abs(enemy.getX() - player.getX()) <=1 && Math.abs(enemy.getY() - player.getY())<=1) {
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
                                player.addXp(enemy_xp); // player get exp by defeating an enemy
                                //enemy.drop();
                                gameWorld.getCurrentEnemies().remove(i);
                                i--; // VERY important: avoid skipping next enemy
                                
                            }
                            playerAttacked = true;
                        }
                    }


                //Rest Mode
                if(key == KeyEvent.VK_R){
                    player.Rest();
                    //spawn enemies?
                    gameWorld.trySpawnEnemy(player.getLevel());
                }

                    
                                    statusPanel.updateStatus();

                if (player.isDead()) {
                    logPanel.addMessage("Game Over! The player died!");
                    JOptionPane.showMessageDialog(frame, "Game Over! You died!", "Game Over", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                    
                    
                gamePanel.repaint();
                statusPanel.updateStatus();                    
                    
                
                gamePanel.requestFocusInWindow();
                
                if (gameWorld.isPlayerOnExit()){                    
                    boolean floorUp = gameWorld.moveToNextFloor();
                    floorMap = generateFloorMap(gameWorld.getCurrentMap());                    
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
                    floorMap = generateFloorMap(gameWorld.getCurrentMap());
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

    private static boolean[][] generateFloorMap(GameMap map) {
        boolean[][] mapData = new boolean[map.getWidth()][map.getHeight()];
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                mapData[x][y] = map.getTile(x, y).getType() == TileType.FLOOR;
            }
        }
        return mapData;
    }

}