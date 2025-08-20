package rogue.enemies;

import java.util.Random;
import rogue.items.*;
import rogue.items.consumables.*;

public abstract class AbstractEnemy implements Enemy {
    protected int x, y;
    protected int hp;
    protected int dmg;
    protected int xp;
    protected int vis;
    private final Random random = new Random();

    //@Override public String getName() {return }
    @Override public int getX() { return x; }
    @Override public int getY() { return y; }
    @Override public void setPosition(int x, int y) { this.x = x; this.y = y; }
    @Override public int getHp() { return hp; }
    @Override public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    @Override public boolean isDead() { return hp <= 0; }

    @Override
    public void takeTurn(int playerX, int playerY, boolean[][] floorMap, boolean[][] occupied) {
        int distX = Math.abs(playerX - x);
        int distY = Math.abs(playerY - y);
        double distance = Math.sqrt(distX * distX + distY * distY);

        if (distance > vis) return;

        // if enemy reaches player don't move just attack 
        if (distX <= 1 && distY <= 1){
            // Do damage logic here (you'll likely call a method on the player from outside this class)
            // For now, we just don't move.
            return;
        }

        int dx = Integer.compare(playerX, x);
        int dy = Integer.compare(playerY, y);

        if (distX > distY) {
            tryMove(x + dx, y, floorMap,occupied,playerX,playerY);
        } else if (distY > distX) {
            tryMove(x, y + dy, floorMap,occupied,playerX,playerY);
        } else {
            if (distX > 1 && distY > 1){ // don't step on player 
                if (random.nextBoolean()) {
                    if (!tryMove(x + dx, y, floorMap,occupied,playerX,playerY)) tryMove(x, y + dy, floorMap,occupied,playerX,playerY);
                } else {
                    if (!tryMove(x, y + dy, floorMap,occupied,playerX,playerY)) tryMove(x + dx, y, floorMap,occupied,playerX,playerY);
                }
            }
        }
    }

    protected boolean tryMove(int newX, int newY, boolean[][] floorMap, boolean[][] occupied,int playerX, int playerY) {
        if (newX == playerX && newY == playerY) {
            return false; //  Don't move onto player
        }
        
        if (newX >= 0 && newX < floorMap.length &&
            newY >= 0 && newY < floorMap[0].length &&
            floorMap[newX][newY] &&
            !occupied[newX][newY] ) {


            occupied[x][y] = false;     // free old
            occupied[newX][newY] = true; // take new

            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    @Override public int getDmg() { return dmg; }
    @Override public int getXp() { return xp; }
    @Override public int getVis() { return vis; }

    @Override public Item drop() {
        // 25% chance to drop a consumable
        if (Math.random() < 0.25) {
            if (Math.random() < 0.5) {
                return new HealthPotion();
            } else {
                return new ManaPotion();
            }
        }
        return null; // No drop
    }
}