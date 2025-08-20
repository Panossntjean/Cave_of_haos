package rogue.enemies;

public class Shade extends AbstractEnemy {
    public Shade(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 40;
        this.dmg = 10;        
        this.xp = 100;
        this.vis = 6;
    }

    @Override 
    public String getName(){
        return "Shade";
    }
}