package rogue.enemies;

public class ShadowSoldier implements Enemy {
    private int x, y;
    private int hp = 10;

    public ShadowSoldier(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public int getX() { return x; }
    @Override public int getY() { return y; }
    @Override public void setPosition(int x, int y) { this.x = x; this.y = y; }
    @Override public String getName() { return "Shadow Soldier"; }
    @Override public int getHp() { return hp; }
    @Override public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    @Override public boolean isDead() { return hp <= 0; }

    @Override
    public void takeTurn(int playerX, int playerY, boolean[][] floorMap) {
        int dx = Integer.compare(playerX, x);
        int dy = Integer.compare(playerY, y);
        int newX = x + dx;
        int newY = y + dy;
        if (newX >= 0 && newX < floorMap.length && newY >= 0 && newY < floorMap[0].length
            && floorMap[newX][newY]) {
            x = newX;
            y = newY;
        }
    }
}