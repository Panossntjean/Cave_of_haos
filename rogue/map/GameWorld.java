package rogue.map;

import rogue.enemies.*;
import rogue.map.GameMap;
import java.util.*;

public class GameWorld {
    private List<GameMap> floors;
    private List<List<Enemy>> floorEnemies;
    public static final int NUM_FLOORS = 10;

    public GameWorld() {
        floors = new ArrayList<>();
        floorEnemies = new ArrayList<>();
        Random rand = new Random();

        for (int floor = 0; floor < NUM_FLOORS; floor++) {
            GameMap map = new GameMap();

            // Εύρεση entry (είσοδος) και exit (έξοδος)
            int entryX, entryY, exitX, exitY;

            if (floor == 0) {
                // Πρώτο floor, αρχική είσοδος στη μέση
                entryX = map.getWidth() / 2;
                entryY = map.getHeight() / 2;
                while (map.getTile(entryX, entryY).getType() != TileType.FLOOR) {
                    entryX = rand.nextInt(map.getWidth());
                    entryY = rand.nextInt(map.getHeight());
                }
            } else {
                // Είσοδος όπου ήταν η έξοδος του προηγούμενου
                int[] prevExit = floors.get(floor - 1).getExit();
                entryX = prevExit[0];
                entryY = prevExit[1];
            }

            // Βρες exit (μακρινότερο FLOOR tile από την είσοδο)
            int maxDist = -1;
            exitX = entryX; exitY = entryY;
            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    if (map.getTile(x, y).getType() == TileType.FLOOR) {
                        int dist = Math.abs(x - entryX) + Math.abs(y - entryY);
                        if (dist > maxDist) {
                            maxDist = dist;
                            exitX = x; exitY = y;
                        }
                    }
                }
            }

            map.setEntrance(entryX, entryY);
            map.setExit(exitX, exitY);

            // Εχθροί για κάθε floor
            List<Enemy> enemies = new ArrayList<>();
            int numEnemies = 3 + floor; // δυσκολία!
            for (int i = 0; i < numEnemies; i++) {
                int ex, ey;
                boolean conflict;
                do {
                    ex = rand.nextInt(map.getWidth());
                    ey = rand.nextInt(map.getHeight());
                    final int fex = ex, fey = ey;
                    conflict = enemies.stream().anyMatch(e -> e.getX() == fex && e.getY() == fey);
                } while (
                    map.getTile(ex, ey).getType() != TileType.FLOOR ||
                    (ex == entryX && ey == entryY) ||
                    (ex == exitX && ey == exitY) ||
                    conflict
                );
                enemies.add(new rogue.enemies.ShadowSoldier(ex, ey));
            }

            floors.add(map);
            floorEnemies.add(enemies);
        }
    }

    public GameMap getFloor(int i) { return floors.get(i); }
    public List<Enemy> getEnemies(int i) { return floorEnemies.get(i); }
    public int[] getEntrance(int i) { return floors.get(i).getEntrance(); }
    public int[] getExit(int i) { return floors.get(i).getExit(); }

    // Call when all enemies defeated to "activate" the exit (μπορείς να το κάνεις και απλά στο Main)
}
