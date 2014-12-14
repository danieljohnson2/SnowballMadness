/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * RespawnInfo tracks when players have respawned and gives them a little boost
 * if they are respawning too fast. The spawn area can be destroyed very
 * completely, and this tries to help players spawn in if its not survivable.
 *
 * @author DanJ
 */
public final class RespawnInfo {

    /**
     * This is the maximum number of milliseconds between consecutive respawns;
     * if you respawn faster than this, we intervene by flinging you about.
     */
    private static final long flingRespawnMillis = 120000;
    /**
     * This holds onto a player info for every player who has respawned, but
     * might be respawning too fast.
     */
    private static final Map<Player, RespawnInfo> infos = new WeakHashMap<Player, RespawnInfo>();
    private long lastRespawnTime;
    private int failedRespawnCount;

    /**
     * This method handles bad respawns; call this every time a player respawns.
     * If he is respawning too quickly, this gives him a boost to the side and
     * up, so he'll hopefully land somewhere survivable.
     *
     * @param player The player who has just respawned.
     */
    public static void checkRespawn(Player player, Plugin plugin) {
        long now = System.currentTimeMillis();
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

    private static void startFling(final Player player, final int respawnCount, Plugin plugin) {
        final Vector boost = Vector.getRandom().add(new Vector(-0.5, 0.0, -0.5)).
                setY(0).
                normalize().
                multiply(6).
                setY(2);

        player.setVelocity(boost);

        new BukkitRunnable() {
            private final int runLimit = respawnCount * 6;
            private int runCount = 0;
            private double deltaY = 1.0;

            @Override
            public void run() {
                ++runCount;
                if (runCount >= runLimit) {
                    cancel();
                } else if (runCount > 4 && player.isOnGround()) {
                    cancel();
                } else if (player.isDead()) {
                    cancel();
                } else {
                    player.setFallDistance(0);
                    player.setFireTicks(0);
                    player.setVelocity(boost.clone().setY(deltaY));
                    deltaY *= 0.75;
                }
            }
        }.runTaskTimer(plugin, 10, 10);
    }
}
