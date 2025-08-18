
package rogue.items;

import rogue.items.Item;
import rogue.player.Player;

public abstract class Weapon extends Item {
    protected int hpBoost;
    protected int mpBoost;
    protected int strengthBoost;
    protected int intelligenceBoost;

    public Weapon(String name, char symbol, int hpBoost, int mpBoost, int strengthBoost, int intelligenceBoost) {
        super(name, symbol);
        this.hpBoost = hpBoost;
        this.mpBoost = mpBoost;
        this.strengthBoost = strengthBoost;
        this.intelligenceBoost = intelligenceBoost;
    }

    public int getHpBoost() { return hpBoost; }
    public int getMpBoost() { return mpBoost; }
    public int getStrengthBoost() { return strengthBoost; }
    public int getIntelligenceBoost() { return intelligenceBoost; }

    @Override
    public void onPickup(Player player) {
        //player.equipWeapon(this);
    }
}