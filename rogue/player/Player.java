private int xp;
private int level;

// Tiers για level up (πχ level 1: 0 XP, level 2: 30 XP, level 3: 90 XP, level 4: 270 XP κλπ)
private static final int[] XP_LEVELS = {0, 30, 90, 270, 650, 1400}; // μπορείς να το αλλάξεις

public Player(int startX, int startY) {
    this.x = startX;
    this.y = startY;
    this.level = 1;
    this.xp = 0;
    this.maxHp = 30;
    this.hp = maxHp;
}

public int getXp() { return xp; }
public int getLevel() { return level; }

public void addXp(int amount) {
    xp += amount;
    // Level up check
    if (level < XP_LEVELS.length && xp >= XP_LEVELS[level]) {
        level++;
        maxHp += 10; // πχ κάθε level +10 HP
        hp = maxHp;  // full heal στο level up
    }
}