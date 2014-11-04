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

/**
 * This is the plug-in class for this mod; it handles events and forwards them
 * to logic objects.
 *
 * @author DanJ
 */
public class SnowballMadness extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        getServer().getPluginManager().registerEvents(this, this);
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
    public void x(EntityDamageByEntityEvent e) {
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
