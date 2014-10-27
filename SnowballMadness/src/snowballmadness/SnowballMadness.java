/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
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
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();

        if (projectile instanceof Snowball) {
            World world = projectile.getWorld();
            world.createExplosion(projectile.getLocation(), 4);
        }
    }
}
