package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic places on top of the one you hit with the snowball, replacing only air with it.
 *
 * @author DanJ
 */
public class TorchPlaceSnowballLogic extends SnowballLogic {

    public TorchPlaceSnowballLogic() {
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation();
        loc.setX(Math.floor(loc.getX() / 7) * 7);
        loc.setZ(Math.floor(loc.getZ() / 7) * 7);
        loc.setY(loc.getBlockY() + 1);
        Block target = loc.getBlock();
        if (target.getType() == Material.AIR) {
            target = target.getRelative(BlockFace.DOWN);
        }
        if (target.getType() == Material.AIR||
                target.getType() == Material.DOUBLE_PLANT) {
            target = target.getRelative(BlockFace.DOWN);
        }
        if (target.getType() == Material.AIR||
                target.getType() == Material.LEAVES||
                target.getType() == Material.LONG_GRASS||
                target.getType() == Material.DOUBLE_PLANT||
                target.getType() == Material.RED_ROSE||
                target.getType() == Material.YELLOW_FLOWER||
                target.getType() == Material.DEAD_BUSH) {
            target = target.getRelative(BlockFace.DOWN);
        }
        //this is primitive, but it starts with our location and goes to find a spot that's not air
        target = target.getRelative(BlockFace.UP);
        //having found this spot, we go up into the spot where a torch will be placed
        if (target.getRelative(BlockFace.DOWN).getType().isSolid()) {
            if (target.getType() == Material.AIR||
                target.getType() == Material.LEAVES||
                target.getType() == Material.LONG_GRASS||
                target.getType() == Material.DOUBLE_PLANT||
                target.getType() == Material.RED_ROSE||
                target.getType() == Material.YELLOW_FLOWER||
                target.getType() == Material.DEAD_BUSH) {
                target.setType(Material.TORCH);
            }
        }
        //and then, if the block under our magic spot is solid (and not say another torch) we torch.
    }
}