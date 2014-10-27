/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.*;

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
        Projectile proj = e.getEntity();
        LivingEntity shooter = proj.getShooter();

        if (proj instanceof Snowball && shooter instanceof Player) {
            Snowball snowball = (Snowball) proj;
            Player player = (Player) shooter;

            PlayerInventory inv = player.getInventory();
            int heldSlot = inv.getHeldItemSlot();
            int overSlot = heldSlot + 27;
            ItemStack over = inv.getItem(overSlot);

            SnowballLogic logic = SnowballLogic.createLogic(snowball, over.getType());

            if (logic != null) {
                logic.start();
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();

        if (proj instanceof Snowball) {
            Snowball snowball = (Snowball) proj;
            SnowballLogic logic = SnowballLogic.getLogic(snowball);

            if (logic != null) {
                try {
                    logic.hit();
                } finally {
                    logic.end();
                }
            }
        }
    }
}
