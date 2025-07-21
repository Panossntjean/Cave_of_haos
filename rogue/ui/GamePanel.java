package rogue.ui;

import rogue.map.*;
import rogue.player.*;
import rogue.enemies.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private GameMap gameMap;
    private Player player;
    private List<Enemy> enemies;

    public GamePanel(GameMap gameMap, Player player, List<Enemy> enemies) {
        this.gameMap = gameMap;
        this.player = player;
        this.enemies = enemies;
        setPreferredSize(new Dimension(gameMap.getWidth() * 20, gameMap.getHeight() * 20));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileSize = 20;
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.getType() == TileType.WALL) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(new Color(180, 160, 100));
                }
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
        // Παίκτης
        g.setColor(new Color(30, 200, 200));
        g.fillOval(player.getX() * tileSize + 2, player.getY() * tileSize + 2, tileSize - 4, tileSize - 4);

        // Εχθροί
        g.setColor(new Color(200, 60, 60));
        for (Enemy enemy : enemies) {
            g.setColor(new Color(200, 60, 60));
            g.fillRect(enemy.getX() * tileSize + 4, enemy.getY() * tileSize + 4, tileSize - 8, tileSize - 8);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("" + enemy.getHp(), enemy.getX() * tileSize + 7, enemy.getY() * tileSize + 18);
        }
        if (enemies.isEmpty()) {
            int[] exit = gameMap.getExit();
            g.setColor(Color.GREEN);
            g.fillRect(exit[0] * tileSize, exit[1] * tileSize, tileSize, tileSize);
            g.setColor(Color.BLACK);
            g.drawRect(exit[0] * tileSize, exit[1] * tileSize, tileSize, tileSize);
        }

    }

        
}