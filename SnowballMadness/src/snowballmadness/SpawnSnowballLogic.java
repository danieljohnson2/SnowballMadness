package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
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
public class SpawnSnowballLogic extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);
    private final EntityType entityType;
    private final EntityType poweredEntityType;
    private final double upgradePower;

    public SpawnSnowballLogic(EntityType entityType) {
        this(entityType, entityType, 1.0);
    }

    public SpawnSnowballLogic(EntityType entityType, EntityType poweredEntityType, double upgradePower) {
        this.entityType = Preconditions.checkNotNull(entityType);
        this.poweredEntityType = Preconditions.checkNotNull(poweredEntityType);
        this.upgradePower = upgradePower;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (cooldown.check("")) {
            createEntity(snowball.getWorld(), snowball.getLocation(), info);
        }
    }

    /**
     * This method returns the entity type to spawn; we pick the powered version
     * if the snowball is sufficiently powered.
     *
     * @param info The info for the snowball; tell us if the snowball was
     * powered.
     * @return The entity type to spawn.
     */
    protected EntityType pickEntityType(SnowballInfo info) {
        if (info.power > upgradePower) {
            return poweredEntityType;
        } else {
            return entityType;
        }
    }

    /**
     * This method actually creates the entity; subclasses can adjust the
     * entity's properties.
     *
     * @param world The world to spawn in.
     * @param location The place to spawn at.
     * @param info The snowball info in effect, if you need it.
     * @return The new entity created.
     */
    protected Entity createEntity(World world, Location location, SnowballInfo info) {
        return world.spawnEntity(location, pickEntityType(info));
    }

    /**
     * This returns the logic for a given skull type; some skull types just
     * produce null, which means we don't support them.
     *
     * @param skullType The type of skull used for the snowball.
     * @return A logic for the snowball, or null.
     */
    public static SpawnSnowballLogic fromSkullType(SkullType skullType) {
        switch (skullType) {
            case CREEPER:
                return new CreeperLogic();

            case SKELETON:
                return new SpawnSnowballLogic(EntityType.SKELETON);

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
    private static class CreeperLogic extends SpawnSnowballLogic {

        public CreeperLogic() {
            super(EntityType.CREEPER);
        }

        @Override
        protected Entity createEntity(World world, Location location, SnowballInfo info) {
            Creeper creepy = (Creeper) super.createEntity(world, location, info);

            if (info.power > 8) {
                creepy.setPowered(true);
            }

            return creepy;
        }
    }

    /**
     * This logic provides villager zombies when powered.
     */
    private static class ZombieLogic extends SpawnSnowballLogic {

        public ZombieLogic() {
            super(EntityType.ZOMBIE);
        }

        @Override
        protected Entity createEntity(World world, Location location, SnowballInfo info) {
            Zombie zombie = (Zombie) super.createEntity(world, location, info);

            if (info.power > 8) {
                zombie.setVillager(true);
            }

            return zombie;
        }
    }

    /**
     * This logic provides wither skeletons when not powered, but withers when
     * powered a lot.
     */
    private static class WitherSkeletonLogic extends SpawnSnowballLogic {

        public WitherSkeletonLogic() {
            super(EntityType.SKELETON, EntityType.WITHER, 8);
        }

        @Override
        protected Entity createEntity(World world, Location location, SnowballInfo info) {
            Entity spawned = super.createEntity(world, location, info);

            if (spawned instanceof Skeleton) {
                Skeleton skelly = (Skeleton) spawned;
                skelly.setSkeletonType(Skeleton.SkeletonType.WITHER);
            }

            return spawned;
        }
    }
}