/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;

public class RegenChunkOnlySnowballLogic extends SnowballLogic {

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

            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Snowball) {
                    entity.remove();
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) entity;
                    living.setHealth(living.getMaxHealth());
                }
                //with the grass block or regen potions, we make everything in the chunk be full health
                //whether we've hit them with the snowball or not. Note that we're not stripping inventories here.
                //we are going to wipe all the snowballs. The snowballs have been getting stuck.
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
     * This method checks to see if we can safely regenerate a chunk. We keep a weak map of timeouts, and we must be after this
     * time to do so. This method also updates that map with a new time, to be a second from now. This is changed because the
     * player case now strips inventories, making it impossible to fire rapidly: but a mod in creative mode can rapidfire regen if
     * desired. Multipliers are not recommended though!
     *
     * @param chunk The chunk to check.
     * @return True if we should regenerate the chunk.
     */
    private static boolean checkRegenTimer(Chunk chunk) {
        Long key = chunk.getX() | ((long) chunk.getZ()) << 32;
        return cooldown.check(key);
    }
}
