package rogue.enemies;

public class Apophis extends AbstractEnemy {
    public Apophis(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 1500;
        this.dmg = 8; //8d6+10        
        this.xp = 0;
        this.vis = 10;
    }

    @Override 
    public String getName(){
        return "Apophis";
    }
    
}