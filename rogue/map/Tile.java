package rogue.map;
import rogue.items.*;

public class Tile {
    private TileType type;
    private TileVisibility vision;
    private Item item;

    public Tile(TileType type) {
        this.type = type;
        this.item = null;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public Tile(TileVisibility vision){
        this.vision = vision;
    }

    public TileVisibility getVision() {
        return vision;
    }

    public void setVision(TileVisibility vision){
        this.vision = vision;
    }

    public Item getItem() {
    return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void clearItem() {
        this.item = null;
    }


}





