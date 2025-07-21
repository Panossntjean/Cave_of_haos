package rogue.player;

public class Player {
    private int x, y;
    private int hp;
    private int maxHp;
    private int xp;
    private int level;

    private static final int[] XP_LEVELS = {0, 30, 90, 270, 650, 1400}; // μπορείς να το αλλάξεις


    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.maxHp = 30; // αρχικό HP
        this.hp = maxHp;
        this.xp = 0;
        this.level = 1;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getXp() { return xp; }
    public int getLevel() { return level;}

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void setPosition(int x, int y) {
        this.x = x; this.y = y;
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void addXp(int amount){
        xp += amount;
        // Level up check
        if (level < XP_LEVELS.length && xp >= XP_LEVELS[level]) {
            level++;
            maxHp += 10; // πχ κάθε level +10 HP
            hp = maxHp;  // full heal στο level up
        }
    }

}