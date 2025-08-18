package rogue.enemies;
import rogue.items.*;

public interface Enemy {
    int getX();
    int getY();
    void setPosition(int x, int y);
    String getName();

    int getHp();
    void takeDamage(int dmg);
    boolean isDead();

    void takeTurn(int playerX, int playerY, boolean[][] floorMap, boolean[][] occupied);

    int getXp();
    int getDmg();
    int getVis();
    Item drop();
}

