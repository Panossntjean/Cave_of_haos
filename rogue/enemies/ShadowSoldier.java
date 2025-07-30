package rogue.enemies;

public class ShadowSoldier extends AbstractEnemy {
    public ShadowSoldier(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 5;
        this.dmg = 2;        
        this.xp = 30;
        this.vis = 4;
    }

    @Override 
    public String getName(){
        return "Shadow Soldier";
    }
    
}