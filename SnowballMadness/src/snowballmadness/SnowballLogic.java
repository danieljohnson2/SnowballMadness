package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.Lists;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.projectiles.*;

/**
 * This class is the base class that hosts the logic that triggers when a snowball hits a target.
 *
 * We keep these in a weak hash map, so it is important that this object (and all subclasses) not hold onto a reference to a
 * Snowball, or that snowball may never be collected.
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
     * This is called when the snowball hits something and returns teh damange to be done (which can be 0).
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
     * This method creates a new logic, but does not start it. It chooses the logic based on 'hint', which is the stack
     * immediately above the snowball being thrown.
     *
     * @param slice The inventory slice above the snowball in the inventory.
     * @return The new logic, not yet started or attached to a snowball, or null if the snowball will be illogical.
     */
    public static SnowballLogic createLogic(InventorySlice slice) {
        ItemStack hint = slice.getBottomItem();

        if (hint == null) {
            return null;
        }

        switch (hint.getType()) {

            case TORCH:
                return new TorchPlaceSnowballLogic(hint.getType());
            //let's just allow people to light stuff, what the heck. So convenient.

            case ARROW:
                return new ArrowSnowballLogic();

            case RED_ROSE:
            case YELLOW_FLOWER:
                return new FireworkSnowballLogic(hint);

            case SAPLING:
                return new ArboristSnowballLogic(hint);

            case IRON_PICKAXE:
            case GOLD_PICKAXE:
            case DIAMOND_PICKAXE:
                return new PickaxeSnowballLogic(hint.getType());

            case SHEARS:
                return new ShearsSnowballLogic();

            case BOOKSHELF:
            case ENDER_CHEST:
            case DIAMOND_BLOCK:
            case IRON_BLOCK:
            case EMERALD_BLOCK:
            case REDSTONE_BLOCK:
            case LAPIS_BLOCK:
            case SLIME_BLOCK:
            case QUARTZ_BLOCK:
            case COAL_BLOCK:
            case GOLD_BLOCK:
            case SNOW_BLOCK:
            case BRICK:
            case NETHER_BRICK:
            case RED_NETHER_BRICK:
            case SMOOTH_BRICK:
            case GLASS:
            case STAINED_GLASS:
            case CLAY:
            case STAINED_CLAY:
            case GLOWSTONE:
            case PURPUR_BLOCK:
            case PRISMARINE:
            case NETHER_WART_BLOCK:
                return new BlockPlacementSnowballLogic(hint.getType(), hint.getDurability());
            //generate stuff in quantity from any block of the consolidated stuff, including books and bricks
            //Any block made by players out of sub-components. Glass is made out of sand. All generate single block at a time.

            case DIRT:
            case LEAVES:
            case IRON_FENCE:
            case STONE:
            case ENDER_STONE:
            case WOOD:
            case LOG:
            case MOSSY_COBBLESTONE:
            case COBBLESTONE:
                return new BoxSnowballLogic(hint.getType(), hint.getAmount(), hint.getDurability());

            case GLASS_BOTTLE:
            case POTION:
                return new BoxSnowballLogic(Material.GLASS, hint.getAmount(), hint.getDurability());

            case WOOD_SPADE:
            case LADDER:
            case VINE:
            case COAL_ORE:
            case IRON_ORE:
            case REDSTONE_ORE:
            case LAPIS_ORE:
            case EMERALD_ORE:
            case DIAMOND_ORE:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType());

            case BUCKET:
                return new SphereSnowballLogic(Material.AIR, Material.AIR);

            case MAGMA:
                return new SphereSnowballLogic(Material.FIRE, Material.FIRE);

            case SULPHUR:
                return new TNTSnowballLogic(0.25f);

            case TNT:
                return new TNTSnowballLogic(1.0f);

            case FIREWORK:
                return new JetpackSnowballLogic();

            case DRAGON_EGG:
                return new DeathVortexSnowballLogic();

            case IRON_INGOT:
                return new MagneticSnowballLogic();

            case APPLE:
                return new SpeededSnowballLogic(1.5, slice.skip(1));

            case MELON:
                return new SpeededSnowballLogic(1.5, slice.skip(1));

            case SUGAR:
                return new SpeededSnowballLogic(2, slice.skip(1));

            case COOKIE:
                return new SpeededSnowballLogic(2.4, slice.skip(1));

            case PUMPKIN_PIE:
                return new SpeededSnowballLogic(2.6, slice.skip(1));

            case CAKE:
                return new SpeededSnowballLogic(3, slice.skip(1));

            case JACK_O_LANTERN:
                return new SpawnSnowballLogic<Skeleton>(Skeleton.class) {
                    @Override
                    protected void initializeEntity(Skeleton spawned, final SnowballInfo info) {
                        super.initializeEntity(spawned, info);

                        //my skellington army of undead minions!
                        if (info.speed > 1) {
                            spawned.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (int) info.speed));
                        }
                    }

                    @Override
                    protected void equipEntity(Skeleton spawned, SnowballInfo info) {
                        super.equipEntity(spawned, info);
                        equipSkele(info.plugin, spawned, info);
                    }
                };

            case ROTTEN_FLESH:
                return new SpawnSnowballLogic<Zombie>(Zombie.class) {
                    @Override
                    protected void initializeEntity(Zombie spawned, SnowballInfo info) {
                        super.initializeEntity(spawned, info);
                        //my zombie army of yucky minions!
                        if (info.speed > 1) {
                            spawned.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (int) info.power));
                        }
                    }

                    @Override
                    protected void equipEntity(Zombie spawned, SnowballInfo info) {
                        super.equipEntity(spawned, info);
                        equipZombie(info.plugin, spawned, info);
                    }
                };

            default:
                return null;
        }
    }

    ////////////////////////////////////////////////////////////////
    // Event Handling
    //
    /**
     * This method processes a new snowball, executing its launch() method and also recording it so the hit() method can be called
     * later.
     *
     * The shooter may be provided as well; this allows us to launch snowballs from places that are not a player, but associated
     * it with a player anyway.
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
     * This overload of performLaunch takes the logic to associate with the snowball instead of an inventory.
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
     * This method processes the impact of a snowball, and invokes the hit() method on its logic object, if it has one.
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
     * This method handles a projectile launch; it selects a logic and runs its launch method.
     *
     * @param e The event data.
     */
    public static void onProjectileLaunch(SnowballMadness plugin, ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        ProjectileSource psource = proj.getShooter();
        if (psource instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity) psource;

            if (proj instanceof Snowball && shooter instanceof Player) {

                Snowball snowball = (Snowball) proj;
                Player player = (Player) shooter;
                //if ((player.getLocation().toVector().getX() < 0.0) || (player.getLocation().toVector().getZ() < 0.0)) {
                //either of the vectors is negative: we're not in the Versus Set Area
                PlayerInventory inv = player.getInventory();
                int heldSlot = inv.getHeldItemSlot();
                ItemStack sourceStack = inv.getItem(heldSlot);
                if (sourceStack == null || sourceStack.getType() == Material.SNOW_BALL) {
                    InventorySlice slice = InventorySlice.fromSlot(player, heldSlot).skip(1);
                    SnowballLogic logic = performLaunch(slice, snowball,
                            new SnowballInfo(plugin, snowball.getLocation(), player));
                    if (logic != null) {
                        replenishSnowball(plugin, inv, heldSlot);
                    }
                }
                //} //this goes with the Versus special case: defines an area where Snowball doesn't function
            }
        }
    }

    /**
     * This method calls tick() on each snowball that has any logic. This also checks shouldContinue() on each snowball and
     * removes snowball that shouldn't continue.
     */
    public static void onTick(long tickCount) {
        for (Map.Entry<Snowball, SnowballLogicData> e : inFlight.entrySet()) {

            Snowball snowball = e.getKey();
            SnowballLogic logic = e.getValue().logic;
            SnowballInfo info = e.getValue().info;

            Location here = snowball.getLocation();
            double y = here.getY();

            if (y > 0 && y < 1024 && info.launchLocation.distanceSquared(here) < 1048576) {  //square the desired distance for this number: is 1024
                logic.tick(snowball, info);
            } else {
                logic.end(snowball);
                snowball.remove();
                break;
                //we never need to delete all snowballs at once: each tick we can get one
            }
        }
    }

    /**
     * This method handles the damage a snowball does on impact, and can adjust that damage.
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
     * This method increments the number of snowballs in the slot indicated; but it does this after a brief delay since changes
     * made during the launch are ignored. If the indicated slot contains something that is not a snowball, we don't update it. If
     * it is empty, we put one snowball in there.
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

    private static void equipSkele(Plugin plugin, final Skeleton spawned, final SnowballInfo info) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (info.power > 3.3 && info.shooter != null) {
                    //you have to be over level 10 to make them your minions
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(info.shooter.getName());
                    skull.setItemMeta(meta);
                    // OH GOD IT HAS MY FAAAAAAACE!
                    spawned.getEquipment().setHelmet(skull);
                    spawned.getEquipment().setHelmetDropChance(0.0f);

                    if (info.power > 4.5) {
                        //you have to be over level 20 to armor them and have them holding your weapon
                        ItemStack gear = info.shooter.getInventory().getChestplate();
                        if (gear != null) {
                            spawned.getEquipment().setChestplate(gear);
                            spawned.getEquipment().setChestplateDropChance(0.0f);
                        }
                        gear = info.shooter.getInventory().getLeggings();
                        if (gear != null) {
                            spawned.getEquipment().setLeggings(gear);
                            spawned.getEquipment().setLeggingsDropChance(0.0f);
                        }
                        gear = info.shooter.getInventory().getBoots();
                        if (gear != null) {
                            spawned.getEquipment().setBoots(gear);
                            spawned.getEquipment().setBootsDropChance(0.0f);
                        }
                        if (info.power > 5.5) {
                            gear = info.shooter.getInventory().getItem(0);
                            if (gear != null) {
                                spawned.getEquipment().setItemInMainHand(gear);
                                spawned.getEquipment().setItemInMainHandDropChance(1.0f);
                            }
                        }
                    }
                    spawned.setCustomName(info.shooter.getName() + "'s Minion");
                    spawned.setCustomNameVisible(true);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    private static void equipZombie(Plugin plugin, final Zombie spawned, final SnowballInfo info) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (info.power > 3.3 && info.shooter != null) {
                    //you have to be over level 10 to make them your minions
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(info.shooter.getName());
                    skull.setItemMeta(meta);
                    // OH GOD IT HAS MY FAAAAAAACE!
                    spawned.getEquipment().setHelmet(skull);
                    spawned.getEquipment().setHelmetDropChance(0.0f);

                    if (info.power > 4.5) {
                        //you have to be over level 20 to armor them
                        ItemStack gear = info.shooter.getInventory().getChestplate();
                        if (gear != null) {
                            spawned.getEquipment().setChestplate(gear);
                            spawned.getEquipment().setChestplateDropChance(0.0f);
                        }
                        gear = info.shooter.getInventory().getLeggings();
                        if (gear != null) {
                            spawned.getEquipment().setLeggings(gear);
                            spawned.getEquipment().setLeggingsDropChance(0.0f);
                        }
                        gear = info.shooter.getInventory().getBoots();
                        if (gear != null) {
                            spawned.getEquipment().setBoots(gear);
                            spawned.getEquipment().setBootsDropChance(0.0f);
                        }
                        if (info.power > 5.5) {
                            gear = info.shooter.getInventory().getItem(0);
                            if (gear != null) {
                                spawned.getEquipment().setItemInMainHand(gear);
                                spawned.getEquipment().setItemInMainHandDropChance(1.0f);
                            }
                        }
                    }
                    spawned.setCustomName(info.shooter.getName() + "'s Minion");
                    spawned.setCustomNameVisible(true);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    public static void onEntityTargetPlayer(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            if ((event.getEntity().getCustomName() != null) && (event.getTarget().getName() != null)) {
                //trying to prevent null exceptions: we only care about the 'match' case
                if (event.getEntity().getCustomName().startsWith(event.getTarget().getName())) {
                    event.setCancelled(true);
                } //make all minions not harm their creators
            } //in some circumstances, new entities run this before they're ready.
        } //when that happens, the odd minion will try to kill you until you smack it to snap it out of its madness!
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
    // private static int approximateInFlightCount = 0;
    private static long inFlightSyncDeadline = 0;

    /**
     * this class just holds the snowball logic and info for a snowball; the snowball itself must not be kept here, as this is the
     * value of a weak-hash-map keyed on the snowballs. We don't want to keep them alive.
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
     * @return The logic and info of the snowball, or null if it is an illogical snowball or it was null.
     */
    private static SnowballLogicData getData(Snowball snowball) {
        if (snowball != null) {
            return inFlight.get(snowball);
        } else {
            return null;
        }
    }

    /**
     * This method registers the logic so getLogic() can find it. Logics only work once started.
     *
     * @param snowball The snowball being launched.
     * @param info Other information about the snowball.
     */
    public void start(Snowball snowball, SnowballInfo info) {
        inFlight.put(snowball, new SnowballLogicData(this, info));
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is done when snowball hits something.
     *
     * @param snowball The snowball to deregister.
     */
    public void end(Snowball snowball) {
        inFlight.remove(snowball);
    }
    ////////////////////////////////////////////////////////////////
    // Utility Methods
    //

    /**
     * This returns the of the nearest non-air block underneath 'location' that is directly over the ground. If 'locaiton' is
     * inside the ground, we'll return a new copy of the same location.
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
