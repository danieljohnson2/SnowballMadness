package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This logic replaces leaves or grass or flower blocks with air. It basically shaves everything, with the shears. No special
 * purpose beyond mowing lawns.
 *
 * @author chrisjohnson
 */
public class HoeSnowballLogic extends SnowballLogic {

    private final Material toolUsed;

    public HoeSnowballLogic(Material toolUsed) {
        this.toolUsed = Preconditions.checkNotNull(toolUsed);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        int baseTool = 0; //wooden pick
        switch (toolUsed) {
            case DIAMOND_HOE:
                baseTool = 4;
                break;
            case GOLD_HOE:
                baseTool = 3;
                break;
            case IRON_HOE:
                baseTool = 2;
                break;
            case STONE_HOE:
                baseTool = 1;
                break;
        }
        final double totalEffectiveness = baseTool;
        final int radius = (int) (2.0f * baseTool);
        final double distanceSquaredLimit = (radius * (double) radius) + 1.0;

        //size is heavily dependent on tool type, power expands so aggressively with
        //doubling that we must control it. Max will still be very huge.
        final int diameter = (int) (radius * 2);
        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(0, snowballLoc.getBlockY() - radius);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + diameter);
        final int endZ = beginZ + diameter;

        final Location locationBuffer = new Location(world, 0, 0, 0);

        for (int x = beginX; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x + 0.5);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z + 0.5);
                    if (snowballLoc.distanceSquared(locationBuffer) < distanceSquaredLimit) {
                        final Block beingMined = world.getBlockAt(x, y, z);
                        if (beingMined.getType() == Material.GRASS) {
                            beingMined.setType(Material.GRASS_PATH);
                        }
                        if (beingMined.getType() == Material.LONG_GRASS
                                || beingMined.getType() == Material.DOUBLE_PLANT
                                || beingMined.getType() == Material.RED_ROSE
                                || beingMined.getType() == Material.YELLOW_FLOWER
                                || beingMined.getType() == Material.DEAD_BUSH) {
                            beingMined.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}