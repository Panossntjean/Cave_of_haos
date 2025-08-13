package rogue.enemies;

public class ChaosKnight extends AbstractEnemy {
    public ChaosKnight(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 60;
        this.dmg = 20;        
        this.xp = 150;
        this.vis = 9;
    }

    @Override 
    public String getName(){
        return "Chaos Knight";
    }
}