package rogue.map;
import java.util.Random;
import rogue.map.Tile;
import rogue.map.TileType;


public class GameMap {

    public enum VisibilityState {
        UNKNOWN, FOGGY, VISIBLE
    }
    int floor = 0;
    int startX = 0;
    int startY = 0;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    
    
    public Tile[][] tiles;
    private VisibilityState[][] visibility;

    public GameMap() {
        tiles = new Tile[WIDTH][HEIGHT];
        visibility = new VisibilityState[WIDTH][HEIGHT];
        generateRandomMap();
        initializeVisibility();
        floor++;
    }

    public void generateRandomMap() {
        // Fill with walls
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(TileType.WALL);
            }
        }
        // Carve out a cave using random walk
        int percentage = 45; // % of map to be floor
        int total = WIDTH * HEIGHT;
        int targetFloor = total * percentage / 100;

        Random rand = new Random();
        int x = WIDTH / 2;
        int y = HEIGHT / 2;
        tiles[x][y].setType(TileType.FLOOR);
        int floorCount = 1;

        while (floorCount < targetFloor) {
            int dir = rand.nextInt(4);
            switch (dir) {
                case 0: x = Math.max(1, x - 1); break;
                case 1: x = Math.min(WIDTH - 2, x + 1); break;
                case 2: y = Math.max(1, y - 1); break;
                case 3: y = Math.min(HEIGHT - 2, y + 1); break;
            }
            if (tiles[x][y].getType() == TileType.WALL) {
                tiles[x][y].setType(TileType.FLOOR);
                
                floorCount++;
            }
        }
    }

    
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    private int[] entrance = new int[2];
    private int[] exit = new int[2];

    public void setEntrance(int x, int y) { entrance[0] = x; entrance[1] = y; }
    public void setExit(int x, int y) { exit[0] = x; exit[1] = y; }
    public void setExitDown(int x, int y) { exit[0] = x; exit[1] = y; }

    public int[] getEntrance() { return entrance; }
    public int[] getExit() { return exit; }

    private void initializeVisibility() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                visibility[x][y] = VisibilityState.UNKNOWN;
            }
        }
    }

    public void updateVisibility(int playerX, int playerY, int radius) {
        // Mark previously visible tiles as FOGGY
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (visibility[x][y] == VisibilityState.VISIBLE) {
                    visibility[x][y] = VisibilityState.FOGGY;
                }
            }
        }

        // Update currently visible tiles using Manhattan distance
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int nx = playerX + dx;
                int ny = playerY + dy;
                if (inBounds(nx, ny) && manhattanDistance(playerX, playerY, nx, ny) < radius) {
                    visibility[nx][ny] = VisibilityState.VISIBLE;
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    public int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
        
    public VisibilityState getVisibilityState(int x, int y) {
        if (inBounds(x, y)) {
            return visibility[x][y];
        }
        return VisibilityState.UNKNOWN;
    }

}
