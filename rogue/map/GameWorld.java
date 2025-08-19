package rogue.map;

import rogue.enemies.*;
import rogue.map.GameMap;
import rogue.map.MapUtils;
import rogue.ui.*;
import rogue.player.*;
import rogue.items.*;
import rogue.items.weapons.*;

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

    public GameWorld(String name, PlayerClass playerClass) {
        System.out.println("GameWorld constructor: start");
        this.currentFloor = 0;
        this.floors = new ArrayList<>();
        this.floorEnemies = new ArrayList<>();
        this.player = new Player(name,playerClass,1, 1);
        player.recalcStatsFromEquipment();
        Random rand = new Random();

        System.out.println("GameWorld: floors init");
        for (int floor = 0; floor < NUM_FLOORS; floor++) {
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
            if (floor < 9){
                map.setExit(exitX, exitY);
            }
            floors.add(map);
            
            if (floor == 9) {
                List<Enemy> Boss = new ArrayList<>();
                /* 
                int centerX = map.getWidth()/2;
                int centerY = map.getHeight()/2;
                int radius = 1;
                boolean found = false;
                int center[] = new int[2];

                while (!found && radius < Math.max(map.getWidth(), map.getHeight())) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dy = -radius; dy <= radius; dy++) {
                            int x = centerX + dx;
                            int y = centerY + dy;
                            if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
                                if (map.getTile(x, y).getType() == TileType.FLOOR) {
                                    center[0] = x;
                                    center[1] = y;
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) break;
                    }
                    radius++;
                }*/

                ///////////////////////////////////////////////
                int w = map.getWidth();
                int h = map.getHeight(); // change to map etc         
                Tile tile;//= map.getTile(x,y);
                int[] entry = map.getEntrance();
                int[] exit = map.getExit();

                
                int x, y;
                int tries = 0;
                do {
                    x = rand.nextInt(map.getWidth());
                    y = rand.nextInt(map.getHeight());
                    tile = map.getTile(x,y);
                    if ( map.getTile(x, y).getType() == TileType.FLOOR && !isEnemyAt(x, y, enemies) &&
                        !(x == entry[0] && y == entry[1]) &&
                        !(x == exit[0] && y == exit[1])){            
                        
                        Boss.add(new Apophis(x, y)); 
                        break;                                      
                        
                    }
                    tries++;
                } while (tries < 100); 


            ///////////////////////////////////////////////
                //Boss initialized at the center of the map on 10nth floor 
                    
                floorEnemies.add(Boss);

            }else{
                floorEnemies.add(null);
            }
            //as it shows it does create 10 floor maps
            //System.out.println("GameWorld created map floor "+ floor);
            ItemSpawner.spawnRandomItems(map);
            Item weaponToDrop = pickWeaponForFloor(player.getPlayerClass(), floor);
            ItemSpawner.spawnSpecificItem(map, weaponToDrop);
            //pala; 

        }
        //this.player = new Player(name,playerClass,1, 1);
        //this.player.name = name;
        //this.player.playerClass = playerClass
        //currentFloor = 8; // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~test 
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
        GameMap map = getCurrentMap();
        EnemySpawner spawner = new EnemySpawner();
        List<Enemy> enemies;
        
        
        if(comingFromAbove) {
            spawn = findNearbySpawn(getEntrance(currentFloor));
        }else{
            spawn = findNearbySpawn(getExit(currentFloor));
        }       
    
        player.setPosition(spawn[0], spawn[1]);
        

        int entry[]= map.getEntrance();
        int exit [] = map.getExit();
        if (currentFloor < 9 ){
            //enemies = spawner.spawnEnemies(map, player.getLevel(), entry[0], entry[1], exit[0], exit[1]);
            //floorEnemies.set(currentFloor, enemies);
            floorEnemies.set(currentFloor, new ArrayList<>());
        }else if (currentFloor == 9){
            enemies = floorEnemies.get(9);
            Enemy boss = enemies.stream()
                    .filter(e -> "Apophis".equals(e.getName()))
                    .findFirst()
                    .orElse(null);
            spawnBoss(map,boss,enemies);
        }
        map.updateVisibility(player.getX(), player.getY(), 6);
                
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


    public void trySpawnEnemy(int playerLevel) {
        final int MAX_ENEMIES = 5;
        List<Enemy> enemies = getCurrentEnemies();
        if (enemies == null) {
            enemies = new ArrayList<>();
            floorEnemies.set(currentFloor, enemies);
        }

        if (enemies.size() >= MAX_ENEMIES) return;

        double hpPercent = (double) (player.getHp() / player.getMaxHp());// * 100;
        double spawnChance = hpPercent * 0.2;  // percentage spawn enemies with maximum probability of 0.2 % 
        
        if (Math.random() > spawnChance) return;

        GameMap map = getCurrentMap();
        Random rand = new Random();
        int px = player.getX();
        int py = player.getY();
        int vis = 6;

        // find a place near visibility of player
        int attempts = 50;
        List<Enemy> candidates = new ArrayList<>();
        while (attempts-- > 0) {
            int dx = rand.nextInt(vis + 3) - (vis + 1);  // από -vis-1 έως vis+1
            int dy = rand.nextInt(vis + 3) - (vis + 1);
            int x = px + dx;
            int y = py + dy;

            if (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight()) continue;
            if (Math.abs(dx) <= vis && Math.abs(dy) <= vis) continue;

            if (map.getTile(x, y).getType() == TileType.FLOOR && !isEnemyAt(x, y, enemies)) {
                //need fix in enemies spawn 
                if(playerLevel >= 1 && playerLevel < 2 ){

                    candidates.add(new ShadowSoldier(x, y));
                    candidates.add(new AncientGuard(x, y));

                }else if (playerLevel >= 2 && playerLevel < 3 ){
                    candidates.add(new AncientGuard(x, y));
                    candidates.add(new Fallen(x, y));

                }else if (playerLevel >= 3 && playerLevel < 4 ){
                    candidates.add(new Shade(x, y));
                    candidates.add(new GreaterShade(x, y));
                    candidates.add(new ShadowSerpent(x, y));

                }else if (playerLevel >= 4 && playerLevel < 5 ){
                    candidates.add(new GreaterShade(x, y));
                    candidates.add(new ShadowSerpent(x, y));
                    candidates.add(new ChaosKnight(x, y));

                }else if (playerLevel >= 5 && playerLevel <= 6 ){
                    candidates.add(new ChaosKnight(x, y));
                    candidates.add(new ChaosBeast(x, y));
                    candidates.add(new PatternShade(x, y));

                }else {//fallback
                    candidates.add(new ShadowSoldier(x, y));
                }

                Enemy chosen = candidates.get(rand.nextInt(candidates.size()));
                enemies.add(chosen);
                //logPanel.addMessage("careful there is a "+chosen.getName()+" nearby");
                candidates.clear();
                break;
            }
        }
    }

    private boolean isEnemyAt(int x, int y, List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (e.getX() == x && e.getY() == y) return true;
        }
        return false;
    }



    private Item pickWeaponForFloor(PlayerClass playerClass, int floor) {
    // Floor is whatever indexing you use (1..10). Adjust to your game.
        switch (floor) {
            case 0:  return (playerClass == PlayerClass.WARRIOR) ? new BronzeSword()     : new StaffOfLight();
            case 1:  return (playerClass == PlayerClass.WARRIOR) ? new SteelSword()     : new SecretWand();
            case 2:  return (playerClass == PlayerClass.WARRIOR) ? new BloodThirst()       : new SorcerersBook();
            case 3:  return (playerClass == PlayerClass.WARRIOR) ? new SilverDagger()      : new WitsEnd();
            case 4:  return (playerClass == PlayerClass.WARRIOR) ? new SwordOfFlames()     : new ArchangelsStaff();
            case 5:  return (playerClass == PlayerClass.WARRIOR) ? new KnightBlade()     : new AghanimsScepter();
            case 6:  return (playerClass == PlayerClass.WARRIOR) ? new BladeOfChaos()     : new LudensEcho();
            case 7:  return (playerClass == PlayerClass.WARRIOR) ? new SwordOfDarkKnight()     : new Morellonomicon();
            case 8:  return (playerClass == PlayerClass.WARRIOR) ? new BladeOfTheRuinedKing()     : new VoidStaff();
            default: return (playerClass == PlayerClass.WARRIOR) ? new WoodenSword()      : new ApprenticeStaff();
        }
    }
    
    public void spawnBoss(GameMap map, Enemy Boss, List<Enemy> enemies){
        Random rand = new Random();
        int w = map.getWidth();
        int h = map.getHeight(); // change to map etc         
        Tile tile;//= map.getTile(x,y);
        int[] entry = map.getEntrance();
        int[] exit = map.getExit();   
        
        int x, y;
        int tries = 0;
        do {
            x = rand.nextInt(map.getWidth());
            y = rand.nextInt(map.getHeight());
            tile = map.getTile(x,y);
            if ( map.getTile(x, y).getType() == TileType.FLOOR && !isEnemyAt(x, y, enemies) &&
                !(x == entry[0] && y == entry[1]) &&
                !(x == exit[0] && y == exit[1])){            
            
                Boss.setPosition(x,y);
                break;                                      
            
            }
            tries++;
        } while (tries < 100); 
    }

}
