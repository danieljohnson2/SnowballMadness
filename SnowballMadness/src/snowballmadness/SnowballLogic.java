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
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;

/**
 * This class is the base class that hosts the logic that triggers when a
 * snowball hits a target.
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    private final static WeakHashMap<Snowball, SnowballLogic> inFlight = new WeakHashMap<Snowball, SnowballLogic>();
    private Snowball snowball;

    /**
     * This provides the world the snowball is in.
     *
     * @return The world the snowball is in.
     */
    public final World getWorld() {
        return snowball.getWorld();
    }

    /**
     * This returns the snowball being tracked.
     *
     * @return The snowball whose logic this is.
     */
    public final Snowball getSnowball() {
        if (snowball == null) {
            throw new IllegalStateException("A SnwoballLogic must be given a snowball before it can be used.");
        }

        return snowball;
    }

    private void setSnowball(Snowball newSnowball) {
        this.snowball = newSnowball;
    }

    /**
     * This is called when the snowball is launcher.
     */
    public void launch() {
    }

    /**
     * This is called when the snowball hits something.
     */
    public void hit() {
    }

    /**
     * This method handles a projectile launcher; it selects a logic and runs
     * its launch method.
     *
     * @param e The event data.
     */
    public static void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        LivingEntity shooter = proj.getShooter();

        if (proj instanceof Snowball && shooter instanceof Player) {
            Snowball snowball = (Snowball) proj;
            Player player = (Player) shooter;

            PlayerInventory inv = player.getInventory();
            int heldSlot = inv.getHeldItemSlot();
            
            InventorySlice slice = InventorySlice.fromSLot(inv, heldSlot).skip(1);

            SnowballLogic logic = createLogic(slice);

            if (logic != null) {
                try {
                    logic.setSnowball(snowball);
                    logic.start();
                    logic.launch();
                } finally {
                    logic.setSnowball(null);
                }
            }
        }
    }

    /**
     * This method handles a projectile hit event, and runs the hit method.
     *
     * @param e The event data.
     */
    public static void onProjectileHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();

        if (proj instanceof Snowball) {
            Snowball snowball = (Snowball) proj;
            SnowballLogic logic = getLogic(snowball);

            if (logic != null) {
                try {
                    logic.setSnowball(snowball);
                    logic.hit();
                } finally {
                    logic.end();
                    logic.setSnowball(null);
                }
            }
        }
    }

    /**
     * This returns the logic for a snowball that has one.
     *
     * @param snowball The snowball of interest.
     * @return The logic of the snowball, or none if it is an illogical
     * snowball.
     */
    private static SnowballLogic getLogic(Snowball snowball) {
        return inFlight.get(snowball);
    }

    /**
     * This method registers the logic so getLogic() can find it. Logics only
     * work once started.
     */
    public void start() {
        inFlight.put(getSnowball(), this);
        Bukkit.getLogger().info(String.format("Snowball launched: %d", inFlight.size()));
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is
     * done when snowball hits something.
     */
    public void end() {
        inFlight.remove(getSnowball());
        Bukkit.getLogger().info(String.format("Snowball hit: %d", inFlight.size()));
    }

    /**
     * This method creates a new logic, but does not start it. It chooses the
     * logic based on 'hint', which is the stack immediately above the snowball
     * being thrown.
     *
     * @param snowball The snowball whose logic this will be.
     * @param hint The stack above the snowball in the inventory; may be null.
     * @return The new logic, not yet started, or null if the snowball will be
     * illogical.
     */
    public static SnowballLogic createLogic(InventorySlice slice) {
        if (slice.isEmpty()) {
            return null;
        }

        ItemStack hint = slice.get(0);

        if (hint == null) {
            return null;
        }

        switch (hint.getType()) {
            case TNT:
                return new TNTSnowballLogic(4);

            case SULPHUR:
                return new TNTSnowballLogic(1);

            case FIREWORK:
                return new JetbackSnowballLogic();

            case SPIDER_EYE:
                return new InvertedSnowballLogic();

            default:
                return null;
        }
    }
}
