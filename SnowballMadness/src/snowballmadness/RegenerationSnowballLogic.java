package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.projectiles.ProjectileSource;

/**
 * This logic regenerates any chunk it lands in, and also heals players it hits.
 * As a bonus gimme, if you are using a dirt block to create this, it will
 * convert that block to grass.
 *
 * @author DanJ
 */
public class RegenerationSnowballLogic extends SnowballLogic {

    private final static CooldownTimer<Long> cooldown = new CooldownTimer<Long>(1000);

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

        Location loc = snowball.getLocation();
        Chunk chunk = loc.getBlock().getChunk();

        if (checkRegenTimer(chunk)) {
            ProjectileSource shooter = snowball.getShooter();
            clearInventory(shooter);

            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Snowball) {
                    entity.remove();
                }
                //we are going to wipe all the snowballs while clearing
                //player inventories. The snowballs have been getting stuck.
                //however, this bit might not be fixing that.
                
                clearInventory(entity);
            }

            chunk.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
            chunk.getWorld().unloadChunk(chunk);
            chunk.getWorld().loadChunk(chunk);
            //further attempts to handle snowballs getting stuck
            //if multiplied multiplier regen snowballs are spammed,
            //the server locks up. Trying to avoid this.
        }
    }

    /**
     * This overload of clearInventory() will clear the inventory of the victim
     * if it is a player, but do nothing if it is not.
     *
     * @param victim The player to clear, or not.
     */
    private static void clearInventory(Object victim) {
        if (victim instanceof Player) {
            clearInventory((Player) victim);
        }
    }

    /**
     * This removes everything from the player given, so they have no items. It
     * even removes armor. It does not affect creative-mode players (like me on
     * my server!)
     *
     * @param victim The player who is to be enlightened.
     */
    private static void clearInventory(Player victim) {
        if (victim.getGameMode() != GameMode.CREATIVE) {
            PlayerInventory playerInv = victim.getInventory();
            playerInv.clear();
            playerInv.setArmorContents(new ItemStack[4]);
        }
    }

    /**
     * This method checks to see if we can safely regenerate a chunk. We keep a
     * weak map of timeouts, and we must be after this time to do so. This
     * method also updates that map with a new time, to be a second from now.
     * This is changed because the player case now strips inventories, making it
     * impossible to fire rapidly: but a mod in creative mode can rapidfire
     * regen if desired. Multipliers are not recommended though!
     *
     * @param chunk The chunk to check.
     * @return True if we should regenerate the chunk.
     */
    private static boolean checkRegenTimer(Chunk chunk) {
        Long key = chunk.getX() | ((long) chunk.getZ()) << 32;
        return cooldown.check(key);
    }
}
