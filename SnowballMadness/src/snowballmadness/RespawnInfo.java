/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * RespawnInfo tracks when players have respawned and gives them a little boost if they are respawning too fast. The spawn area
 * can be destroyed very completely, and this tries to help players spawn in if its not survivable.
 *
 * @author DanJ
 */
public final class RespawnInfo {

    /**
     * This is the maximum number of milliseconds between consecutive respawns; if you respawn faster than this, we intervene by
     * flinging you about.
     */
    private static final long flingRespawnMillis = 10000;
    /**
     * This holds onto a player info for every player who has respawned, but might be respawning too fast.
     */
    private static final Map<Player, RespawnInfo> infos = new WeakHashMap<Player, RespawnInfo>();
    private long lastRespawnTime;
    private int failedRespawnCount;

    /**
     * This method handles bad respawns; call this every time a player respawns. If he is respawning too quickly, this gives him a
     * boost to the side and up, so he'll hopefully land somewhere survivable.
     *
     * @param player The player who has just respawned.
     */
    public static void checkRespawn(Player player, Plugin plugin) {
        long now = System.currentTimeMillis();
        double randX;
        double randZ;
        boolean foundSpawn;
        Block block; //we'll just keep using this one

        /**
         * before doing the respawn fling testing, we will override the player's spawn location. We are going to check the
         * immediate vicinity of the spawn location for solid ground that doesn't include any bad blocks.
         */
        foundSpawn = false;
        
        if (player.hasPlayedBefore()) {
            foundSpawn = true;
            //crazy random only on first logon ever
        }
        
        while (foundSpawn == false) {
            randX = -1000 + Math.random() * 2001;
            randZ = -1000 + Math.random() * 2001;
            World world = player.getWorld();
            Location loc = new Location(player.getWorld(), randX, 127, randZ);
            world.getChunkAt(loc).load();
            block = loc.getBlock();
            if (world.getEnvironment().equals(Environment.NETHER)) {
                while (block.getType() != Material.AIR && block.getY() > 1) {
                    loc.setY(loc.getY() - 1);
                    block = loc.getBlock(); //step down until air is reached if nether
                }
            }
            while (block.getType() == Material.AIR && block.getY() > 1) {
                loc.setY(loc.getY() - 1);
                block = loc.getBlock();
            }
            //step down until we've hit anything that's not air (or fallen into the void)
            //now we've hit something or our Y is 1
            if (block.getY() > 1) {
                //only if our Y is greater than one, do we even check to see that the ground is valid
                //otherwise we'll just try again
                Material material = block.getType();
                if (material == Material.WATER
                        || material == Material.STATIONARY_WATER
                        || material == Material.LAVA
                        || material == Material.STATIONARY_LAVA
                        || material == Material.WEB
                        || material == Material.TNT
                        || material == Material.FIRE
                        || material == Material.CACTUS
                        || material == Material.PORTAL
                        || material == Material.ENDER_PORTAL) {
                    //it's a bad thing we hit! we leave foundSpawn as false!                
                } else {
                    loc.setY(loc.getY() + 1.5);
                    //step back up into the air again
                    foundSpawn = true; //score!
                    player.setFallDistance(0);
                    player.teleport(loc);
                    player.setVelocity(new Vector(0.0, 0.3, 0.0));
                    block = loc.getBlock(); //now let's touch the ground blocks below
                    while (block.getY() > 1) {
                        player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                        loc.setY(loc.getY() - 1);
                        block = loc.getBlock(); //step down to zero sending changes
                    }
                }
            }
        }
        //if we pass this point, we have a legit random spawn, probably!!


        /*
         * back to DanJ code :D
         */

        RespawnInfo info = infos.get(player);

        if (info == null) {
            info = new RespawnInfo();
            info.lastRespawnTime = now;
            infos.put(player, info);
            return;
        }

        long elapsed = now - info.lastRespawnTime;
        info.lastRespawnTime = now;

        if (elapsed < flingRespawnMillis) {
            info.failedRespawnCount++;

            Bukkit.getLogger().info(String.format(
                    "Player %s spawned %d seconds after last respawn, applying fling.",
                    player.getName(),
                    elapsed / 1000));

            startFling(player, info.failedRespawnCount, plugin);
        } else {
            infos.remove(player);
        }
    }

    /**
     * This method begins the flinging process; it gives the player an initial velocity, and sets up a runnable to periodicity
     * apply the velocity again. We reduce the Y velocity each time to avoid hoisting the player into the stratosphere.
     *
     * We stop this repated boosting process if the player dies, or lands on the ground.
     *
     * @param player THe player that is respawning.
     * @param boostCount The number of times to boost the player after the initial spawn event.
     * @param plugin The plugin; used to schedule boosts.
     */
    private static void startFling(final Player player, final int boostCount, Plugin plugin) {
        final Vector boost = Vector.getRandom().add(new Vector(-0.5, 0.0, -0.5)).
                setY(0).
                normalize().
                multiply(6).
                setY(2);

        player.setVelocity(boost);

        new BukkitRunnable() {
            private int runCount = 0;
            private double deltaY = 1.0;

            @Override
            public void run() {
                if (runCount >= boostCount) {
                    cancel();
                } else if (runCount > 4 && player.isOnGround()) {
                    // we have to wait a bit after the initial spawn, since
                    // the 'is on ground' flag is not immediately updated.
                    cancel();
                } else if (player.isDead()) {
                    cancel();
                } else {
                    player.setFallDistance(0);
                    player.setFireTicks(0);
                    player.setVelocity(boost.clone().setY(deltaY));
                    deltaY *= 0.75;
                    ++runCount;
                }
            }
        }.runTaskTimer(plugin, 10, 10);
    }
}
