package rogue.ui;

import rogue.map.*;
import rogue.player.*;
import rogue.ui.*;
import rogue.enemies.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private GameWorld gameWorld;

    public GamePanel(GameWorld gameWorld) {
        this.gameWorld = gameWorld;        
        //old style 
        //setPreferredSize(new Dimension(gameWorld.getCurrentMap().getWidth() * 20, gameWorld.getCurrentMap().getWidth() * 20));
        //new style ||
        //          \/
        setPreferredSize(new Dimension(600,480));
        
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameMap gameMap = gameWorld.getCurrentMap();
        Player player = gameWorld.getPlayer();
        List<Enemy> enemies = gameWorld.getCurrentEnemies();

        int [] entrance = gameMap.getEntrance();
        int[] exit = gameMap.getExit();
        int tileSize = 20;
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                //Great this is going well and coloring is good
                /*Tile tile = gameMap.getTile(x, y);
                if (tile.getType() == TileType.WALL) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(new Color(180, 160, 100));
                }
                */
                if (x == entrance[0] && y == entrance[1]) {
                    g.setColor(new Color(255, 165, 0)); // ORANGE entrance
                } else if (x == exit[0] && y == exit[1]) {
                        g.setColor(Color.GREEN); // green exit
                } else if (gameMap.getTile(x, y).getType() == TileType.FLOOR) {
                    g.setColor(new Color(180, 160, 100)); // default floor
                } else if (gameMap.getTile(x, y).getType() == TileType.WALL) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(Color.BLACK); // default
                }

                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
                
                }
            }
        
        // Player
        g.setColor(new Color(30, 200, 200));
        g.fillOval(player.getX() * tileSize + 2, player.getY() * tileSize + 2, tileSize - 4, tileSize - 4);
        
        
        
        // Enemies
        g.setColor(new Color(200, 60, 60));
        
        for (Enemy enemy : enemies) {
            g.setColor(new Color(200, 60, 60));
            g.fillRect(enemy.getX() * tileSize + 4, enemy.getY() * tileSize + 4, tileSize - 8, tileSize - 8);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("" + enemy.getHp(), enemy.getX() * tileSize + 7, enemy.getY() * tileSize + 18);
        }
        if (enemies.isEmpty()) {
            //int[] exit = gameMap.getExit();
            g.setColor(Color.GREEN);
            g.fillRect(exit[0] * tileSize, exit[1] * tileSize, tileSize, tileSize);
            g.setColor(Color.BLACK);
            g.drawRect(exit[0] * tileSize, exit[1] * tileSize, tileSize, tileSize);
        }

    }


    public void refresh() {
    repaint();
    }

}