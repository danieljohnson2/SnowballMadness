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

        boxSize = (int) Math.min(boxSize, info.power);
        //size of bukkit is limited by how high level you are
        final int radius = boxSize / 2;
        final int diameter = boxSize;
        final double distanceLimit = radius + 1.0;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(snowballLoc.getBlockY() - radius, 1);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = beginY + diameter;
        final int endZ = beginZ + diameter;
        final Location locationBuffer = new Location(world, 0, 0, 0);

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!
        if (purpose == Material.BUCKET) {
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {
                    for (int y = beginY; y <= endY; ++y) {
                        locationBuffer.setX(x);
                        locationBuffer.setY(y);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                            Block target = world.getBlockAt(x, y, z);
                            if (target.getType() == Material.GLASS
                                    || target.getType() == Material.FIRE
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

        if (purpose == Material.WATER_BUCKET) {
            int y = beginY;
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {
                    locationBuffer.setX(x);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z);
                    if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                        Block target = world.getBlockAt(x, y, z);
                        if (target.getType() == Material.AIR
                                || target.getType() == Material.WATER) {
                            target.setType(Material.STATIONARY_WATER);
                        }
                    }
                }
            }
        }
        //the normal world water filling version

        if (purpose == Material.LAVA_BUCKET) {
            int y = beginY;
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {

                    for (int yz = 130; yz >= 40; --yz) {
                        locationBuffer.setX(x);
                        locationBuffer.setY(yz);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                            locationBuffer.setX(x);
                            locationBuffer.setY(y);
                            locationBuffer.setZ(z);
                            if (snowballLoc.distance(locationBuffer) <= distanceLimit) {
                                Block target = world.getBlockAt(x, y, z);
                                if (target.getType() == Material.AIR
                                        || target.getType() == Material.WATER
                                        || target.getType() == Material.STATIONARY_WATER) {
                                    target.setType(Material.STATIONARY_LAVA);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
