package snowballmadness;

import com.google.common.base.Preconditions;
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
public class WallPaintingSnowballLogic extends SnowballLogic {

    private final Material paint;
    private final short durability;
    private int boxSize;

    public WallPaintingSnowballLogic(Material purpose, short durability, int boxSize) {
        this.paint = purpose;
        this.durability = Preconditions.checkNotNull(durability);
        this.boxSize = boxSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        final int radius = boxSize + 1;
        final int diameter = radius * 2;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

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
        for (int x = beginX; x <= endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z);
                    if (snowballLoc.distance(locationBuffer) <= radius) {
                        Block target = world.getBlockAt(x, y, z);
                        if (target.getType() == Material.STONE
                                || target.getType() == Material.CONCRETE
                                || target.getType() == Material.HARD_CLAY
                                || target.getType() == Material.QUARTZ_BLOCK
                                || target.getType() == Material.STAINED_CLAY) {
                            target.setType(paint);
                            target.setData((byte) durability);
                        } //this logic repaints things in shades of stone or concrete (colored)
                    }
                }
            }
        }
    }
}