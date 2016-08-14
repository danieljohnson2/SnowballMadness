package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This logic creates an entity at the point of impact. We can create a
 * different, better entity for powered snowballs (to get cave spiders instead
 * of spiders for instance). You can specify the 'upgradePower'; the pumped up
 * entity is spawned if the snowball is more powerful than this; use 1.0 to
 * indicate an upgrade for a snowball that is at all powered.
 *
 * We also have special handling for skulls; we can get powered creepers and
 * such this way.
 *
 * @author DanJ
 */
public class SpawnSnowballLogic<TEntity extends Entity> extends SnowballLogic {

    //private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);
    private final Class<? extends TEntity> entityClass;
    private final Class<? extends TEntity> poweredEntityClass;
    private final double upgradePower;

    public SpawnSnowballLogic(Class<? extends TEntity> entityClass) {
        this(entityClass, entityClass, 1.0);
    }

    public SpawnSnowballLogic(Class<? extends TEntity> entityClass, Class<? extends TEntity> poweredEntityClass, double upgradePower) {
        this.entityClass = Preconditions.checkNotNull(entityClass);
        this.poweredEntityClass = Preconditions.checkNotNull(poweredEntityClass);
        this.upgradePower = upgradePower;
    }

    /**
     * This method creates an instance of this logic; the use of a static method
     * like this means the type argument T is inferred, so you don't have to
     * give the type twice.
     */
    public static <T extends Entity> SpawnSnowballLogic<T> fromEntityClass(Class<T> entityClass) {
        return new SpawnSnowballLogic<T>(entityClass);
    }

    /**
     * This method creates an instance of this logic; the use of a static method
     * like this means the type argument T is inferred, so you don't have to
     * give the type thrice.
     */
    public static <T extends Entity> SpawnSnowballLogic<T> fromEntityClass(Class<? extends T> entityClass, Class<? extends T> poweredEntityClass, double upgradePower) {
        return new SpawnSnowballLogic<T>(entityClass, poweredEntityClass, upgradePower);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        //if (cooldown.check("")) {
            spawnEntity(snowball.getLocation(), info);
        //}
    }

    /**
     * This method actually creates the entity; it calls the overridable methods
     * canSpawnAt() pickSpawnClass(), and initializeEntity().
     *
     * @param location The place to spawn at.
     * @param info The snowball info in effect, if you need it.
     */
    private final void spawnEntity(Location location, final SnowballInfo info) {
        World world = location.getWorld();
        Location adjusted = location.clone();
        Class<? extends TEntity> spawnClass = pickSpawnClass(adjusted, info);

        if (spawnClass != null) {
            if (world.getLivingEntities().size() < 3000) {
                final TEntity spawned = world.spawn(adjusted, spawnClass);
                initializeEntity(spawned, info);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        equipEntity(spawned, info);
                    }
                }.runTaskLater(info.plugin, 1);
            }
        }
    }

    /**
     * This method decides whether we can spawn the entity at the location
     * given.
     *
     * @param location The location we proposed to spawn at.
     * @param info The snowball info of the snowball.
     * @return True to allow the spawn; false to spawn nothing.
     */
 /*   protected boolean canSpawnAt(Location location, SnowballInfo info) {
        //we're only going to spawn an entity if the block is breathable space
        //nerfing too-trivially-easy suffocation mob farms
        Block above = location.getBlock().getRelative(BlockFace.UP);
        return above.isEmpty() || above.isLiquid();
    } //since minions are now tame and can be freely killed there's no reason to have this
*/
    /**
     * This method returns the entity type to spawn; we pick the powered version
     * if the snowball is sufficiently powered.
     *
     * This method is also allowed to alter the location (it's a copy); this
     * will change where the entity spawns.
     *
     * This method can return null to not spawn anything.
     *
     * @param location The place the snowball hit. This method may modify the
     * location.
     * @param info The info for the snowball; tell us if the snowball was
     * powered.
     * @return The entity type to spawn.
     */
    protected Class<? extends TEntity> pickSpawnClass(Location location, SnowballInfo info) {
        if (info.power > upgradePower) {
            return poweredEntityClass;
        } else {
            return entityClass;
        }
    }

    /**
     * This method is called on the spawned entity to initialize it; if we
     * decide not to spawn the entity, this method is never called.
     *
     * @param spawned The newly spawned thing.
     * @param info The info of the snowball that spawned it.
     */
    protected void initializeEntity(TEntity spawned, SnowballInfo info) {
    }

    /**
     * This method is called on the spawned entity to populate it with
     * equipment, if appropriate. If decide not to spawn the entity, this method
     * is never called. Unlike initializeEntity(), this method is called only
     * after the entity has entered the world (it is delayed by one tick)- we
     * can't get equipment to 'stick' without this delay.
     *
     * @param spawned The newly spawned thing.
     * @param info The info of the snowball that spawned it.
     */
    protected void equipEntity(TEntity spawned, SnowballInfo info) {
    }

    /**
     * This returns the logic for a given skull type; some skull types just
     * produce null, which means we don't support them.
     *
     * @param skullType The type of skull used for the snowball.
     * @return A logic for the snowball, or null.
     */
    public static SpawnSnowballLogic<?> fromSkullType(SkullType skullType) {
        switch (skullType) {
            case CREEPER:
                return new CreeperLogic();

            case SKELETON:
                return fromEntityClass(Skeleton.class);

            case ZOMBIE:
                return new ZombieLogic();

            case WITHER:
                return new WitherSkeletonLogic();

            default:
                return null;
        }
    }

    /**
     * This logic provides powered creepers when powered.
     */
    private static class CreeperLogic extends SpawnSnowballLogic<Creeper> {

        public CreeperLogic() {
            super(Creeper.class);
        }

        @Override
        protected void initializeEntity(Creeper creepy, SnowballInfo info) {
            super.initializeEntity(creepy, info);

            if (info.power > 8) {
                creepy.setPowered(true);
            }
        }
    }

    /**
     * This logic provides villager zombies when powered.
     */
    private static class ZombieLogic extends SpawnSnowballLogic<Zombie> {

        public ZombieLogic() {
            super(Zombie.class);
        }

        @Override
        protected void initializeEntity(Zombie zombie, SnowballInfo info) {
            super.initializeEntity(zombie, info);

            if (info.power > 8) {
                zombie.setVillagerProfession(Villager.Profession.NORMAL);
            }
        }
    }

    /**
     * This logic provides wither skeletons when not powered, but withers when
     * powered a lot. You have to already have two wither stars.
     */
    private static class WitherSkeletonLogic extends SpawnSnowballLogic<Monster> {

        public WitherSkeletonLogic() {
            super(Skeleton.class, Wither.class, 15);
        }

        @Override
        protected void initializeEntity(Monster spawned, SnowballInfo info) {
            super.initializeEntity(spawned, info);

            if (spawned instanceof Skeleton) {
                Skeleton skelly = (Skeleton) spawned;
                skelly.setSkeletonType(Skeleton.SkeletonType.WITHER);
            }
        }
    }
}