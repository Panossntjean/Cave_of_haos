package rogue.items;
import rogue.player.Player;

public abstract class Item {
    protected String name;
    protected char symbol;
    public ItemType type;

    public Item(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.type=type;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public abstract void onPickup(Player player);

}