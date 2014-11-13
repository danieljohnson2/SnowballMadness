package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This logic mines blocks with picks, with a broad set of rules for what can be
 * replaced with air. It drops nothing, it's just a clear-things-out tool for
 * quickly exposing ores.
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
        int baseTool = 0;
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
            case WOOD_PICKAXE:
            //nothing
        }

        final int radius = (int) (Math.sqrt(baseTool * info.power) * baseTool);
        //size is heavily dependent on tool type, power expands so aggressively with
        //doubling that we must control it. Max will still be very huge.
        final int diameter = (int) (radius * 2);
        World world = snowball.getWorld();

        Location snowballLoc = snowball.getLocation().clone();
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(0, snowballLoc.getBlockY() - baseTool);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + radius);
        final int endZ = beginZ + diameter;

        for (int x = beginX; x < endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    Block beingMined = world.getBlockAt(x, y, z);
                    if (canMine(beingMined, baseTool * info.power)) {
                        beingMined.setType(Material.AIR);
                    }
                }
            }
        }
    }

    protected boolean canMine(Block target, double tool) {
        Material material = target.getType();
        //base tools:
        //0 = wood, 1 = stone, 2 = iron, 3 = gold, 4 = diamond
        //multiplied by: 1 for no power boosts at all
        //multiplied by: 9 for double glowstone
        //multiplied by: 16 for double nether star

        return target.getType() == Material.STONE //the simplest case: clear stone.

                || (material == Material.SAND && (tool > 1))
                || (material == Material.DIRT && (tool > 1))
                || (material == Material.SANDSTONE && (tool > 1))
                || (material == Material.COBBLESTONE && (tool > 1))
                || (material == Material.GRASS && (tool > 1))
                || (material == Material.GRAVEL && (tool > 1)) //better than wood, and you are clearing dirt and gravel etc.

                || (material == Material.WATER && (tool > 2))
                || (material == Material.STATIONARY_WATER && (tool > 2)) //better than plain iron and you can clear water

                || (material == Material.LAVA && (tool > 4))
                || (material == Material.STATIONARY_LAVA && (tool > 4)) //better than plain diamond and you can clear lava
                || (material == Material.COAL_ORE && (tool > 4))
                || (material == Material.IRON_ORE && (tool > 4)) //and you are not looking for mere iron or coal, pfaugh!

                || (material == Material.OBSIDIAN && (tool > 11)) //glowstone block diamond means you can clear obsidian
                || (material == Material.BEDROCK && (tool > 15) && (target.getY() > 0)) //more means a bedrock flat floor
                || (material == Material.BEDROCK && (tool > 63)) //double nether star diamond removes ALL!
                //this leaves a void floor beneath. OP tools are dangerous! you best be standing on some kind of ore!

                || (material == Material.DIAMOND_ORE && (tool < 2))
                || (material == Material.LAPIS_ORE && (tool < 2))
                || (material == Material.REDSTONE_ORE && (tool < 2))
                || (material == Material.EMERALD_ORE && (tool < 2)); //worse than plain iron and you lose valuables!


    }
}