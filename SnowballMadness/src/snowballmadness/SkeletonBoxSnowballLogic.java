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
public class SkeletonBoxSnowballLogic extends SnowballLogic {

    private final Material wallMaterial;

    public SkeletonBoxSnowballLogic(Material wallMaterial) {
        this.wallMaterial = wallMaterial;
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

        if (wallMaterial != null) {
            for (int x = beginX; x <= endX; ++x) {
                Block target = world.getBlockAt(x, beginY, beginZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(x, endY, beginZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(x, beginY, endZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(x, endY, endZ);
                target.setType(wallMaterial);

            }
            for (int z = beginZ; z <= endZ; ++z) {
                Block target = world.getBlockAt(beginX, beginY, z);
                target.setType(wallMaterial);
                target = world.getBlockAt(endX, beginY, z);
                target.setType(wallMaterial);
                target = world.getBlockAt(beginX, endY, z);
                target.setType(wallMaterial);
                target = world.getBlockAt(endX, endY, z);
                target.setType(wallMaterial);
            }
            for (int y = beginY; y <= endY; ++y) {
                Block target = world.getBlockAt(beginX, y, beginZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(endX, y, beginZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(beginX, y, endZ);
                target.setType(wallMaterial);
                target = world.getBlockAt(endX, y, endZ);
                target.setType(wallMaterial);
            }
        }
    }
}