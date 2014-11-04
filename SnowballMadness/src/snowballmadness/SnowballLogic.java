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
 * We keep these in a weak hash map, so it is important that this object (and
 * all subclasses) not hold onto a reference to a Snowball, or that snowball may
 * never be collected.
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    ////////////////////////////////////////////////////////////////
    // Logic
    //
    /**
     * This is called when the snowball is launched.
     *
     * @param snowball The snowball being launched.
     * @param info Other information about the snowball.
     */
    public void launch(Snowball snowball, SnowballInfo info) {
    }

    /**
     * This is called when the snowball hits something and returns teh damange
     * to be done (which can be 0).
     *
     * @param snowball The snowball hitting something.
     * @param info Other information about the snowball.
     * @param target The entity that was hit.
     * @param damage The damage the snowball is expected to do..
     * @return The damage teh snowball will do.
     */
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        return proposedDamage;
    }

    /**
     * This is called when the snowball hits something.
     *
     * @param snowball The snowball hitting something.
     * @param info Other information about the snowball.
     */
    public void hit(Snowball snowball, SnowballInfo info) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
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

            case COBBLESTONE:
            case SMOOTH_BRICK:
            case SAND:
            case GRAVEL:
                return new BlockPlacementSnowballLogic(hint.getType());
            //considering adding data values to smooth brick so it randomizes
            //including mossy, cracked and even silverfish
                
            case PUMPKIN:
                return new BlockPlacementSnowballLogic(Material.ENDER_PORTAL);
                
            case SOUL_SAND:
                return new BlockPlacementSnowballLogic(Material.PORTAL);

            case WATER_BUCKET:
                return new BlockPlacementSnowballLogic(Material.WATER);

            case LAVA_BUCKET:
                return new BlockPlacementSnowballLogic(Material.LAVA);

            case WOOD_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
            case DIAMOND_SWORD:
                return new SwordSnowballLogic(slice);

            case TNT:
                return new TNTSnowballLogic(4);

            case SULPHUR:
                return new TNTSnowballLogic(1);

            case FIREWORK:
                return new JetpackSnowballLogic();

            case FLINT_AND_STEEL:
                return new FlintAndSteelSnowballLogic(Material.FIRE);

            case SPIDER_EYE:
                return new ReversedSnowballLogic();

            case SUGAR:
                return new SpeededSnowballLogic(1.5, slice.skip(1));
                
            case CAKE:
                return new SpeededSnowballLogic(3, slice.skip(1));
                //the cake is a... lazor!

            case GLOWSTONE_DUST:
                return new PoweredSnowballLogic(1.5, slice.skip(1));

            case GLOWSTONE:
                return new PoweredSnowballLogic(3, slice.skip(1));

            case SNOW_BALL:
                return new MultiplierSnowballLogic(hint.getAmount(), slice.skip(1));

            case GRASS:
                return new RegenerationSnowballLogic(slice);

            case GHAST_TEAR:
                return new SpawnSnowballLogic(EntityType.GHAST);
                
            case ROTTEN_FLESH:
                return new SpawnSnowballLogic(EntityType.ZOMBIE);
                
            case ENCHANTMENT_TABLE:
                return new SpawnSnowballLogic(EntityType.WITCH);
                
            case GOLD_INGOT:
                return new SpawnSnowballLogic(EntityType.PIG);
                
            case GOLD_BLOCK:
                return new SpawnSnowballLogic(EntityType.PIG_ZOMBIE);

            case STRING:
                return new SpawnSnowballLogic(EntityType.SPIDER, EntityType.CAVE_SPIDER, 1.0);

            case EYE_OF_ENDER:
                return new SpawnSnowballLogic(EntityType.ENDERMAN);
                
            case DRAGON_EGG:
                return new SpawnSnowballLogic(EntityType.ENDER_DRAGON);

            case MILK_BUCKET:
                return new SpawnSnowballLogic(EntityType.COW, EntityType.MUSHROOM_COW, 1.0);

            case SKULL_ITEM:
                SkullType skullType = SkullType.values()[hint.getDurability()];
                return SpawnSnowballLogic.fromSkullType(skullType);

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
     * @param info The info record that describes the snowball.
     * @return The logic associated with the snowball; may be null.
     */
    public static SnowballLogic performLaunch(InventorySlice inventory, Snowball snowball, SnowballInfo info) {
        SnowballLogic logic = createLogic(inventory);

        if (logic != null) {
            logic.start(snowball, info);

            Bukkit.getLogger().info(String.format("Snowball launched: %s [%d]", logic, inFlight.size()));

            logic.launch(snowball, info);
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
        SnowballLogicData data = getData(Preconditions.checkNotNull(snowball));

        if (data != null) {
            try {
                Bukkit.getLogger().info(String.format("Snowball hit: %s [%d]", data.logic, inFlight.size()));
                data.logic.hit(snowball, data.info);
            } finally {
                data.logic.end(snowball);
            }
        }
    }

    public static double performDamage(Snowball snowball, Entity target, double damage) {
        SnowballLogicData data = getData(Preconditions.checkNotNull(snowball));

        if (data != null) {
            Bukkit.getLogger().info(String.format("Snowball damage: %s [%d]", data.logic, inFlight.size()));
            return data.logic.damage(snowball, data.info, target, damage);
        }

        return damage;
    }

    /**
     * This method handles a projectile launch; it selects a logic and runs its
     * launch method.
     *
     * @param e The event data.
     */
    public static void onProjectileLaunch(SnowballMadness plugin, ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        LivingEntity shooter = proj.getShooter();

        if (proj instanceof Snowball && shooter instanceof Player) {
            Snowball snowball = (Snowball) proj;
            Player player = (Player) shooter;

            PlayerInventory inv = player.getInventory();
            int heldSlot = inv.getHeldItemSlot();
            ItemStack sourceStack = inv.getItem(heldSlot);

            if (sourceStack == null || sourceStack.getType() == Material.SNOW_BALL) {
                InventorySlice slice = InventorySlice.fromSlot(player, heldSlot).skip(1);
                SnowballLogic logic = performLaunch(slice, snowball, new SnowballInfo(plugin));

                if (logic != null && player.getGameMode() != GameMode.CREATIVE) {
                    replenishSnowball(plugin, inv, heldSlot);
                }
            }
        }
    }

    /**
     * This method handles the damage a snowball does on impact, and can adjust
     * that damage.
     *
     * @param e The damage event.
     */
    public static void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity damagee = e.getEntity();
        Entity damager = e.getDamager();
        double damage = e.getDamage();

        if (damager instanceof Snowball) {
            double newDamage = performDamage((Snowball) damager, damagee, damage);

            if (newDamage != damage) {
                e.setDamage(newDamage);
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
    private final static WeakHashMap<Snowball, SnowballLogicData> inFlight = new WeakHashMap<Snowball, SnowballLogicData>();

    /**
     * this class just holds the snowball logic and info for a snowball; the
     * snowball itself must not be kept here, as this is the value of a
     * weak-hash-map keyed on the snowballs. We don't want to keep them alive.
     */
    private final static class SnowballLogicData {

        public final SnowballLogic logic;
        public final SnowballInfo info;

        public SnowballLogicData(SnowballLogic logic, SnowballInfo info) {
            this.logic = logic;
            this.info = info;
        }
    }

    /**
     * This returns the logic and shooter for a snowball that has one.
     *
     * @param snowball The snowball of interest; can be null.
     * @return The logic and info of the snowball, or null if it is an illogical
     * snowball or it was null.
     */
    private static SnowballLogicData getData(Snowball snowball) {
        if (snowball != null) {
            return inFlight.get(snowball);
        } else {
            return null;
        }
    }

    /**
     * This method registers the logic so getLogic() can find it. Logics only
     * work once started.
     *
     * @param snowball The snowball being launched.
     * @param info Other information about the snowball.
     */
    public void start(Snowball snowball, SnowballInfo info) {
        inFlight.put(snowball, new SnowballLogicData(this, info));
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is
     * done when snowball hits something.
     *
     * @param snowball The snowball to deregister.
     */
    public void end(Snowball snowball) {
        inFlight.remove(snowball);
    }
}
