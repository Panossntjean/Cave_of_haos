package rogue;

import rogue.map.*;
import rogue.player.*;
import rogue.ui.*;
import rogue.items.*;
import rogue.items.weapons.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import rogue.enemies.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Comparator;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Welcome fellow wonderer to the Caves of Chaos");
        //int currentFloor = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        
        System.out.print("Choose class (1 - Warrior, 2 - Mage): ");
        int choice = scanner.nextInt();

        PlayerClass chosenClass = (choice == 2) ? PlayerClass.MAGE : PlayerClass.WARRIOR;
        
        GameWorld gameWorld = new GameWorld(name,chosenClass);
        Player player = gameWorld.getPlayer();
        //player.setName(name);
        //player.setPlayerClass(chosenClass);

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
        
        
        gamePanel.requestFocusInWindow();
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
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT ){
                    player.move(key,currentMap,logPanel,enemies);
                    currentMap.updateVisibility(player.getX(), player.getY(), 6);
                    //maybe check the tile if has item
                    //spawn enemies  
                    gameWorld.trySpawnEnemy(player.getLevel());  // make root panel appear a message later on                 
                }

                //enemies.sort(Comparator.comparingInt(
                    //enemy -> currentMap.manhattanDistance(enemy.getX(),enemy.getY(),player.getX(), player.getY())
                //));


                // --- Enemies take their turn---
                for (Enemy enemy : enemies) {
                    enemy.takeTurn(player.getX(), player.getY(), floorMap);
                    //logPanel.addMessage("careful there is a "+enemy.getName()+" nearby");
                }




                                
                // ENEMIES ATTACK
                boolean playerAttacked = false;
                    
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);
                        
                    if (Math.abs(enemy.getX() - player.getX()) <=1 && Math.abs(enemy.getY() - player.getY())<=1) {
                        // Simple battle: enemies hit always , player hit with action space 
                        //----------------------------------Will fix this later
                        player.takeDamage(enemy.getDmg());
                        
                    }
                }


                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    player.attack(enemies, currentMap, logPanel);
                    //enemy.takeDamage(player.getDmg());
                    //logPanel.addMessage("Battle with " + enemy.getName() + "! Player -"+ enemy.getDmg() +"HP, Enemy -"+ player.getDmg() +" HP.");
                    Iterator<Enemy> it = enemies.iterator();
                    while (it.hasNext()) {
                        Enemy en = it.next();
                        if (en.getHp() <= 0) {
                            player.addXp(en.getXp());                            
                            // maybe drop item here too
                            
                            if (Math.random() < 0.25) {  // 25% drop chance
                                Item drop = en.drop();   // make sure you have this method
                                currentMap.getTile(en.getX(), en.getY()).setItem(drop);
                                //DROP ITEM
                            }

                            if (en.getName() == "Apophis")
                            {
                                logPanel.addMessage("You killed The Boss!!!");
                                Item bossDrop = new GemOfJudgement();
                                currentMap.getTile(en.getX(), en.getY()).setItem(bossDrop);
                            }
                            it.remove();
                        }
                        
                    }
                    playerAttacked = true;

                }


                //Rest Mode
                if(key == KeyEvent.VK_R){
                    player.Rest();
                    //spawn enemies?
                    gameWorld.trySpawnEnemy(player.getLevel());
                }

                if(key == KeyEvent.VK_H){
                    boolean used = player.useHealthPotion();
                    if (used) logPanel.addMessage("You used a Health Potion.");
                    else logPanel.addMessage("No Health Potion available.");
                }

                if (key == KeyEvent.VK_M) {
                    boolean used = player.useManaPotion();
                    if (used) logPanel.addMessage("You used a Mana Potion.");
                    else logPanel.addMessage("No Mana Potion available.");
                }



                Tile tile = currentMap.getTile(player.getX(), player.getY());
                Item item = tile.getItem();

                if (item instanceof Weapon) {
                    
                    logPanel.addMessage("You Found " + item.getName());
                    logPanel.addMessage("press P to pick up weapon");
                    if (key == KeyEvent.VK_P){
                        Weapon newWeapon = (Weapon) item;

                        //Weapon old = player.getEquippedWeapon();
                        //player.equipWeapon(newWeapon);
                        Weapon old = player.swapWeapon(newWeapon);
                        tile.setItem(old); // drop the old one to the tile 
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
