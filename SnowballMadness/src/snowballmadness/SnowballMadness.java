package snowballmadness;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is the plug-in class for this mod; it handles events and forwards them
 * to logic objects.
 *
 * @author DanJ
 */
public class SnowballMadness extends JavaPlugin implements Listener {

    private BukkitRunnable ticker;

    @Override
    public void onEnable() {
        super.onEnable();

        getServer().getPluginManager().registerEvents(this, this);

        if (ticker != null) {
            ticker.cancel();
        }

        ticker = new BukkitRunnable() {
            @Override
            public void run() {
                SnowballLogic.onTick();
            }
        };

        ticker.runTaskTimer(this, 4, 4);
    }

    @Override
    public void onDisable() {

        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }

        HandlerList.unregisterAll((JavaPlugin)this);
        super.onDisable();
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        SnowballLogic.onProjectileLaunch(this, e);
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        SnowballLogic.onProjectileHit(e);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SnowballLogic.onEntityDamageByEntityEvent(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        bestowSnowball(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        bestowSnowball(e.getPlayer());
    }

    /**
     * This method gives a player a snowball in a designated snowball slot,
     * provided this slot is empty.
     *
     * @param player The player to be gifted with snow!
     */
    @SuppressWarnings("deprecation")
    private void bestowSnowball(Player player) {
        PlayerInventory inventory = player.getInventory();
        final int slotNumber = 8;

        ItemStack oldStack = inventory.getItem(slotNumber);

        if (oldStack == null || oldStack.getType() == Material.SNOW_BALL) {
            inventory.setItem(slotNumber, new ItemStack(Material.SNOW_BALL, 16));
            player.updateInventory();
        }
    }
}
