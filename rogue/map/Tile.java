package rogue.map;

public class Tile {
    private TileType type;
    private TileVisibility vision;

    public Tile(TileType type) {
        this.type = type;
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
}