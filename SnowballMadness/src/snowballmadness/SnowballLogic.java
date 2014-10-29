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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class is the base class that hosts the logic that triggers when a
 * snowball hits a target.
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    ////////////////////////////////////////////////////////////////
    // Logic
    //
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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    ////////////////////////////////////////////////////////////////
    // Snowball Information
    //
    private Snowball snowball;
    private LivingEntity shooter;

    /**
     * This provides the world the snowball is in. This works only during calls
     * to launch() and hit() and such; at other times it throws.
     *
     * @return The world the snowball is in.
     * @throws IllegalStateException If the logic is not prepared for use.
     */
    public final World getWorld() {
        return snowball.getWorld();
    }

    /**
     * The player that fires off this snowball. In cases where extra snowballs
     * get spawned, this is the player who started the cascade of nonsense. In
     * such cases getSnowball().getShooter() will be null, and you must use this
     * method. This works only during calls to launch() and hit() and such; at
     * other times it throws.
     *
     * @return The snowball's shooter.
     * @throws IllegalStateException If the logic is not prepared for use.
     */
    public final LivingEntity getShooter() {
        if (shooter == null) {
            throw new IllegalStateException("A SnwoballLogic must be given a shooter before it can be used.");
        }

        return shooter;
    }

    /**
     * This returns the snowball being tracked. This works only during calls to
     * launch() and hit() and such; at other times it throws.
     *
     * @return The snowball whose logic this is.
     * @throws IllegalStateException If the logic is not prepared for use.
     */
    public final Snowball getSnowball() {
        if (snowball == null) {
            throw new IllegalStateException("A SnwoballLogic must be given a snowball before it can be used.");
        }

        return snowball;
    }

    /**
     * This method sets the shooter; we can't just use the value from the
     * snowball itself, because we can fire snowballs with no real shooter, and
     * we need a way to provide a fake one.
     *
     * @param shooter The shooter to return from getShooter().
     */
    protected void setShooter(LivingEntity shooter) {
        this.shooter = shooter;
    }

    /**
     * This method sets the snowball; we associate the logic with the snowball
     * when in use, but then reset it afterwards. This will allow the snowball
     * to be garbage-collected.
     *
     * @param snowball The snowball to associate with the logic.
     */
    protected void setSnowball(Snowball snowball) {
        this.snowball = snowball;
    }

    /**
     * This method is used by the AmplifiedSnowballLogic to amplify the effect
     * of another logic.
     *
     * @param amplification A multiplier to apply to the logic.
     */
    protected void applyAmplification(double amplification) {
    }

    ////////////////////////////////////////////////////////////////
    // Creation
    //
    /**
     * This method creates a new logic, but does not start it. It chooses the
     * logic based on 'hint', which is the stack immediately above the snowball
     * being thrown.
     *
     * @param slice The inventory slice above the snowball in the inventory.
     * @return The new logic, not yet started or attached to a snowball, or null
     * if the snowball will be illogical.
     */
    public static SnowballLogic createLogic(InventorySlice slice) {
        ItemStack hint = slice.getBottomItem();

        if (hint == null) {
            return null;
        }

        switch (hint.getType()) {
            case ARROW:
                return new ProjectileSnowballLogic(Arrow.class);

            case TNT:
                return new TNTSnowballLogic(4);

            case SULPHUR:
                return new TNTSnowballLogic(1);

            case FIREWORK:
                return new JetbackSnowballLogic();

            case SPIDER_EYE:
                return new ReversedSnowballLogic();

            case GLOWSTONE_DUST:
                return new AmplifiedSnowballLogic(1.25, slice.skip(1));

            case GLOWSTONE:
                return new AmplifiedSnowballLogic(1.5, slice.skip(1));

            case SNOW_BALL:
                return new MultiplierSnowballLogic(hint.getAmount(), slice.skip(1));

            default:
                return null;
        }
    }
    ////////////////////////////////////////////////////////////////
    // Event Handling
    //

    /**
     * This method processes a new snowball, executing its launch() method and
     * also recording it so the hit() method can be called later.
     *
     * The shooter may be provided as well; this allows us to launch snowballs
     * from places that are not a player, but associated it with a player
     * anyway.
     *
     * @param inventory The inventory slice that determines the logic type.
     * @param snowball The snowball to be launched.
     * @param shooter The shooter who launched the snowball.
     * @param amplification The amplification to apply to the logic before
     * using it.
     * @return The logic associated with the snowball; may be null.
     */
    public static SnowballLogic performLaunch(InventorySlice inventory, Snowball snowball, LivingEntity shooter, double amplification) {
        SnowballLogic logic = createLogic(inventory);

        if (logic != null) {
            try {
                logic.setShooter(shooter);
                logic.setSnowball(snowball);
                logic.applyAmplification(amplification);
                logic.start();

                Bukkit.getLogger().info(String.format("Snowball launched: %s [%d]", logic, inFlight.size()));

                logic.launch();
            } finally {
                logic.setSnowball(null);
            }
        }

        return logic;
    }

    /**
     * This method processes the impact of a snowball, and invokes the hit()
     * method on its logic object, if it has one.
     *
     * @param snowball The impacting snowball.
     */
    public static void performHit(Snowball snowball) {
        SnowballLogic logic = getLogic(Preconditions.checkNotNull(snowball));

        if (logic != null) {
            try {
                Bukkit.getLogger().info(String.format("Snowball hit: %s [%d]", logic, inFlight.size()));
                logic.hit();
            } finally {
                logic.end();
                logic.unget();
            }
        }
    }

    /**
     * This method handles a projectile launcher; it selects a logic and runs
     * its launch method.
     *
     * @param e The event data.
     */
    public static void onProjectileLaunch(Plugin plugin, ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        LivingEntity shooter = proj.getShooter();

        if (proj instanceof Snowball && shooter instanceof Player) {
            Snowball snowball = (Snowball) proj;
            Player player = (Player) shooter;

            PlayerInventory inv = player.getInventory();
            int heldSlot = inv.getHeldItemSlot();
            ItemStack sourceStack = inv.getItem(heldSlot);

            if (sourceStack == null || sourceStack.getType() == Material.SNOW_BALL) {
                InventorySlice slice = InventorySlice.fromSlot(inv, heldSlot).skip(1);
                SnowballLogic logic = performLaunch(slice, snowball, shooter, 1.0);

                if (logic != null) {
                    replenishSnowball(plugin, inv, heldSlot);
                }
            }
        }
    }

    /**
     * This method increments the number of snowballs in the slot indicated; but
     * it does this after a brief delay since changes made during the launch are
     * ignored. If the indicated slot contains something that is not a snowball,
     * we don't update it. If it is empty, we put one snowball in there.
     *
     * @param plugin The plugin, used to schedule the update.
     * @param inventory The inventory to update.
     * @param slotIndex The slot to update.
     */
    private static void replenishSnowball(Plugin plugin, final PlayerInventory inventory, final int slotIndex) {

        // ugh. We must delay the inventory update or it won't take.
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack replacing = inventory.getItem(slotIndex);

                if (replacing == null) {
                    inventory.setItem(slotIndex, new ItemStack(Material.SNOW_BALL));
                } else if (replacing.getType() == Material.SNOW_BALL) {
                    int oldCount = replacing.getAmount();
                    int newCount = Math.min(16, oldCount + 1);

                    if (oldCount != newCount) {
                        inventory.setItem(slotIndex, new ItemStack(Material.SNOW_BALL, newCount));
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }

    /**
     * This method handles a projectile hit event, and runs the hit method.
     *
     * @param e The event data.
     */
    public static void onProjectileHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();

        if (proj instanceof Snowball) {
            performHit((Snowball) proj);
        }
    }
    ////////////////////////////////////////////////////////////////
    // Logic Association
    //
    private final static WeakHashMap<Snowball, SnowballLogic> inFlight = new WeakHashMap<Snowball, SnowballLogic>();

    /**
     * This returns the logic for a snowball that has one. This also attaches
     * the snowball to it; callers should use unget() when done with the
     * snowball to break this link, because we want the snowball to remain
     * collectible by the GC.
     *
     * @param snowball The snowball of interest; can be null.
     * @return The logic of the snowball, or none if it is an illogical snowball
     * or it was null.
     */
    private static SnowballLogic getLogic(Snowball snowball) {
        if (snowball != null) {
            SnowballLogic logic = inFlight.get(snowball);

            if (logic != null) {
                logic.setSnowball(snowball);
                return logic;
            }
        }

        return null;
    }

    /**
     * unget() reverses the effects of getLogic() on the snowball; removing the
     * link to the snowball itself. We do this to make the snowball collectible
     * by the GC, so we don't keep it alive forever.
     */
    private void unget() {
        setSnowball(null);
    }

    /**
     * This method registers the logic so getLogic() can find it. Logics only
     * work once started.
     */
    public void start() {
        inFlight.put(getSnowball(), this);
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is
     * done when snowball hits something.
     */
    public void end() {
        inFlight.remove(getSnowball());
    }
}
