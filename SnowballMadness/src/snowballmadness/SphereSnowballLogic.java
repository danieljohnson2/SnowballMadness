package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;

/**
 * This logic makes a box out of the material you give it, varying the construction method by type. You can specify the material
 * for the walls and the fill, but by default this class will only replace air and liquid blocks.
 *
 * @author christopherjohnson
 */
public class SphereSnowballLogic extends SnowballLogic {

    private final Material wallMaterial;
    private final Material fillMaterial;
    private int boxSize;

    public SphereSnowballLogic(Material wallMaterial, Material fillMaterial, int boxSize) {
        this.wallMaterial = wallMaterial;
        this.fillMaterial = fillMaterial;
        this.boxSize = boxSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        ProjectileSource shooter = snowball.getShooter();
        int expLevel = 1;
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            expLevel = player.getLevel();
        }
        if (boxSize > expLevel) {
            boxSize = expLevel;
        } //as you get better at magic (level up) your max creation size ramps up too

        final double totalEffectiveness = boxSize * info.power;
        final int radius = boxSize / 2;
        final int diameter = boxSize;
        final int stepSize = (int)Math.cbrt(boxSize) + 1;
        final double distanceSquaredLimit = (radius * (double) radius) + 1.0;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.

        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(1, snowballLoc.getBlockY() - radius);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + diameter);
        final int endZ = beginZ + diameter;
        final Location locationBuffer = new Location(world, 0, 0, 0);

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!

        if (fillMaterial == Material.FIRE) {
            for (int x = beginX; x <= endX; x += stepSize) {
                for (int z = beginZ; z <= endZ; z += stepSize) {
                    for (int y = beginY; y <= endY; y += stepSize) {
                        //fire is nasty so we make it more sparse
                        //that way it can spread and wipe itself out using our fire spread rule
                        Material replacement = fillMaterial;
                        locationBuffer.setX(x);
                        locationBuffer.setY(y);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distanceSquared(locationBuffer) <= distanceSquaredLimit) {
                            Block target = world.getBlockAt(x, y, z);
                            if (canReplace(target)) {
                                target.setType(replacement);
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = beginX; x <= endX; ++x) {
                for (int z = beginZ; z <= endZ; ++z) {
                    for (int y = beginY; y <= endY; ++y) {
                        Material replacement = fillMaterial;
                        locationBuffer.setX(x);
                        locationBuffer.setY(y);
                        locationBuffer.setZ(z);
                        if (snowballLoc.distanceSquared(locationBuffer) > ((distanceSquaredLimit * 0.9) - 9.0)) {
                            replacement = wallMaterial;
                        }
                        if (snowballLoc.distanceSquared(locationBuffer) <= distanceSquaredLimit) {
                            Block target = world.getBlockAt(x, y, z);
                            if (canReplace(target)) {
                                target.setType(replacement);
                            }
                        }
                    }
                }
            }

        }



    }

    /**
     * This method decides whether to replace a given block; by default it replaces air and liquid blocks only.
     *
     * @param target The proposed block to update.
     * @return True to update the block; false to do nothing.
     */
    protected boolean canReplace(Block target) {
        Material material = target.getType();

        return target.getType() == Material.AIR
                || material == Material.WATER
                || material == Material.STATIONARY_WATER
                || material == Material.LAVA
                || material == Material.STATIONARY_LAVA
                || material == Material.WEB
                || material == Material.TNT
                || material == Material.MONSTER_EGGS
                || material == Material.FIRE
                || material == Material.LONG_GRASS
                || material == Material.RED_ROSE
                || material == Material.YELLOW_FLOWER;
        //including some of the ground cover blocks as they seem like
        //glitches when they block wall placement
        //also, we replace webs and TNT where possible
    }
}