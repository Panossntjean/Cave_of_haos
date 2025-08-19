package rogue.enemies;

public class AncientGuard extends AbstractEnemy {
    public AncientGuard(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 15;
        this.dmg = 5;        
        this.xp = 50;
        this.vis = 7;
    }

    @Override 
    public String getName(){
        return "Ancient Guard";
    }
}