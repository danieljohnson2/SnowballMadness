package snowballmadness;

import java.util.*;
import com.google.common.base.*;

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
     * this is called every many times every second.
     *
     * @param snowball A snowball that gets a chance to do something.
     * @param info Other information about the snowball.
     */
    public void tick(Snowball snowball, SnowballInfo info) {
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
                return new ArrowSnowballLogic(hint);
                
            case FIREWORK_CHARGE:
            case BLAZE_ROD:
            case EGG:
            case EXP_BOTTLE:
                return new ProjectileSnowballLogic(hint.getType());

            case COBBLESTONE:
            case OBSIDIAN:
            case BOOKSHELF:
            case BRICK:
            case SAND:
            case GRAVEL:
                return new BlockPlacementSnowballLogic(hint.getType());
            //considering adding data values to smooth brick so it randomizes
            //including mossy, cracked and even silverfish

            case ENDER_PEARL:
                return new BlockPlacementSnowballLogic(Material.ENDER_CHEST);

            case ANVIL:
                return new AnvilSnowballLogic();

            case DAYLIGHT_DETECTOR:
                return new DaylightFinderSnowballLogic();

            case WATCH:
                return new WatchSnowballLogic();

            case REDSTONE:
                return new StartRainLogic();

            case CACTUS:
                return new StopRainLogic();

            case ENDER_STONE:
                return new BlockPlacementSnowballLogic(Material.ENDER_PORTAL);

            case WATER_BUCKET:
                return new BlockPlacementSnowballLogic(Material.WATER);

            case LAVA_BUCKET:
                return new BlockPlacementSnowballLogic(Material.LAVA);

            case RED_ROSE:
            case YELLOW_FLOWER:
                return new FireworkSnowballLogic(hint);

            case QUARTZ:
            case COAL:
            case COAL_BLOCK:
            case REDSTONE_BLOCK:
            case NETHERRACK:
            case LADDER:
            case VINE:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType());

            case SAPLING:
                return new ArboristSnowballLogic(hint);

            case WOOD_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
            case DIAMOND_SWORD:
                return new SwordSnowballLogic(slice);

            case WOOD_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLD_PICKAXE:
            case DIAMOND_PICKAXE:
                return new PickaxeSnowballLogic(hint.getType());

            case SHEARS:
                return new ShearsSnowballLogic();

            case STICK:
            case BONE:
            case FENCE:
            case COBBLE_WALL:
            case NETHER_FENCE:
                return KnockbackSnowballLogic.fromMaterial(hint.getType());

            case LEAVES:
            case STONE:
            case SMOOTH_BRICK:
            case IRON_FENCE:
            case WEB:
                return new BoxSnowballLogic(hint.getType());
            //all structures that can be broken with any pick, but can be
            //large with use of glowstone. Provides a defensive game

            case GLASS:
                return new BoxSnowballLogic(Material.GLASS, Material.AIR);

            case GLASS_BOTTLE:
                return new SphereSnowballLogic(Material.GLASS, Material.AIR);
            //these two enable a sort of minigame: try not to get encased in glass
            //against other players who are trying to encase you

            case POTION:
                return PotionInfo.fromItemStack(hint).createPotionLogic();

            case BUCKET:
                return new BoxSnowballLogic(Material.AIR);

            case TORCH:
                return new LinkedTrailSnowballLogic(Material.FIRE);

            case FENCE_GATE:
                return new LinkedTrailSnowballLogic(Material.FENCE);

            case CAULDRON_ITEM:
                return new LinkedTrailSnowballLogic(Material.STATIONARY_WATER);

            case WATER_LILY:
                return new LinkedWaterTrailSnowballLogic(Material.WATER_LILY);

            case TNT:
                return new TNTSnowballLogic(4);

            case SULPHUR:
                return new TNTSnowballLogic(2);

            case FIREWORK:
                return new JetpackSnowballLogic();

            case FLINT_AND_STEEL:
                return new FlintAndSteelSnowballLogic(Material.FIRE);

            case SPIDER_EYE:
                return new ReversedSnowballLogic(1);

            case FERMENTED_SPIDER_EYE:
                return new EchoSnowballLogic(hint.getAmount(), slice.skip(1));

            case SUGAR:
                return new SpeededSnowballLogic(1.6, slice.skip(1));

            case BOW:
                return new SpeededSnowballLogic(1.8, slice.skip(1));

            case COOKIE:
                return new SpeededSnowballLogic(2, slice.skip(1));

            case CAKE:
                return new SpeededSnowballLogic(3, slice.skip(1));
            //the cake is a... lazor!

            case BEACON:
                return new SpeededSnowballLogic(4, slice.skip(1));
            //the beacon is the REAL lazor.

            case GLOWSTONE_DUST:
                return new PoweredSnowballLogic(1.5, slice.skip(1));

            case GLOWSTONE:
                return new PoweredSnowballLogic(3, slice.skip(1));

            case NETHER_STAR:
                return new PoweredSnowballLogic(4, slice.skip(1));
            //nuclear option. Beacon/netherstar designed to be insane
            //overkill but not that cost-effective, plus more unwieldy.

            case SNOW_BALL:
                return new MultiplierSnowballLogic(hint.getAmount(), slice.skip(1));

            case SLIME_BALL:
                return new BouncySnowballLogic(hint.getAmount(), slice.skip(1));

            case QUARTZ_BLOCK:
                return new KapwingSnowballLogic(hint.getAmount(), slice.skip(1));

            case GRASS:
            case DIRT:
                return new RegenerationSnowballLogic();

            case GHAST_TEAR:
                return new SpawnSnowballLogic(EntityType.GHAST);

            case ROTTEN_FLESH:
                return new SpawnSnowballLogic(EntityType.ZOMBIE);

            case ENCHANTMENT_TABLE:
                return new SpawnSnowballLogic(EntityType.WITCH, EntityType.ENDER_CRYSTAL, 8.0);

            case GOLD_NUGGET:
                return new ItemDropSnowballLogic(Material.PORK) {
                    @Override
                    protected EntityType getEntityToSpawn(Snowball snowball, SnowballInfo info) {
                        if (info.power > 8) {
                            return EntityType.PIG_ZOMBIE;
                        } else {
                            return null;
                        }
                    }
                };

            case DRAGON_EGG:
                return new DeathVortexSnowballLogic();

            case LEATHER:
                return new ItemDropSnowballLogic(
                        Material.BOOK,
                        Material.LEATHER_HELMET,
                        Material.LEATHER_CHESTPLATE,
                        Material.LEATHER_LEGGINGS,
                        Material.LEATHER_BOOTS,
                        Material.STICK,
                        Material.WOOD_SWORD,
                        Material.WOOD_PICKAXE,
                        Material.WOOD_AXE,
                        Material.WOOD_SPADE,
                        Material.WOOD_HOE,
                        Material.WORKBENCH,
                        Material.SADDLE);

            case IRON_BLOCK:
                return new ItemDropSnowballLogic(
                        Material.IRON_HELMET,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_LEGGINGS,
                        Material.IRON_BOOTS,
                        Material.IRON_SWORD,
                        Material.IRON_PICKAXE,
                        Material.IRON_AXE,
                        Material.IRON_SPADE,
                        Material.IRON_HOE);

            case GOLD_BLOCK:
                return new ItemDropSnowballLogic(
                        Material.GOLD_HELMET,
                        Material.GOLD_CHESTPLATE,
                        Material.GOLD_LEGGINGS,
                        Material.GOLD_BOOTS,
                        Material.GOLD_SWORD,
                        Material.GOLD_PICKAXE,
                        Material.GOLD_AXE,
                        Material.GOLD_SPADE,
                        Material.GOLD_HOE);

            case SADDLE:
                return new ItemDropSnowballLogic(Material.LEASH) {
                    @Override
                    protected EntityType getEntityToSpawn(Snowball snowball, SnowballInfo info) {
                        return EntityType.HORSE;
                    }
                };

            //developing ItemDropSnowball to be a randomizer, it won't be
            //heavily used so it can be full of special cases

            case GOLD_INGOT:
                return new SpawnSnowballLogic(EntityType.PIG, EntityType.PIG_ZOMBIE, 8.0);

            case STRING:
                return new SpawnSnowballLogic(EntityType.SPIDER, EntityType.CAVE_SPIDER, 8.0);

            case EYE_OF_ENDER:
                return new SpawnSnowballLogic(EntityType.ENDERMAN);

            case MILK_BUCKET:
                return new SpawnSnowballLogic(EntityType.COW);
            //get cow if you have ever milked one
            //path to leather -> book -> enchanting table

            case MUSHROOM_SOUP:
                return new SpawnSnowballLogic(EntityType.MUSHROOM_COW);
            //get mooshroom if you can get both mushrooms and wood
            //alternate path to leather -> book -> enchanting table!

            case JACK_O_LANTERN:
                return new SpawnSnowballLogic(EntityType.SKELETON, EntityType.WITHER_SKULL, 8.0);

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
            performLaunch(logic, snowball, info);
        }

        return logic;
    }

    /**
     * This overload of performLaunch takes the logic to associate with the
     * snowball instead of an inventory.
     *
     * @param logic The logic to apply to the snowball; can't be null.
     * @param snowball The snowball to be launched.
     * @param info The info record that describes the snowball.
     */
    public static void performLaunch(SnowballLogic logic, Snowball snowball, SnowballInfo info) {
        logic.start(snowball, info);

        if (info.shouldLogMessages) {
            Bukkit.getLogger().info(String.format("Snowball launched: %s [%d]", logic, inFlight.size()));
        }

        logic.launch(snowball, info);
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
                if (data.info.shouldLogMessages) {
                    Bukkit.getLogger().info(String.format("Snowball hit: %s [%d]", data.logic, inFlight.size()));
                }

                data.logic.hit(snowball, data.info);
            } finally {
                data.logic.end(snowball);
            }
        }
    }

    public static double performDamage(Snowball snowball, Entity target, double damage) {
        SnowballLogicData data = getData(Preconditions.checkNotNull(snowball));

        if (data != null) {
            if (data.info.shouldLogMessages) {
                Bukkit.getLogger().info(String.format("Snowball damage: %s [%d]", data.logic, inFlight.size()));
            }

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
     * This method calls tick() on each snowball that has any logic.
     */
    public static void onTick() {
        for (Map.Entry<Snowball, SnowballLogicData> e : inFlight.entrySet()) {
            Snowball snowball = e.getKey();
            SnowballLogic logic = e.getValue().logic;
            SnowballInfo info = e.getValue().info;

            logic.tick(snowball, info);
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
    private static int approximateInFlightCount = 0;
    private static long inFlightSyncDeadline = 0;

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
     * This returns the number of snowballs (that have attached logic) that are
     * currently in flight. This may count snowballs that have been unloaded or
     * otherwise destroyed for a time; it is not exact.
     *
     * @return The number of in-flight snowballs.
     */
    public static int getInFlightCount() {
        long now = System.currentTimeMillis();

        if (inFlightSyncDeadline <= now) {
            inFlightSyncDeadline = now + 1000;
            approximateInFlightCount = inFlight.size();
        }
        return approximateInFlightCount;
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

        approximateInFlightCount++;
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is
     * done when snowball hits something.
     *
     * @param snowball The snowball to deregister.
     */
    public void end(Snowball snowball) {
        approximateInFlightCount--;
        inFlight.remove(snowball);
    }
    ////////////////////////////////////////////////////////////////
    // Utilitu Methods
    //

    /**
     * This returns the of the nearest non-air block underneath 'location' that
     * is directly over the ground. If 'locaiton' is inside the ground, we'll
     * return a new copy of the same location.
     *
     * @param location The starting location; this is not modified.
     * @return A new location describing the place found.
     */
    public static Location getGroundUnderneath(Location location) {
        Location loc = location.clone();

        for (;;) {
            // just in case we have a shaft to the void, we  need
            // to give up before we reach it.

            if (loc.getBlockY() <= 0) {
                return loc;
            }

            switch (loc.getBlock().getType()) {
                case AIR:
                case WATER:
                case STATIONARY_WATER:
                case LEAVES:
                case LONG_GRASS:
                case DOUBLE_PLANT:
                case LAVA:
                case SNOW:
                case WATER_LILY:
                case RED_ROSE:
                case YELLOW_FLOWER:
                case DEAD_BUSH:
                    loc.add(0, -1, 0);
                    break;
                default:
                    return loc;
            }
        }
    }
}
