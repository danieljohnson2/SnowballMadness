package snowballmadness;

import com.google.common.base.*;
import java.util.Random;
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
public class ShearsSnowballLogic extends SnowballLogic {

    public ShearsSnowballLogic() {
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        final int radius = (int) Math.sqrt(16 * info.power);
        final double distanceSquaredLimit = (radius * (double) radius);

        //size is heavily dependent on tool type, power expands so aggressively with
        //doubling that we must control it. Max will still be very huge.
        final int diameter = (int) (radius * 2);
        World world = snowball.getWorld();
        Random rand = new Random();
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

        return target.getType() == Material.LEAVES //the simplest case: clear leaves.
                || (material == Material.LEAVES_2)
                || (material == Material.LONG_GRASS)
                || (material == Material.DOUBLE_PLANT)
                || (material == Material.RED_ROSE)
                || (material == Material.YELLOW_FLOWER)
                || (material == Material.DEAD_BUSH)
                || (material == Material.DEAD_BUSH)
                || (material == Material.DEAD_BUSH)
                || (material == Material.SNOW) 
                || (material == Material.CACTUS)
                || (material == Material.CROPS)
                || (material == Material.MELON)
                || (material == Material.MELON_STEM)
                || (material == Material.POTATO)
                || (material == Material.PUMPKIN) 
                || (material == Material.PUMPKIN_STEM);
    }
}