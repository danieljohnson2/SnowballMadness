package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

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
public class SpawnSnowballLogic2<TEntity extends Entity> extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);
    private final Class<? extends TEntity> entityType;
    private final Class<? extends TEntity> poweredEntityType;
    private final double upgradePower;

    public SpawnSnowballLogic2(Class<? extends TEntity> entityType) {
        this(entityType, entityType, 1.0);
    }

    public SpawnSnowballLogic2(Class<? extends TEntity> entityType, Class<? extends TEntity> poweredEntityType, double upgradePower) {
        this.entityType = Preconditions.checkNotNull(entityType);
        this.poweredEntityType = Preconditions.checkNotNull(poweredEntityType);
        this.upgradePower = upgradePower;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (cooldown.check("")) {
            createEntity(snowball.getLocation(), info);
        }
    }

    /**
     * This method actually creates the entity; subclasses can adjust the
     * entity's properties.
     *
     * @param world The world to spawn in.
     * @param location The place to spawn at.
     * @param info The snowball info in effect, if you need it.
     * @return The new entity created, or null if it could not be created.
     */
    protected final TEntity createEntity(Location location, SnowballInfo info) {
        //We always return here to create an entity.
        World world = location.getWorld();
        
        if (canSpawnAt(location, info)) {
            //we're only here if the block is breathable space
            //nerfing too-trivially-easy suffocation mob farms

            Location adjusted = location.clone();
            Class<? extends TEntity> spawnClass = pickSpawnClass(adjusted, info);

            if (spawnClass != null) {
                TEntity spawned = world.spawn(adjusted, spawnClass);
                initializeEntity(spawned, info);
                return spawned;
            } else {
                return null;
            }
        } else {
            //we did not have an air block, so we will just return a big zap!
            //we have to return something that's an entitytype.
            //this is sound and fire but does not do the lightning display, which
            //distinguishes it from real lightning: leave that way.
            world.spawnEntity(location, EntityType.LIGHTNING);
            return null;
        }
    }

    // TODO: comment
    protected boolean canSpawnAt(Location location, SnowballInfo info) {
        Block above = location.getBlock().getRelative(BlockFace.UP);
        return above.isEmpty() || above.isLiquid();
    }

    /**
     * This method returns the entity type to spawn; we pick the powered version
     * if the snowball is sufficiently powered.
     *
     * This method is also allowed to alter the location (it's a copy); this
     * will change where the entity spawns.
     *
     * this method cna return null to not spawn anything.
     *
     * @param location The place the snowball hit. This method may modify the
     * location.
     * @param info The info for the snowball; tell us if the snowball was
     * powered.
     * @return The entity type to spawn.
     */
    protected Class<? extends TEntity> pickSpawnClass(Location location, SnowballInfo info) {
        if (info.power > upgradePower) {
            return poweredEntityType;
        } else {
            return entityType;
        }
    }

    // TODO: comment
    protected void initializeEntity(TEntity spawned, SnowballInfo info) {
    }
    // TODO: comment

    public static <T extends Entity> SpawnSnowballLogic2<T> fromEntityClass(Class<T> entityClass) {
        return new SpawnSnowballLogic2<T>(entityClass);
    }

    public static <T extends Entity> SpawnSnowballLogic2<T> fromEntityClass(Class<? extends T> entityType, Class<? extends T> poweredEntityType, double upgradePower) {
        return new SpawnSnowballLogic2<T>(entityType, poweredEntityType, upgradePower);
    }

    /**
     * This returns the logic for a given skull type; some skull types just
     * produce null, which means we don't support them.
     *
     * @param skullType The type of skull used for the snowball.
     * @return A logic for the snowball, or null.
     */
    public static SpawnSnowballLogic2<?> fromSkullType(SkullType skullType) {
        switch (skullType) {
            case CREEPER:
                return new CreeperLogic();

            case SKELETON:
                return new SpawnSnowballLogic2<Skeleton>(Skeleton.class);

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
    private static class CreeperLogic extends SpawnSnowballLogic2<Creeper> {

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
    private static class ZombieLogic extends SpawnSnowballLogic2<Zombie> {

        public ZombieLogic() {
            super(Zombie.class);
        }

        @Override
        protected void initializeEntity(Zombie zombie, SnowballInfo info) {
            super.initializeEntity(zombie, info);

            if (info.power > 8) {
                zombie.setVillager(true);
            }
        }
    }

    /**
     * This logic provides wither skeletons when not powered, but withers when
     * powered a lot. You have to already have two wither stars.
     */
    private static class WitherSkeletonLogic extends SpawnSnowballLogic2<Monster> {

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