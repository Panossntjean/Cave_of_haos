package rogue.enemies;

public class ShadowSerpent extends AbstractEnemy {
    public ShadowSerpent(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 20;
        this.dmg = 4; // 4d6+10  is 4 times a dice +10
        this.xp = 100;
        this.vis = 4;
    }

    @Override 
    public String getName(){
        return "Shadow Serpent";
    }
}