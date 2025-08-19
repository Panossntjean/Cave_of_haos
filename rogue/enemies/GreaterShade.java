package rogue.enemies;

public class GreaterShade extends AbstractEnemy {
    public GreaterShade(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 50;
        this.dmg = 12;        
        this.xp = 120;
        this.vis = 7;
    }

    @Override 
    public String getName(){
        return "Greater Shade";
    }
}