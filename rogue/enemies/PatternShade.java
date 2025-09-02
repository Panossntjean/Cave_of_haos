package rogue.enemies;
import rogue.ui.Dice;

public class PatternShade extends AbstractEnemy {
    public PatternShade(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 50;
        this.dmg = 3;
        this.xp = 400;
        this.vis = 10;
    }

    @Override 
    public String getName(){
        return "Pattern Shade";
    }
    @Override
    public int getDmg(){
        return Dice.rollNDice(3, 6) + 10;
    }
}