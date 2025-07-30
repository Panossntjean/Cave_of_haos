package rogue.enemies;

import java.util.*;
import rogue.map.*;

public class EnemySpawner {
    private Random rand = new Random();
    
    public List<Enemy> spawnEnemies(GameMap map, int playerLevel, int entryX, int entryY, int exitX, int exitY) {
        List<Enemy> enemies = new ArrayList<>();
        // Enemies for the current floor it depend on player level            
        int maxEnemies = 3; // max enemies per floor will see how we will work on that 
        for (int i = 0; i < maxEnemies; i++) {
            int ex, ey;
            boolean conflict;
            int tries = 0;
            do {
                ex = rand.nextInt(map.getWidth());
                ey = rand.nextInt(map.getHeight());
                final int fex = ex, fey = ey;
                conflict = enemies.stream().anyMatch(e -> e.getX() == fex && e.getY() == fey);
                tries++;
                if (tries > 10000) {
                    System.out.println("Stuck finding entry on floor ");
                    break; 
                }
            } while (
                map.getTile(ex, ey).getType() != TileType.FLOOR ||
                (ex == entryX && ey == entryY) ||
                (ex == exitX && ey == exitY) ||
                conflict
                
            );

            switch(playerLevel){
                case 1:
                    enemies.add(new ShadowSoldier(ex, ey));
                    break;
                case 2:
                    enemies.add(new AncientGuard(ex, ey));
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                default:
                    enemies.add(new ShadowSoldier(ex, ey));
                    break;
            }            
        }            
        return enemies;
    }
}

