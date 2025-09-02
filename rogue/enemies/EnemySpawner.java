package rogue.enemies;

import java.util.*;
import rogue.map.*;

public class EnemySpawner {
    private Random rand = new Random();
    
    public List<Enemy> spawnEnemies(GameMap map, int playerLevel, int entryX, int entryY, int exitX, int exitY) {
        List<Enemy> enemies = new ArrayList<>();
        //this is spawning enemies randomly in the map BUT  we want to spawn enemies on the floor after every movement of the player depending on his hp | and also have maximum enemies per floor
        // Enemies for the current floor it depend on player level            
        int maxEnemies = 6; // max enemies per floor  
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
            // Spawning enemies based on player level
            if(playerLevel >= 1 && playerLevel < 2 ){
                enemies.add(new ShadowSoldier(ex, ey));
                //enemies.add(new AncientGuard(ex, ey));

            }else if (playerLevel >= 2 && playerLevel < 3 ){
                enemies.add(new AncientGuard(ex, ey));
                enemies.add(new Fallen(ex, ey));

            }else if (playerLevel >= 3 && playerLevel < 4 ){
                enemies.add(new Shade(ex, ey));
                enemies.add(new GreaterShade(ex, ey));
                enemies.add(new ShadowSerpent(ex, ey));

            }else if (playerLevel >= 4 && playerLevel < 5 ){
                enemies.add(new GreaterShade(ex, ey));
                enemies.add(new ShadowSerpent(ex, ey));
                enemies.add(new ChaosKnight(ex, ey));

            }else if (playerLevel >= 5 && playerLevel <= 6 ){
                enemies.add(new ChaosKnight(ex, ey));
                enemies.add(new ChaosBeast(ex, ey));
                enemies.add(new PatternShade(ex, ey));

            }else {//fallback
                enemies.add(new ShadowSoldier(ex, ey));
            }
            
        }            
        return enemies;
    }
}

