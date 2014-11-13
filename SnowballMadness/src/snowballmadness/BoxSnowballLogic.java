/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic makes a box out of the material you give it, varying the
 * construction method by type. You can specify the material for the walls and
 * the fill, but by default this class will only replace air and liquid blocks.
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
        // we allow nulls here, with the meaning 'don't touch the blocks at
        // all'.
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

        for (int x = beginX; x <= endX; ++x) {
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
                            replaceBlock(target, replacement);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is called on each block that is to be updated, and by default
     * simply calls setType() on nit. You can override this to set metadata or
     * make other changes.
     *
     * This method is not called if the material would be null because null was
     * passed tot he constructor. It is also nto called if canReplace() returns
     * false.
     *
     * @param target The block to update.
     * @param material The new material it should have.
     */
    protected void replaceBlock(Block target, Material material) {
        target.setType(material);
    }

    /**
     * This method decides whether to replace a given block; by default it
     * replaces air and liquid blocks only.
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
                || material == Material.STATIONARY_LAVA;
    }
}