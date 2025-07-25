package rogue.map;

import rogue.enemies.*;
import rogue.map.GameMap;
import rogue.map.MapUtils;
import rogue.ui.*;
import rogue.player.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class GameWorld {

    private List<GameMap> floors;
    private List<List<Enemy>> floorEnemies;
    private  Player player;
    public static final int NUM_FLOORS = 10;
    private int currentFloor = 0;

    public GameWorld() {
        System.out.println("GameWorld constructor: start");
        this.currentFloor = 0;
        this.floors = new ArrayList<>();
        this.floorEnemies = new ArrayList<>();
        Random rand = new Random();

        System.out.println("GameWorld: floors init");
        for (int floor = 0; floor <= NUM_FLOORS; floor++) {
            GameMap map = new GameMap();
            List<Enemy> enemies = new ArrayList<>();

            //for entrance and exit
            int entryX = 0, entryY = 0 , exitX = 0, exitY = 0;
            int[] prevExit;
            
            if (floor ==0 ){// First floor only  rest of them based on the exits of previous floors
                 // random picking a corner of the map 
                int[][] corners = {
                    {1, 1},
                    {map.getWidth() - 2, 1},
                    {1, map.getHeight() - 2},
                    {map.getWidth() - 2, map.getHeight() - 2}
                };

                int[] corner = corners[rand.nextInt(4)];
                entryX = corner[0];
                entryY = corner[1];

                // find floor tile or the next available 
                int tries = 0;
                while (map.getTile(entryX, entryY).getType() != TileType.FLOOR) {
                    entryX = rand.nextInt(map.getWidth());
                    entryY = rand.nextInt(map.getHeight());
                    tries++;
                    if (tries > 10000) {
                        System.out.println("Stuck finding entry on floor 0");
                        break;
                    }
                }
            }else {

                // Rest of the floors entrance is at the exit of the previous map
                prevExit = floors.get(floor - 1).getExit();
                entryX = prevExit[0];
                entryY = prevExit[1];
                int tries = 0 ;
                while (map.getTile(entryX,entryY).getType() != TileType.FLOOR) {
                    entryX = rand.nextInt(map.getWidth());
                    entryY = rand.nextInt(map.getHeight());
                    tries++;
                    if (tries > 100000) {
                        System.out.println("Stuck finding escape on floor " + floor);
                        break;
                    }
                    
                }              
            }
            // Find exit the most distance from the entrance
            int[] furthestExit = MapUtils.findFurthestReachableFloorTile(map, entryX, entryY);
            exitX = furthestExit[0];
            exitY = furthestExit[1];

            map.setEntrance(entryX, entryY);
            map.setExit(exitX, exitY);
            
            // Enemies for every floor            
            int numEnemies = 3 + floor; // difficulty 
            for (int i = 0; i < numEnemies; i++) {
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
                        System.out.println("Stuck finding entry 2 on floor " + floor);
                        break; 
                    }
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
            //as it shows it does create 10 floor maps
            //System.out.println("GameWorld created map floor "+ floor);
        }
        this.player = new Player(1, 1);
        setupCurrentFloor(true);
        System.out.println("GameWorld constructor: end");
    }

    //----------------   PUBLIC API   ----------------------

    public GameMap getFloor(int i) { return floors.get(i); } // getCurrentMap same thing
    public List<Enemy> getEnemies(int i) { return floorEnemies.get(i); }
    public int[] getEntrance(int i) { return floors.get(i).getEntrance(); }
    public int[] getExit(int i) { return floors.get(i).getExit(); }
    public Player getPlayer() { return this.player; }

    public int getCurrentFloor() { return currentFloor; }
    public GameMap getCurrentMap(){ return floors.get(currentFloor); }
    public List<Enemy> getCurrentEnemies() { return floorEnemies.get(currentFloor); }

    public boolean moveToNextFloor(){
        if (currentFloor < NUM_FLOORS - 1){
            currentFloor++;
            setupCurrentFloor(true); // true if going upstairs
            return true;
        }
        return false;
    }

    public boolean moveToPreviousFloor(){
        if (currentFloor >0){
            currentFloor--;
            setupCurrentFloor(false); // false if going downstairs
            return true;
        }
        return false;
    }
    
    //--------------  Core: when changing floors -------------
    private void setupCurrentFloor(boolean comingFromAbove) {   
        int[] spawn;
        
        if(comingFromAbove) {
            spawn = findNearbySpawn(getEntrance(currentFloor));
        }else{
            spawn = findNearbySpawn(getExit(currentFloor));
        }       
    
        player.setPosition(spawn[0], spawn[1]);
        
    }

    private int[] findNearbySpawn(int[] center) {
        GameMap map = getCurrentMap();
        int x = center[0];
        int y = center[1];
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (int[] d : directions) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && ny >= 0 && nx < map.getWidth() && ny < map.getHeight() &&
                map.getTile(nx, ny).getType() == TileType.FLOOR) {
                return new int[]{nx, ny};
            }
        }

        return new int[]{x, y}; // fallback
    }

    public boolean isPlayerOnEntrance(){
        int[] entrance = getEntrance(currentFloor);
        return player.getX() == entrance[0] && player.getY() == entrance[1];
    }

    public boolean isPlayerOnExit() {
        int[] exit = getExit(currentFloor);
        return player.getX() == exit[0] && player.getY() == exit[1];
    }

    



}
