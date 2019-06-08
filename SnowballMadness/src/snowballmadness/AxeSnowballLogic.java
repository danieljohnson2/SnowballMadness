package snowballmadness;

import com.google.common.base.Preconditions;
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
public class AxeSnowballLogic extends SnowballLogic {

    private final Material toolUsed;

    public AxeSnowballLogic(Material toolUsed) {
        this.toolUsed = Preconditions.checkNotNull(toolUsed);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        int baseTool = 0; //wooden pick
        switch (toolUsed) {
            case DIAMOND_AXE:
                baseTool = 8;
                break;
            case GOLD_AXE:
                baseTool = 4;
                break;
            case IRON_AXE:
                baseTool = 2;
                break;
            case STONE_AXE:
                baseTool = 1;
                break;
        }
        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();
        final int beginX = snowballLoc.getBlockX() - baseTool;
        final int beginY = Math.max(0, snowballLoc.getBlockY() - (baseTool * 10));
        final int beginZ = snowballLoc.getBlockZ() - baseTool;
        final int endX = beginX + (baseTool * 2);
        final int endY = Math.min(world.getMaxHeight(), beginY + (baseTool * 20));
        final int endZ = beginZ + (baseTool * 2);

        final Location locationBuffer = new Location(world, 0, 0, 0);

        for (int x = beginX; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x + 0.5);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z + 0.5);
                    final Block beingMined = world.getBlockAt(x, y, z);
                    if (beingMined.getType() == Material.LOG || beingMined.getType() == Material.LOG_2) {
                        beingMined.setType(Material.AIR);
                    }

                }
            }
        }
    }
}