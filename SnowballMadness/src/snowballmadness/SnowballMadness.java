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

        if (proj instanceof Snowball) {
            Snowball snowball = (Snowball) proj;
            new TNTSnowballLogic(snowball).start();
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
