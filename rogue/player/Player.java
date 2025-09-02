package rogue.player;

import rogue.map.*;
import rogue.items.*;
import rogue.items.weapons.*;
import rogue.enemies.*;
import rogue.ui.*;
import rogue.items.consumables.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.stream.Collectors;


public class Player {

    private String name;
    private PlayerClass playerClass;
    private int x, y;    
    private int hp, maxHp;
    private int mp, maxMp;
    private int dmg;
    public int strength;
    public int intelligence;
    private int xp;
    private int level;
    private Weapon equippedWeapon;
    private Runnable onWin;


    private static final int[] XP_LEVELS = {0, 300, 900, 2700, 6500, 14000}; // 6 total levels

    private static final int[] Warrior_Base_Hp = {30, 60, 80, 90, 100, 140}; //10000,20000,30000,40000,50000,60000};// test
    private static final int[] Warrior_Base_Strength = {10, 20, 20, 30, 35, 45};//1000,1000,1000,1000,1000,1000};// test

    private static final int[] Mage_Base_Hp = {20, 40, 50, 55, 60, 80};
    private static final int[] Mage_Base_Mp = {30, 50, 70, 90, 110, 140};
    private static final int[] Mage_Base_Intelligence = {10, 20, 30, 40, 50, 70};

    private int  baseMaxHp, baseMaxMp, baseStrength, baseIntelligence;
    

    public Player(String name, PlayerClass playerClass, int startX, int startY) {
        this.name = name;
        this.playerClass = playerClass;
        this.x = startX;
        this.y = startY;        
        this.xp = 0;
        this.level = 1;
        this.onWin =null;
        // initialize 
        if (playerClass == PlayerClass.WARRIOR) { // WARRIOR
            this.baseMaxHp = Warrior_Base_Hp[0];            
            this.baseMaxMp = 0;            
            this.baseStrength = Warrior_Base_Strength[0];
            this.baseIntelligence = 0;
            //initialize first weapon for warrior
            this.equippedWeapon = null;
            recalcStatsFromEquipment();
            this.hp = this.maxHp;
            this.mp = this.maxMp;

            equipWeapon(new WoodenSword());
        } else { // MAGE
            this.baseMaxHp = Mage_Base_Hp[0];
            this.baseMaxMp = Mage_Base_Mp[0]; 
            this.baseStrength = 0;       
            this.baseIntelligence = Mage_Base_Intelligence[0];
            //initialize first weapon for mage

            this.equippedWeapon = null;
            recalcStatsFromEquipment();
            this.hp = this.maxHp;
            this.mp = this.maxMp;
            
            equipWeapon(new ApprenticeStaff());
        }
        this.hp = this.maxHp;
        this.mp = this.maxMp;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getXp() { return xp; }
    public int getLevel() { return level;}

    public String getName() { return name; }
    public PlayerClass getPlayerClass() { return playerClass; }

    public int getDmg() {
        if (playerClass == PlayerClass.WARRIOR){
            return strength;
        } else {
            return intelligence;
        }
    }

    public void move(int key, GameMap currentMap, GameLogPanel logPanel,List<Enemy> enemies) {
        int dx = 0, dy = 0;
        switch (key) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                dy = -1;
                break;

            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                dy = 1;
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                dx = -1;
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                dx = 1;
                break;
        }
        
        
        int newX = getX() + dx;
        int newY = getY() + dy;

        boolean enemyPresent = enemies.stream()
            .anyMatch(e -> e.getX() == newX && e.getY() == newY);

        
        if (!enemyPresent && newX >= 0 && newX < currentMap.getWidth() &&
        newY >= 0 && newY < currentMap.getHeight() &&
        currentMap.getTile(newX, newY).getType() == TileType.FLOOR) {
        
        x += dx;
        y += dy;
        Tile tile = currentMap.getTile(x,y);
        Item item = tile.getItem();
        if ( item != null){ // if a tile has an item ! then            
            if (item instanceof Consumable ) {
                addConsumable((Consumable) item);
                if (item instanceof Trap){
                    restoreHP(-10);
                    logPanel.addMessage("Stepped on a  " + item.getName()); 
                    //maybe want to delete the trap from inventory small mistake but for now is not a big issue 
                }
                tile.clearItem();
            }
        }


        }
    }

    public void attack(List<Enemy> enemies, GameMap currentMap, GameLogPanel logPanel) {
        int dmg = getDmg();
        if (playerClass == PlayerClass.WARRIOR) {
            Enemy weakest = findWeakestAdjacentEnemy(enemies);
            if (weakest != null) {
                weakest.takeDamage(dmg);
                logPanel.addMessage("Battle with " + weakest.getName() + "! Player -"+ weakest.getDmg() +"HP, Enemy -"+ getDmg() +" HP.");
                //will see if we put something here 
                //logpanel etc ... 
                //System.out.println(name + " strikes " + weakest.getName() + " for " + dmg + " damage!");
            }
        } else if (playerClass == PlayerClass.MAGE) {
            if (this.mp < 5) {
                //System.out.println(name + " tried to cast a spell, but lacks mana!");
                logPanel.addMessage("Out of Mana!"); 
                return;
            }
            Enemy closest = findClosestVisibleEnemy(enemies, currentMap);
            if (closest != null) {
                closest.takeDamage(dmg);
                this.mp -= 5;
                logPanel.addMessage("Battle with " + closest.getName() + "! Player -"+ closest.getDmg() +"HP, Enemy -"+ getDmg() +" HP.");
                //logpanel etc 
                //System.out.println(name + " casts spell on " + closest.getName() + " for " + (dmg + intelligence) + " damage!");
            }
        }
        


    }
    

    private Enemy findWeakestAdjacentEnemy(List<Enemy> enemies) {
        return enemies.stream()
            .filter(e -> Math.abs(e.getX() - this.x) < 2
                    && Math.abs(e.getY() - this.y) < 2)
            .min(Comparator.comparingInt(Enemy::getHp))
            .orElse(null);
    }
    


    private Enemy findClosestVisibleEnemy(List<Enemy> enemies, GameMap currentMap) {
        return enemies.stream()
            .filter(e -> {
                int dx = Math.abs(e.getX() - this.x);
                int dy = Math.abs(e.getY() - this.y);
                int distance = dx + dy;
                return distance <= 6 
                    && currentMap.getVisibilityState(e.getX(), e.getY()) == GameMap.VisibilityState.VISIBLE;
            })
            .min(Comparator.comparingInt(e -> 
                Math.abs(e.getX() - this.x) + Math.abs(e.getY() - this.y)))
            .orElse(null);
    }


    public void setPosition(int x, int y) {
        this.x = x; this.y = y;
    }

    
    public boolean isDead() {
        return hp <= 0;
    }

    public void addXp(int amount){
        xp += amount;
        // Level up check
        if (level < XP_LEVELS.length && xp >= XP_LEVELS[level]) {            
            
            if (playerClass == PlayerClass.WARRIOR){
                baseMaxHp = Warrior_Base_Hp[level];               
                baseStrength = Warrior_Base_Strength[level];        
            }else{
                baseMaxHp = Mage_Base_Hp[level];                 
                baseMaxMp = Mage_Base_Mp[level];
                baseIntelligence = Mage_Base_Intelligence[level]; 
            }
            hp = baseMaxHp + equippedWeapon.getHpBoost();  // full heal on level up
            mp = baseMaxMp + equippedWeapon.getMpBoost();  // full mp on level up
            level++;
            recalcStatsFromEquipment();
        }    
    }

    public void Rest(){
        hp = Math.min(maxHp, (int)Math.round(hp + maxHp * 0.05));
        mp = Math.min(maxMp, (int)Math.round(mp + maxMp * 0.05));
        
    }

    public void restoreHP(int amount) {
    hp = Math.min(maxHp, hp + amount); //restore health from potion
    }

    public void restoreMP(int amount) {
        mp = Math.min(maxMp, mp + amount); //restore mana from potion 
    }   

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount); // take damage from traps or enemies
    }
    
    private List<Consumable> inventory = new ArrayList<>();

    public void addConsumable(Consumable c) {
        inventory.add(c);
    }
    

    public boolean useHealthPotion() {
        return inventory.stream()
            .filter(i -> i instanceof HealthPotion)
            .findFirst()
            .map(p -> {
                p.onPickup(this);
                inventory.remove(p);
                return true;
            })
            .orElse(false);
    }

    public boolean useManaPotion() {
        return inventory.stream()
            .filter(i -> i instanceof ManaPotion)
            .findFirst()
            .map(p -> {
                p.onPickup(this);
                inventory.remove(p);
                return true;
            })
            .orElse(false);
    }
        

    public String getPotionSummary() {
        Map<Class<?>, Long> counts = inventory.stream()
            .filter(c -> c instanceof Consumable)
            .collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));

        long health = counts.getOrDefault(HealthPotion.class, 0L);
        long mana   = counts.getOrDefault(ManaPotion.class, 0L);

        return "Potions: " + health + " (H) / " + mana + " (M)";
    }




    public Weapon swapWeapon(Weapon newWeapon) {
        if (newWeapon == this.equippedWeapon) return this.equippedWeapon; // no-op
        Weapon old = this.equippedWeapon;
        this.equippedWeapon = newWeapon;
        recalcStatsFromEquipment();
        return old;
    }

    public void equipWeapon(Weapon newWeapon) {
        if (equippedWeapon != null) {
            // change equipment
            //System.out.println("Dropped " + equippedWeapon.getName());
        }

        this.equippedWeapon = newWeapon;
        recalcStatsFromEquipment();
    }

    

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void recalcStatsFromEquipment() {
        int wHp = 0, wMp = 0, wStr = 0, wInt = 0;
        if (equippedWeapon != null) {
            wHp = equippedWeapon.getHpBoost();
            wMp = equippedWeapon.getMpBoost();
            wStr = equippedWeapon.getStrengthBoost();
            wInt = equippedWeapon.getIntelligenceBoost();
        }

        this.maxHp = Math.max(1, baseMaxHp + wHp);
        this.maxMp = Math.max(0, baseMaxMp + wMp);
        this.strength = Math.max(0, baseStrength + wStr);
        this.intelligence = Math.max(0, baseIntelligence + wInt);

        // Clamp current resources; do NOT auto-heal on equip
        if (hp > maxHp) hp = maxHp;
        if (mp > maxMp) mp = maxMp;
    }


    public void setOnWin(Runnable r) { this.onWin = r; }

    public void winGame(){
        //System.out.println("DEBUG: winGame called");
        if (onWin != null) onWin.run();
        else System.exit(0);    
    }
    
}