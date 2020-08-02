package snowballmadness;

import com.google.common.base.Preconditions;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This logic mines blocks with picks, with a broad set of rules for what can be replaced with air. It drops nothing, it's just a
 * clear-things-out tool for quickly exposing ores.
 *
 * @author chrisjohnson
 */
public class PickaxeSnowballLogic extends SnowballLogic {

    private final Material toolUsed;

    public PickaxeSnowballLogic(Material toolUsed) {
        this.toolUsed = Preconditions.checkNotNull(toolUsed);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        int baseTool = 2; //wooden pick
        switch (toolUsed) {
            case DIAMOND_PICKAXE:
                baseTool = 16;
                break;
            case GOLD_PICKAXE:
                baseTool = 12;
                break;
            case IRON_PICKAXE:
                baseTool = 8;
                break;
            case STONE_PICKAXE:
                baseTool = 4;
                break;
        }
        final double totalEffectiveness = baseTool * 4f;
        final int radius = baseTool;
        final double distanceSquaredLimit = (radius * (double) radius) - 2.0;

        //size is heavily dependent on tool type, power expands so aggressively with
        //doubling that we must control it. Max will still be very huge.
        final int diameter = (int) (radius * 2);
        World world = snowball.getWorld();
        Random rand = new Random();
        Location snowballLoc = snowball.getLocation().clone();
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(1, snowballLoc.getBlockY() - 1); // must be 1 min, for flat bedrock floor
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + radius);
        final int endZ = beginZ + diameter;

        final Location locationBuffer = new Location(world, 0, 0, 0);

        for (int x = beginX; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                double moddedRoof = distanceSquaredLimit;
                //value required is squared, remember: not a fixed amount
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x + 0.5);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z + 0.5);
                    final Block beingMined = world.getBlockAt(x, y, z);
                    final Material material = beingMined.getType();
                    if (snowballLoc.distanceSquared(locationBuffer) < moddedRoof) {
                        if (material == Material.CHEST
                                || (material == Material.ENDER_CHEST)) {
                            //don't mine
                        } else {
                            beingMined.setType(Material.AIR);
                        }//literally anything not a chest becomes air within our cave size
                        //which is also now a smooth form
                    } else {
                        //in the enclosing, larger box, if we have messy liquids and falling things
                        //we try to remove those
                        if (material == Material.SAND
                                || (material == Material.GRAVEL)
                                || (material == Material.WATER)
                                || (material == Material.STATIONARY_WATER)
                                || (material == Material.LAVA)
                                || (material == Material.STATIONARY_LAVA)) {
                            beingMined.setType(Material.AIR);
                        }
                    }
                }

            }
        }
    }
}
