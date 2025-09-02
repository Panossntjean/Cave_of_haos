package rogue.items.consumables;
import rogue.items.*;
import rogue.player.*;

public class HealthPotion extends Consumable {
    
    public HealthPotion() {
        super("Health Potion", 'H', 30, 0); // +30 HP, 0 MP
    }

    @Override
    public void onPickup(Player player) {
        player.restoreHP(30);
        //System.out.println(player.getName() + " drank a Health Potion! (+30 HP)");
    }
}