package rogue.items.weapons;
import rogue.items.Weapon;
import rogue.player.*;

public class GemOfJudgement extends Weapon {
    public GemOfJudgement() {
        super("Gem of Judgement", 'W', 500, 500, 0, 0);
    }
    
    @Override
    public void onPickup(Player player) {
        super.onPickup(player);
        player.winGame();  // end game
    }
}