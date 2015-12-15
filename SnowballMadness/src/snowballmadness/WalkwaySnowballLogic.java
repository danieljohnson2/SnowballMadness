package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;

/**
 * This logic makes walkways
 *
 * @author christopherjohnson
 */
public class WalkwaySnowballLogic extends SnowballLogic {

    private final Material edgeHalfHeight;
    private final int edgeVariation;
    private final int number;

    public WalkwaySnowballLogic(ItemStack trigger) {
        this.edgeHalfHeight = Preconditions.checkNotNull(trigger.getType());
        this.edgeVariation = Preconditions.checkNotNull(trigger.getDurability());
        this.number = Preconditions.checkNotNull(trigger.getAmount());
        //for these, we check the item and its datatype, but the trick for setting height is number of items in the stack
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        Location loc = snowball.getLocation().clone();
        Block block = loc.getBlock();
        World world = snowball.getWorld();

        final int radius = (int) (info.power * 2);
        final int diameter = (int) (info.power * 4);
        //double the area of a box because it's just flat

        final int beginX = loc.getBlockX() - radius;
        final int beginZ = loc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endZ = beginZ + diameter;
        final int y = (int) loc.getY() + (number - 3);
        //the math does this: numbers 1 is step down, 2 is level, 3 is step up and so on        
        // if this is less than zero we don't do anything, but we can set up the materials OK
        Material baseItem = Material.DOUBLE_STEP;
        int variation = edgeVariation;
        switch (edgeVariation) {
            case 0:
                variation = 8;
                break;
            case 1:
                variation = 9;
                break;
            case 7:
                variation = 15;
                break;
        } //stone slab seamless blocks, sandstone produces smooth double, quartz produces the tiled ceiling. All else as normal

        switch (number) {
            case 1:
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        Block target = world.getBlockAt(x, y, z);
                        target.setType(baseItem);
                        target.setData((byte) variation);
                        target = target.getRelative(BlockFace.UP);
                        if (halfstepDown(target) && target.getType() == Material.AIR) {
                            target.setType(edgeHalfHeight);
                            target.setData((byte) edgeVariation);
                        } //
                    }
                } //we've rapidly placed the main part of the pathway. We have one halfslab and are stepping down

                break;
            case 2:
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        Block target = world.getBlockAt(x, y, z);
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                } //we've rapidly placed the pathway. We have two halfslabs and are moving on the level.
                break;
            case 3:
                for (int x = beginX + 1; x <= (endX - 1); ++x) {
                    for (int z = beginZ + 1; z <= (endZ - 1); ++z) {
                        Block target = world.getBlockAt(x, y, z);
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                } //we've rapidly placed the main part of the pathway. We have three halfslabs and are stepping up
                for (int x = beginX; x <= endX; ++x) {
                    Block target = world.getBlockAt(x, y, beginZ);
                    if (target.getRelative(BlockFace.DOWN).getType() == Material.DOUBLE_STEP && halfstepDown(target)) {
                        target.setType(edgeHalfHeight);
                        target.setData((byte) edgeVariation);
                    } else {
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                    target = world.getBlockAt(x, y, endZ);
                    if (target.getRelative(BlockFace.DOWN).getType() == Material.DOUBLE_STEP && halfstepDown(target)) {
                        target.setType(edgeHalfHeight);
                        target.setData((byte) edgeVariation);
                    } else {
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                }
                for (int z = beginZ; z <= endZ; ++z) {
                    Block target = world.getBlockAt(beginX, y, z);
                    if (target.getRelative(BlockFace.DOWN).getType() == Material.DOUBLE_STEP && halfstepDown(target)) {
                        target.setType(edgeHalfHeight);
                        target.setData((byte) edgeVariation);
                    } else {
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                    target = world.getBlockAt(endX, y, z);
                    if (target.getRelative(BlockFace.DOWN).getType() == Material.DOUBLE_STEP && halfstepDown(target)) {
                        target.setType(edgeHalfHeight);
                        target.setData((byte) edgeVariation);
                    } else {
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                } //we've placed halfslabs only where there is an edge and a doublestep beneath it plus only one adjacent doublestep
                break;
            default:
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        Block target = world.getBlockAt(x, y, z);
                        target.setType(baseItem);
                        target.setData((byte) variation);
                    }
                } //we've rapidly placed the pathway. We have four or more halfslabs and are placing a ceiling.
                break;
        }

        //depending on the type of step we'll do different things with halfslabs based on the number in the stack
        //1 step down means we place halfslabs +1 where the block +1 is air, and there's exactly one DOUBLE_STEP adjacent to it
        //2 directly underfoot gives us two air blocks to walk through
        //3 step up means we place halfslabs on the edges if there's DOUBLE_STEP directly under, otherwise we place DOUBLE_STEP
        //4  this and higher place a pure DOUBLE_STEP edge around it. No halfslabs.


        //   if (block.getType() == Material.AIR) {
        //   }

    }

    protected boolean halfstepDown(Block target) {
        int neighborCount = 0;
        if (target.getRelative(BlockFace.SOUTH).getType() == Material.DOUBLE_STEP) {
            neighborCount += 1;
        }
        if (target.getRelative(BlockFace.NORTH).getType() == Material.DOUBLE_STEP) {
            neighborCount += 1;
        }
        if (target.getRelative(BlockFace.EAST).getType() == Material.DOUBLE_STEP) {
            neighborCount += 1;
        }
        if (target.getRelative(BlockFace.WEST).getType() == Material.DOUBLE_STEP) {
            neighborCount += 1;
        }
        return (neighborCount == 1);
        //true if exactly one neighbor adjacent is a DOUBLE_STEP
    }
}
