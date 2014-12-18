package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic makes a box out of the material you give it, varying the construction method by type. You can specify the material
 * for the walls and the fill, but by default this class will only replace air and liquid blocks.
 *
 * @author christopherjohnson
 */
public class BoxSnowballLogic extends SnowballLogic {

    private final Material wallMaterial;
    private final Material fillMaterial;

    public BoxSnowballLogic(Material wallMaterial) {
        this(wallMaterial, Material.AIR);
    }

    public BoxSnowballLogic(Material wallMaterial, Material fillMaterial) {
        this.wallMaterial = wallMaterial;
        this.fillMaterial = fillMaterial;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location snowballLoc = snowball.getLocation();
        World world = snowball.getWorld();
        final int radius = (int) info.power;
        final int diameter = (int) (info.power * 2);
        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.

        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(1, snowballLoc.getBlockY() - radius);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + diameter);
        final int endZ = beginZ + diameter;

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!

        // beginX is our initial solid wall
        if (wallMaterial != null) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    Block target = world.getBlockAt(beginX, y, z);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                    }
                }
            }
        }

        //least efficient method used for hollow section. If we did not need to
        //hollow this out, we could just do each wall directly, but we're filling
        //with air. For a truly huge box, it'd be preferable to do the walls and
        //then fill the center: for 3x3x3 or 5x5x5 it's pointless. Our largest box
        //begins to flirt with this issue.
        for (int x = beginX + 1; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    final boolean isWallBlock =
                            x == beginX || x == endX
                            || z == beginZ || z == endZ
                            || y == beginY || y == endY;

                    final Material replacement = isWallBlock ? wallMaterial : fillMaterial;

                    if (replacement != null) {
                        Block target = world.getBlockAt(x, y, z);
                        if (canReplace(target)) {
                            target.setType(replacement);
                        }
                    }
                }
            }
        }

        //endX is our final wall
        if (wallMaterial != null) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    Block target = world.getBlockAt(endX, y, z);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                    }
                }
            }
        }
        //this implementation is sort of a compromise between Dan making the code
        //as elegant and plain as possible, and Chris insisting on some videogamey
        //efficiency hacks. Off to devise an even crazier power-boost so as to
        //illustrate the need for execution efficiency :D
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