package rogue.items.consumables;
import rogue.items.*;
import rogue.player.*;

public class Trap extends Consumable {

    public Trap(String name, char symbol, int damage) {
        super(name, symbol, -damage, 0); // hp-dmg 
    }

    @Override
    public void onPickup(Player player) {
        player.takeDamage(-hpRestore);
        //System.out.println(player.getName() + " stepped on a trap! (-" + damage + " HP)");
    }
}
