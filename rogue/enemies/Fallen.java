package rogue.enemies;

public class Fallen extends AbstractEnemy {
    public Fallen(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 30;
        this.dmg = 8;        
        this.xp = 80;
        this.vis = 2;
    }

    @Override 
    public String getName(){
        return "Fallen";
    }
}