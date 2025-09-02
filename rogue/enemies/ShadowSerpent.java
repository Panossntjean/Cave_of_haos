package rogue.enemies;
import rogue.ui.Dice;

public class ShadowSerpent extends AbstractEnemy {
    public ShadowSerpent(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 20;
        this.dmg = 4;
        this.xp = 100;
        this.vis = 4;
    }

    @Override 
    public String getName(){
        return "Shadow Serpent";
    }

    @Override
    public int getDmg(){
        return Dice.rollNDice(4, 6) + 10;
    }
}