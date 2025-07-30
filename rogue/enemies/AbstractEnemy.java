package rogue.enemies;

import java.util.Random;

public abstract class AbstractEnemy implements Enemy {
    protected int x, y;
    protected int hp;
    protected int dmg;
    protected int xp;
    protected int vis;
    private final Random random = new Random();

    @Override public int getX() { return x; }
    @Override public int getY() { return y; }
    @Override public void setPosition(int x, int y) { this.x = x; this.y = y; }
    @Override public int getHp() { return hp; }
    @Override public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    @Override public boolean isDead() { return hp <= 0; }

    @Override
    public void takeTurn(int playerX, int playerY, boolean[][] floorMap) {
        int distX = Math.abs(playerX - x);
        int distY = Math.abs(playerY - y);
        double distance = Math.sqrt(distX * distX + distY * distY);

        if (distance > vis) return;

        int dx = Integer.compare(playerX, x);
        int dy = Integer.compare(playerY, y);

        if (distX > distY) {
            tryMove(x + dx, y, floorMap);
        } else if (distY > distX) {
            tryMove(x, y + dy, floorMap);
        } else {
            if (random.nextBoolean()) {
                if (!tryMove(x + dx, y, floorMap)) tryMove(x, y + dy, floorMap);
            } else {
                if (!tryMove(x, y + dy, floorMap)) tryMove(x + dx, y, floorMap);
            }
        }
    }

    protected boolean tryMove(int newX, int newY, boolean[][] floorMap) {
        if (newX >= 0 && newX < floorMap.length &&
            newY >= 0 && newY < floorMap[0].length &&
            floorMap[newX][newY]) {
            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    // Optionally provide accessors for xp, dmg, vis
    public int getDmg() { return dmg; }
    public int getXp() { return xp; }
    public int getVis() { return vis; }
}