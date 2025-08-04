package rogue.items.consumables;
import rogue.items.*;
import rogue.player.*;

public class ManaPotion extends Consumable {

    public ManaPotion() {
        super("Mana Potion", 'M', 0, 20); // 0 HP, +20 MP
    }

    @Override
    public void onPickup(Player player) {
        player.restoreMP(20);
        //System.out.println(player.getName() + " drank a Mana Potion! (+20 MP)");
    }
}