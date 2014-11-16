package snowballmadness;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * This logic mimics LinkedTrail, but it's for placing lily pads or other blocks
 * on water source blocks. Perhaps some of the halfslabs?
 *
 * @author christopherjohnson
 */
public class LinkedWaterTrailSnowballLogic extends LinkedTrailSnowballLogic {

    public LinkedWaterTrailSnowballLogic(Material toPlace) {
        super(toPlace);
    }

    @Override
    protected Block findTargetBlock(Location location) {
        Location target = location.clone();

        while (target.getBlock().getType() == Material.AIR) {
            target.add(0, -1, 0);
        }

        //chase down in the most primitive way possible
        if ((target.getBlock().getType() == Material.STATIONARY_WATER)) {
            target.add(0, 1, 0);
            return target.getBlock();
        }

        return null; // no water, no lilly!
    }
}
