package snowballmadness;

import com.google.common.base.*;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This logic replaces leaves or grass or flower blocks with air. It basically
 * shaves everything, with the shears. No special purpose beyond mowing lawns.
 *
 * @author chrisjohnson
 */
public class ShearsSnowballLogic extends SnowballLogic {

    public ShearsSnowballLogic() {
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        final int radius = (int) Math.sqrt(64 * info.power);
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
                        if (canMine(beingMined, info.power)) {
                            beingMined.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    protected boolean canMine(Block target, double extraPower) {
        Material material = target.getType();
        //multiplied by: 1 for no power boosts at all
        //multiplied by: 9 for double glowstone
        //multiplied by: 16 for double nether star

        return target.getType() == Material.LEAVES //the simplest case: clear leaves.
                || (material == Material.LEAVES_2)
                || (material == Material.LONG_GRASS)
                || (material == Material.DOUBLE_PLANT)
                || (material == Material.RED_ROSE)
                || (material == Material.YELLOW_FLOWER)
                || (material == Material.DEAD_BUSH)
                || (material == Material.DEAD_BUSH)
                || (material == Material.DEAD_BUSH)
                || (material == Material.SNOW && (extraPower > 1)) //power it and you can shovel snow

                || (material == Material.WATER_LILY && (extraPower > 8))
                || (material == Material.LOG && (extraPower > 8))
                || (material == Material.LOG_2 && (extraPower > 8))
                || (material == Material.WOOD && (extraPower > 8))
                || (material == Material.CACTUS && (extraPower > 8))
                || (material == Material.CROPS && (extraPower > 8))
                || (material == Material.HAY_BLOCK && (extraPower > 8))
                || (material == Material.HUGE_MUSHROOM_1 && (extraPower > 8))
                || (material == Material.HUGE_MUSHROOM_2 && (extraPower > 8))
                || (material == Material.MELON && (extraPower > 8))
                || (material == Material.MELON_STEM && (extraPower > 8))
                || (material == Material.POTATO && (extraPower > 8))
                || (material == Material.PUMPKIN && (extraPower > 8))
                || (material == Material.PUMPKIN_STEM && (extraPower > 8)) //double glowstone and it attacks crops & wood

                || (material == Material.DIRT && (extraPower > 11)) //one glowstone block and one nether star and
                || (material == Material.SAND && (extraPower > 11))
                //you can get the weird behavior where just the grass is left floating over space where dirt would be

                || (material == Material.GRASS && (extraPower > 15))
                || (material == Material.GRAVEL && (extraPower > 15)); //double nether star and it wipes out non-rock!
    }
}