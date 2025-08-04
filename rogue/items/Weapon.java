/*public class Weapon extends Item {
    private int damageBonus;
    private int intelligenceBonus;
    private boolean isFinalWeapon;// maybe will not use it
    private WeaponType type;

    public Weapon(String name, char symbol, int damageBonus, int intelligenceBonus, boolean isFinalWeapon) {
        super(name, symbol);
        this.damageBonus = damageBonus;
        this.intelligenceBonus = intelligenceBonus;
        this.hpBonus = hpBonus;
        this.manaBonus = manaBonus;
        this.type = type;
        this.isFinalWeapon = isFinalWeapon;
    }

    @Override
    public void onPickup(Player player) {
        player.equipWeapon(this);
        System.out.println(player.getName() + " equipped " + name + "! (+" + damageBonus + " DMG, +" + intelligenceBonus + " INT)");
        if (isFinalWeapon) {
            player.winGame();
        }
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getIntelligenceBonus() {
        return intelligenceBonus;
    }

    public boolean isFinalWeapon() {
        return isFinalWeapon;
    }

    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = type;
    }
}
*/