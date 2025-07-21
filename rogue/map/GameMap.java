package rogue.map;

import java.util.Random;

public class GameMap {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;

    private Tile[][] tiles;

    public GameMap() {
        tiles = new Tile[WIDTH][HEIGHT];
        generateRandomMap();
    }

    private void generateRandomMap() {
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
}