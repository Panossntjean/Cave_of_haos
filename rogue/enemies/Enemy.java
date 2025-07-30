package rogue.enemies;

public interface Enemy {
    int getX();
    int getY();
    void setPosition(int x, int y);
    String getName();

    int getHp();
    void takeDamage(int dmg);
    boolean isDead();

    void takeTurn(int playerX, int playerY, boolean[][] floorMap);

    int getXp();
    int getDmg();
    int getVis();
}

