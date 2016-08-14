package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.projectiles.*;

/**
 * This logic makes a box out of the material you give it, varying the construction method by type. You can specify the material
 * for the walls and the fill, but by default this class will only replace air and liquid blocks.
 *
 * @author christopherjohnson
 */
public class BoxSnowballLogic extends SnowballLogic {

    private final Material wallMaterial;
    private int boxSize;
    private final short durability;

    public BoxSnowballLogic(Material wallMaterial, int boxSize, short durability) {
        this.wallMaterial = wallMaterial;
        this.boxSize = boxSize;
        this.durability = durability;
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

        Location snowballLoc = snowball.getLocation();
        World world = snowball.getWorld();
        final int radius = boxSize;
        final int diameter = boxSize * 2;
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
                        target.setData((byte) durability);
                    }
                    target = world.getBlockAt(endX, y, z);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                        target.setData((byte) durability);
                    }

                }
            }
        }

        //beginY
        if (wallMaterial != null) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int x = beginX; x <= endX; ++x) {
                    Block target = world.getBlockAt(x, beginY, z);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                        target.setData((byte) durability);
                    }
                    target = world.getBlockAt(x, endY, z);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                        target.setData((byte) durability);
                    }

                }
            }
        }

        //beginZ
        if (wallMaterial != null) {
            for (int x = beginX; x <= endX; ++x) {
                for (int y = beginY; y <= endY; ++y) {
                    Block target = world.getBlockAt(x, y, beginZ);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                        target.setData((byte) durability);
                    }
                    target = world.getBlockAt(x, y, endZ);
                    if (canReplace(target)) {
                        target.setType(wallMaterial);
                        target.setData((byte) durability);
                    }

                }
            }
        }


        //hollow center.
        for (int x = beginX + 1; x < endX - 1; ++x) {
            for (int z = beginZ + 1; z <= endZ - 1; ++z) {
                for (int y = beginY + 1; y <= endY - 1; ++y) {
                    Block target = world.getBlockAt(x, y, z);
                    if (canReplace(target)) {
                        target.setType(Material.AIR);
                    }
                }
            }
        }
    }
    //this implementation is sort of a compromise between Dan making the code
    //as elegant and plain as possible, and Chris insisting on some videogamey
    //efficiency hacks. Off to devise an even crazier power-boost so as to
    //illustrate the need for execution efficiency :D

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