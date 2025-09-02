package rogue.items;

import rogue.map.*;
import rogue.player.*;
import java.util.Random;
import rogue.items.Consumable;
import rogue.items.Item;
import rogue.items.ItemType;
import rogue.items.consumables.*;
import rogue.items.weapons.*;

public class ItemSpawner {

    private static final Random rand = new Random();
    
    public static void spawnRandomItems(GameMap map) {
        
        int numConsumables = 10; //maximum consumables on floor 
        int w = map.getWidth();
        int h = map.getHeight();         
        Tile tile;
        int[] entry = map.getEntrance();
        int[] exit = map.getExit();

        for (int i = 0; i <= numConsumables; i++) {
            int x, y;
            int tries = 0;
            do {
                x = rand.nextInt(map.getWidth());
                y = rand.nextInt(map.getHeight());
                tile = map.getTile(x,y);

                if ( isTileValidForItem(tile) &&
                    !(x == entry[0] && y == entry[1]) &&
                    !(x == exit[0] && y == exit[1])){            

                    // Randomly decide what to spawn
                    int r = rand.nextInt(100);
                    Item item;
                    if (r < 40) item = new HealthPotion();          // 40%
                    else if (r < 80) item = new ManaPotion();       // 40%
                    else item = new Trap("Spike Trap", '^', 10);    // 20% 

                    tile.setItem(item);

                    if (item != null) {
                        tries = 200;
                        break;
                    }
                    
                    tries++;
                }
            } while (tries < 100);   // we have to make sure that the item will not spawn
                                     // neither on the entrance or the exit of the map 
            
        }
    }

    /** NEW: drop a specific weapon (or any Item) once per floor, at a valid random tile */
    public static boolean spawnSpecificItem(GameMap map, Item item) {
        int[] entry = map.getEntrance();
        int[] exit  = map.getExit();

        for (int tries = 0; tries < 200; tries++) {
            int x = rand.nextInt(map.getWidth());
            int y = rand.nextInt(map.getHeight());
            Tile tile = map.getTile(x, y);

            if (isTileValidForItem(tile) &&
                !(x == entry[0] && y == entry[1]) &&
                !(x == exit[0]  && y == exit[1])) {

                tile.setItem(item);
                return true;
            }
        }
        return false; // couldn’t place 
    }


    

    private static boolean isTileValidForItem(Tile tile) {
        return  tile.getType() == TileType.FLOOR && tile.getItem() == null;// && !tile.hasEnemy();
    }
}