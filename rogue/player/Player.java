package rogue.player;

import rogue.map.*;
import rogue.items.*;

public class Player {
    private int x, y;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int xp;
    private int level;
    public int strength;
    //public Weapon equippedWeapon;

    private static final int[] XP_LEVELS = {0, 300, 900, 2700, 6500, 14000}; // 6 total levels

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.maxHp = 100; // starting HP
        this.hp = maxHp;
        this.maxMp = 20;
        this.mp = 0;
        this.xp = 0;
        this.level = 1;
        this.strength = 100 ;//starting strength
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getXp() { return xp; }
    public int getLevel() { return level;}

    public int getDmg() {
        int dmg; 
        // later will be something like dmg = strength + intelligence + level 
        dmg = strength;// + weapon.getDamageBonus(); 
        return dmg;
    }

    public void move(int key, GameMap currentMap) {
        int dx = 0, dy = 0;
        switch (key) {
        case 87: dy = -1; break;    //W
        case 83: dy = 1; break;     //s
        case 65: dx = -1; break;    //A            
        case 68: dx = 1; break;     //D
        }        

        int newX = getX() + dx;
        int newY = getY() + dy;
        if (newX >= 0 && newX < currentMap.getWidth() &&
        newY >= 0 && newY < currentMap.getHeight() &&
        currentMap.getTile(newX, newY).getType() == TileType.FLOOR) {
        
        x += dx;
        y += dy;
        Tile tile = currentMap.getTile(x,y);
        Item item = tile.getItem();
        if ( item != null){ // if a tile has an item ! then            
            if (item instanceof Consumable ) {
                item.onPickup(this);
                tile.clearItem();
            }
        }


        }
    }

    public void setPosition(int x, int y) {
        this.x = x; this.y = y;
    }

    /*public void takeDamage(int dmg) {
        hp -= dmg;
        hp = Math.max(0, hp);
    }*/

    public boolean isDead() {
        return hp <= 0;
    }

    public void addXp(int amount){
        //---------------- will work here later -----------------------
        xp += amount;
        // Level up check
        if (level < XP_LEVELS.length && xp >= XP_LEVELS[level]) {
            level++;
            maxHp += 30; // example every level +10 HP
            hp = maxHp;  // full heal at level up
            strength += 10; // will work here also depends 
        }
    }

    public void Rest(){
        hp += maxHp*0.05;
        mp += maxMp *0.05;
        hp = Math.min(maxHp, hp);
        mp = Math.min(maxMp, mp);
        //5% spawn enemy
    }

    public void restoreHP(int amount) {
    hp = Math.min(maxHp, hp + amount); //restore health from potion
    }

    public void restoreMP(int amount) {
        mp = Math.min(maxMp, mp + amount); //restore mana from potion 
    }   

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount); // take damage for traps
    }
    /*
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getTotalDamage() {
        return baseDamage + (equippedWeapon != null ? equippedWeapon.getDamageBonus() : 0); // NEED FIX
    }*/

}