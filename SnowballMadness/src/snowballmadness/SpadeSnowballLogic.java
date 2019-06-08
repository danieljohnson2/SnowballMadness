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
public class SpadeSnowballLogic extends SnowballLogic {

    private final Material toolUsed;

    public SpadeSnowballLogic(Material toolUsed) {
        this.toolUsed = Preconditions.checkNotNull(toolUsed);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
int baseTool = 0; //wooden pick
        switch (toolUsed) {
            case DIAMOND_SPADE:
                baseTool = 4;
                break;
            case GOLD_SPADE:
                baseTool = 3;
                break;
            case IRON_SPADE:
                baseTool = 2;
                break;
            case STONE_SPADE:
                baseTool = 1;
                break;
        }
        final double totalEffectiveness = baseTool * 4f;
        final int radius = (int) (Math.sqrt(totalEffectiveness * baseTool));
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
                        if (canMine(beingMined)) {
                            beingMined.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    protected boolean canMine(Block target) {
        Material material = target.getType();

        return target.getType() == Material.DIRT //dig up shovel type stuff
                || (material == Material.GRASS)
                || (material == Material.SAND)
                || (material == Material.GRAVEL)
                || (material == Material.SNOW)
                || (material == Material.SNOW_BLOCK)
                || (material == Material.SOIL)
                || (material == Material.SOUL_SAND);
    }
}