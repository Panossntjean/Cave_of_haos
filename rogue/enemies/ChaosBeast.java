package rogue.enemies;

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
}