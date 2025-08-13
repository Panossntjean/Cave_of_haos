package rogue.items;
import rogue.player.Player;

public class Consumable extends Item {
    protected int hpRestore;
    protected int mpRestore;
    private ItemType type;

    public Consumable(String name, char symbol, int hpRestore, int mpRestore) {
        super(name, symbol);
        this.hpRestore = hpRestore;
        this.mpRestore = mpRestore;
        this.type = type.Consumable;
    }

    @Override
    public void onPickup(Player player) {
        player.restoreHP(hpRestore);
        player.restoreMP(mpRestore);
        //System.out.println(player.getName() + " used " + name + "! (+" + hpRestore + " HP, +" + mpRestore + " MP)");
    }
}