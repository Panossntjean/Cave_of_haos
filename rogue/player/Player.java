package rogue.player;

import rogue.map.*;
import rogue.items.*;
import rogue.items.weapons.*;
import rogue.enemies.*;
import rogue.ui.*;
import rogue.items.consumables.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

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


    private static final int[] XP_LEVELS = {0, 300, 900, 2700, 6500, 14000}; // 6 total levels

    private static final int[] Warrior_Base_Hp = {10000,20000,30000,40000,50000,60000};//30, 60, 80, 90, 100, 140};
    private static final int[] Warrior_Base_Strength = {1000,1000,1000,1000,1000,1000};//10, 20, 20, 30, 35, 45};

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
            //this.dmg = strength;

            equipWeapon(new WoodenSword());
        } else { // MAGE
            this.baseMaxHp = Mage_Base_Hp[0];
            this.baseMaxMp = Mage_Base_Mp[0]; 
            this. baseStrength = 0;       
            this.baseIntelligence = Mage_Base_Intelligence[0];
            //initialize first weapon for mage

            this.equippedWeapon = null;
            recalcStatsFromEquipment();
            this.hp = this.maxHp;
            this.mp = this.maxMp;
            //this.dmg = intelligence;
            
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
       //int dmg; 
        // later will be something like dmg = strength + intelligence + level 
        if (playerClass == PlayerClass.WARRIOR){
            return strength;
        } else {
            return intelligence;
        }
    }

    public void move(int key, GameMap currentMap, GameLogPanel logPanel,List<Enemy> enemies) {
        int dx = 0, dy = 0;
        /*switch (key) {
        case 87: dy = -1; break;    //W
        case 83: dy = 1; break;     //s
        case 65: dx = -1; break;    //A            
        case 68: dx = 1; break;     //D
        }*/
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

        boolean enemyPresent = false;
        for (Enemy e : enemies) {
            if (e.getX() == newX && e.getY() == newY) {
                enemyPresent = true;
                break;
            }
        }

        
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
                    logPanel.addMessage("Stepped on a  " + item.getName()); // have to make it on the screen. 
                    //maybe want to delete the trap from inventory small mistake but for now is not a big issue 
                }
                else{
                    //logPanel.addMessage("Picked up " + item.getName()); // have to make it on the screen. 
                }
                tile.clearItem();
            }
        }


        }
    }

    public void attack(List<Enemy> enemies, GameMap currentMap, GameLogPanel logPanel) {
        if (playerClass == PlayerClass.WARRIOR) {
            Enemy weakest = findWeakestAdjacentEnemy(enemies);
            if (weakest != null) {
                weakest.takeDamage(this.dmg + strength);
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
                closest.takeDamage(this.dmg + intelligence);
                this.mp -= 5;
                logPanel.addMessage("Battle with " + closest.getName() + "! Player -"+ closest.getDmg() +"HP, Enemy -"+ getDmg() +" HP.");
                //will see if we put something here
                //logpanel etc 
                //System.out.println(name + " casts spell on " + closest.getName() + " for " + (dmg + intelligence) + " damage!");
            }
        }
        


    }

    //maybe here?
    private Enemy findWeakestAdjacentEnemy(List<Enemy> enemies) {
        /* ... */ 
        Enemy weakest = null;
        int lowestHp = Integer.MAX_VALUE;

        for (Enemy e : enemies) {
            int ex = e.getX();
            int ey = e.getY();

            // check if he is 1 tile away
            if (Math.abs(ex - this.x) <= 1 && (Math.abs(ey - this.y) <= 1 )) {

                if (e.getHp() < lowestHp) {
                    weakest = e;
                    lowestHp = e.getHp();
                }
            }
        }

        return weakest;
    }

    private Enemy findClosestVisibleEnemy(List<Enemy> enemies,GameMap currentMap) { 
        /* ... use visibility + distance */
        Enemy closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Enemy e : enemies) {
            int dx = Math.abs(e.getX() - this.x);
            int dy = Math.abs(e.getY() - this.y);
            int distance = dx + dy;

            //if it's visible and inside range
            if (distance <= 6 && currentMap.getVisibilityState(e.getX(), e.getY()) == GameMap.VisibilityState.VISIBLE) {
                if (distance < minDistance) {
                    closest = e;
                    minDistance = distance;
                }
            }
        }

        return closest;
    }


    public void setPosition(int x, int y) {
        this.x = x; this.y = y;
    }

    
    public boolean isDead() {
        return hp <= 0;
    }

    public void addXp(int amount){
        //---------------- will work here later -------------------
        xp += amount;
        // Level up check
        if (level < XP_LEVELS.length && xp >= XP_LEVELS[level]) {            
            
            if (playerClass == PlayerClass.WARRIOR){
                baseMaxHp = Warrior_Base_Hp[level]; // example every level +10 HP                
                baseStrength = Warrior_Base_Strength[level]; // will work here also depends        
            }else{
                baseMaxHp = Mage_Base_Hp[level]; // example every level +10 HP                
                baseMaxMp = Mage_Base_Mp[level];
                mp = baseMaxMp;
                baseIntelligence = Mage_Base_Intelligence[level]; // will work here also depends 
            }
            hp = baseMaxHp;  // full heal at level up
            level++;
            recalcStatsFromEquipment();
        }    
    }

    public void Rest(){
        //hp += maxHp*0.05;
        //mp += maxMp *0.05;
        hp = Math.min(maxHp, (int)Math.round(hp + maxHp * 0.05));
        mp = Math.min(maxMp, (int)Math.round(mp + maxMp * 0.05));
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
    
    private List<Consumable> inventory = new ArrayList<>();

    public void addConsumable(Consumable c) {
        inventory.add(c);
    }

    public boolean useHealthPotion() {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) instanceof HealthPotion) {
                inventory.get(i).onPickup(this);
                inventory.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean useManaPotion() {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) instanceof ManaPotion) {
                inventory.get(i).onPickup(this);
                inventory.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getPotionSummary() {
        int health = 0;
        int mana = 0;

        for (Consumable c : inventory) {
            if (c instanceof HealthPotion) health++;
            else if (c instanceof ManaPotion) mana++;
        }

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
        //logPanel.addMessage("Picked up  " + equippedWeapon.name);
        // bonus / boosts calculate here 
        /* 
        maxHp += newWeapon.getHpBoost(); //need work here what if a player keeps picking up the weapon then he gets unlimited boost pakal
        hp = maxHp;        
        maxMp += newWeapon.getMpBoost();
        mp = maxMp;
        strength += newWeapon.getStrengthBoost();
        intelligence += newWeapon.getIntelligenceBoost();
        */
        //System.out.println("Equipped " + newWeapon.getName());
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

    public void winGame(){
        GameLogPanel logPanel = new GameLogPanel();
        logPanel.addMessage("You obtained the Gem of Judgement!!!");
        logPanel.addMessage("You Won!!!");
        System.exit(0);
    }
    
}