package rogue.enemies;
import rogue.ui.Dice;

public class ChaosBeast extends AbstractEnemy {

    public ChaosBeast(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 80;
        this.dmg = 2; //2d6+10        
        this.xp = 200;
        this.vis = 5;
    }

    @Override 
    public String getName(){
        return "Chaos Beast";
    }

    @Override
    public int getDmg(){
        return Dice.rollNDice(2, 6) + 10;
    }
}