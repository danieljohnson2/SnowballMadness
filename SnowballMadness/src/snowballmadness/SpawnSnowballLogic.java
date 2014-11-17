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
     * This method actually creates the entity; subclasses can adjust the
     * entity's properties.
     *
     * @param world The world to spawn in.
     * @param location The place to spawn at.
     * @param info The snowball info in effect, if you need it.
     * @return The new entity created.
     */
    protected Entity createEntity(World world, Location location, SnowballInfo info) {
        //We always return here to create an entity.
        Location position = location.clone();
        position.setY(position.getY() + 1.0);
        //quickly check that above block is clear
        if (position.getBlock().isEmpty() || position.getBlock().isLiquid()) {
            //we're only here if the block is breathable space
            //nerfing too-trivially-easy suffocation mob farms
            if (this.entityType == EntityType.WITCH) {
                //here's where we will do the special case for enchanting tables. The basic
                //case is Witch, because that's what's sent by Enchanting Table. So if we
                //fall through, we get Witch.
                position.setY(position.getY() - 2.0);
                Material target = position.getBlock().getType(); //block spawn stands on
                Location inSky = location.clone();
                switch (target) {
                    case STATIONARY_LAVA:
                    case LAVA:
                    case FIRE:
                        return world.spawnEntity(location, EntityType.BLAZE);
                    //lava and fire produce blazes out of the lava/fire

                    case NETHERRACK:
                        return world.spawnEntity(location, EntityType.PIG_ZOMBIE);
                    //netherrack gives you zombie pigmen

                    case QUARTZ_ORE:
                        inSky.setY(inSky.getY() + 16);
                        return world.spawnEntity(inSky, EntityType.GHAST);
                    //to be annoying with ghasts, mine nether quartz ore and place it
                    //and fire enchantment table snowballs at it

                    case DIAMOND_ORE:
                        return world.spawnEntity(location, EntityType.WITHER);
                    //if spawning on diamond ore, you get wither. This is a dangerous way
                    //to get nether stars w/o wither skelly grinding.

                    case ENDER_PORTAL:
                    case ENDER_PORTAL_FRAME:
                    //if we are actually spawning it off the portal it's sent to the End.
                    //this will make the End more ridiculous, but odds of the dragon appearing there
                    //and not portaling out are very slim. May be possible if you break the portal.
                    //you'll get another (w. egg) from killing the dragon.
                    case DRAGON_EGG:
                        return world.spawnEntity(location, EntityType.ENDER_DRAGON);
                    //you get a dragon right there in your face. As it appears,
                    //it will very likely take out the egg it came from, so you fight them
                    //more one at a time, starting right where the egg was.
                    //For best spawning, you have to make a 3x2 wall behind the egg and hit just over it.

                    case GRASS:
                    case DIRT:
                    case GRAVEL:
                    case SAND:
                        return world.spawnEntity(location, EntityType.PRIMED_TNT);
                    //if we hit certain very ordinary blocks, simple boom. Best to be in a rocky cave.

                    case RED_ROSE:
                    case YELLOW_FLOWER:
                        return world.spawnEntity(location, EntityType.HORSE);
                    //horses are cool, spawn them off flowers with an enchanting table snowball

                    case BEDROCK:
                        inSky.setY(inSky.getY() + 2.0);
                        if (inSky.getBlock().isEmpty()) {
                            return world.spawnEntity(inSky, EntityType.ENDERMAN);
                        }
                    //down in those bedrock basements, you get endermen.
                    //Three high space required to not suffocate them.
                }
            }
            return world.spawnEntity(location, pickEntityType(info));
            //entitytype was not WITCH. Therefore, the default spawn case
        } else {
            //we did not have an air block, so we will just return a big zap!
            //we have to return something that's an entitytype.
            //this is sound and fire but does not do the lightning display, which
            //distinguishes it from real lightning: leave that way.
            return world.spawnEntity(location, EntityType.LIGHTNING);
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
     * powered a lot. You have to already have two wither stars.
     */
    private static class WitherSkeletonLogic extends SpawnSnowballLogic {

        public WitherSkeletonLogic() {
            super(EntityType.SKELETON, EntityType.WITHER, 15);
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