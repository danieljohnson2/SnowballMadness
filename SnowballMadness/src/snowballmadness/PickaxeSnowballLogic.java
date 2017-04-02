package snowballmadness;

import com.google.common.base.*;
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
        int baseTool = 0; //wooden pick
        switch (toolUsed) {
            case DIAMOND_PICKAXE:
                baseTool = 4;
                break;
            case GOLD_PICKAXE:
                baseTool = 3;
                break;
            case IRON_PICKAXE:
                baseTool = 2;
                break;
            case STONE_PICKAXE:
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
        Random rand = new Random();
        Location snowballLoc = snowball.getLocation().clone();
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(0, snowballLoc.getBlockY() - 1);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + radius);
        final int endZ = beginZ + diameter;

        final Location locationBuffer = new Location(world, 0, 0, 0);

        for (int x = beginX; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                boolean roofMod = rand.nextBoolean();
                double moddedRoof = distanceSquaredLimit;
                if (roofMod) {
                    moddedRoof = distanceSquaredLimit + totalEffectiveness;
                }
                //being calculated column by column, so it's still fast
                //value required is squared, remember: not a fixed amount
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x + 0.5);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z + 0.5);
                    if (snowballLoc.distanceSquared(locationBuffer) < moddedRoof) {
                        final Block beingMined = world.getBlockAt(x, y, z);
                        final Material material = beingMined.getType();
                        if (material == Material.STONE //the simplest case: clear stone.
                                || (material == Material.NETHERRACK)
                                || (material == Material.ENDER_STONE)
                                || (material == Material.SAND)
                                || (material == Material.DIRT)
                                || (material == Material.SANDSTONE)
                                || (material == Material.COBBLESTONE)
                                || (material == Material.GRASS)
                                || (material == Material.GRAVEL)
                                || (material == Material.WATER && (baseTool > 1))
                                || (material == Material.STATIONARY_WATER && (baseTool > 1)) //better than stone and you can clear water
                                || (material == Material.LAVA && (baseTool > 2)) //better than iron and you can clear lava
                                || (material == Material.STATIONARY_LAVA && (baseTool > 2)) //all of these leave ores to mine
                                || (material == Material.COAL_ORE && (baseTool > 3)) //mining with gold pick means you want ores
                                || (material == Material.IRON_ORE && (baseTool > 3)) //mining with diamond pick clears iron and coal ores
                                || (material == Material.LAPIS_ORE && (baseTool > 3))
                                || (material == Material.GOLD_ORE && (baseTool > 3))
                                || (material == Material.REDSTONE_ORE && (baseTool > 3)) //diamond pick mining leaves no ores to clear
                                || (material == Material.EMERALD_ORE && (baseTool > 3))
                                || (material == Material.DIAMOND_ORE && (baseTool > 3))
                                || (material == Material.BEDROCK && (baseTool > 3) && (beingMined.getY() > 0))) //more means a bedrock flat floor) 
                        {
                            beingMined.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}