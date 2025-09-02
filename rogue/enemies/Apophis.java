package rogue.enemies;
import rogue.ui.Dice;

public class Apophis extends AbstractEnemy {
    public Apophis(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 1500;
        this.dmg = 8;        
        this.xp = 0;
        this.vis = 10;
    }

    @Override 
    public String getName(){
        return "Apophis";
    }
    
    @Override
    public int getDmg(){
        return Dice.rollNDice(8, 6) + 10;
    }
}