package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.World.Environment;

/**
 * This logic makes a box out of the material you give it, varying the construction method by type. You can specify the material
 * for the walls and the fill, but by default this class will only replace air and liquid blocks.
 *
 * @author christopherjohnson
 */
public class RefillSnowballLogic extends SnowballLogic {

    private final Material purpose;
    private int boxSize;

    public RefillSnowballLogic(Material purpose, int boxSize) {
        this.purpose = purpose;
        this.boxSize = boxSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        final double totalEffectiveness = boxSize;
        final int radius = 128; //boxSize / 2;
        final int diameter = 256; //boxSize;
        final double distanceLimit = 128; //radius + 1.0;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = snowballLoc.getBlockY();
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endZ = beginZ + diameter;
        final Location locationBuffer = new Location(world, 0, 0, 0);

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!
        if (purpose == Material.BUCKET) {
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {
                    for (int y = beginY; y >= 1; --y) {
                        locationBuffer.setX(x);
                        locationBuffer.setY(y);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                            Block target = world.getBlockAt(x, y, z);
                            if (target.getType() == Material.GLASS
                                    || target.getType() == Material.LAVA
                                    || target.getType() == Material.STATIONARY_LAVA
                                    || target.getType() == Material.WATER
                                    || target.getType() == Material.STATIONARY_WATER) {
                                target.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
        //the normal world empty-stuff version

        if (world.getEnvironment()
                == Environment.NORMAL && purpose == Material.WATER_BUCKET) {
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {
                    for (int y = 61; y >= 1; --y) { //62
                        locationBuffer.setX(x);
                        locationBuffer.setY(y);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                            Block target = world.getBlockAt(x, y, z);
                            if (target.getType() == Material.AIR
                                    || target.getType() == Material.WATER
                                    || target.getType() == Material.STATIONARY_WATER) {
                                target.setType(Material.STATIONARY_WATER);
                            } else {
                                break;
                                //stop iterating down with y and do another z or x
                            }
                        }
                    }
                }
            }
        }
        //the normal world water filling version

        if (world.getEnvironment()
                == Environment.NETHER && purpose == Material.LAVA_BUCKET) {
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {

                    for (int yz = 130; yz >= 40; --yz) {
                        locationBuffer.setX(x);
                        locationBuffer.setY(yz);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                            for (int y = 39; y >= 30; --y) {
                                locationBuffer.setX(x);
                                locationBuffer.setY(y);
                                locationBuffer.setZ(z);
                                if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                                    Block target = world.getBlockAt(x, y, z);
                                    if (target.getType() == Material.LAVA
                                            || target.getType() == Material.NETHERRACK
                                            || target.getType() == Material.GRAVEL
                                            || target.getType() == Material.BROWN_MUSHROOM
                                            || target.getType() == Material.GLOWSTONE
                                            || target.getType() == Material.FIRE
                                            || target.getType() == Material.NETHER_BRICK
                                            || target.getType() == Material.NETHER_BRICK_STAIRS
                                            || target.getType() == Material.NETHER_FENCE
                                            || target.getType() == Material.NETHER_STALK
                                            || target.getType() == Material.QUARTZ_ORE
                                            || target.getType() == Material.MAGMA
                                            || target.getType() == Material.SOUL_SAND
                                            || target.getType() == Material.STATIONARY_LAVA) {
                                        target.setType(Material.QUARTZ_BLOCK);
                                    } else {
                                        break;
                                        //stop iterating down with y and do another z or x
                                    }
                                }
                            }
                        }
                    }
                }
                //the nether world put a quartz floor in version
            }
        }
    }
}
