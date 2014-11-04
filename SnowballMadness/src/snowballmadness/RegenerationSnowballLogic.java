/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

/**
 * This logic regenerates any chunk it lands in, and also heals players it hits.
 * As a bonus gimme, if you are using a dirt block to create this, it will
 * convert that block to grass.
 *
 * @author DanJ
 */
public class RegenerationSnowballLogic extends SnowballLogic {

    private final static CooldownTimer<Long> cooldown=new CooldownTimer<Long>(8000);
    private final InventorySlice inventory;

    public RegenerationSnowballLogic(InventorySlice inventory) {
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.setHealth(living.getMaxHealth());
        }

        return 0;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (info.power >= 2.0) {
            Location loc = snowball.getLocation();
            Chunk chunk = loc.getBlock().getChunk();

            if (checkRegenTimer(chunk)) {

                LivingEntity shooter = snowball.getShooter();
                if (shooter instanceof HumanEntity
                        && ((Player) shooter).getGameMode() != GameMode.CREATIVE) {
                    ItemStack bottom = inventory.getBottomItem();

                    if (bottom != null && bottom.getType() == Material.GRASS) {
                        int newAmount = bottom.getAmount() - 1;

                        if (newAmount <= 0) {
                            inventory.set(0, null);
                        } else {
                            bottom.setAmount(newAmount);
                        }
                    } else {
                        // if grass is no longer in inventory, abort. This
                        // prevent multiplier snowballs from doing too much
                        // regen.
                        return;
                    }
                }

                chunk.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
            }
        }
    }

    /**
     * This method checks to see if we can safely regenerate a chunk. We keep a
     * weak map of timeouts, and we must be after this time to do so. This
     * method also updates that map with a new time, to be 8 seconds from now.
     *
     * @param chunk The chunk to check.
     * @return True if we should regenerate the chunk.
     */
    private static boolean checkRegenTimer(Chunk chunk) {
        Long key = chunk.getX() | ((long) chunk.getZ()) << 32;
        return cooldown.check(key);
    }
}
