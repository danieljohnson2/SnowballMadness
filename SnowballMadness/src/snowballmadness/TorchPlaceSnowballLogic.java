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

    private final Material toPlace;

    public TorchPlaceSnowballLogic(Material toPlace) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();

        Block target = loc.getBlock();
        if (target.getRelative(BlockFace.DOWN).getType().isSolid()) {
            if (target.getType() == Material.AIR) {
                target.setType(toPlace);
                if (target.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                    target.setData((byte) 4);
                }
                if (target.getRelative(BlockFace.NORTH).getType().isSolid()) {
                    target.setData((byte) 3);
                }
                if (target.getRelative(BlockFace.EAST).getType().isSolid()) {
                    target.setData((byte) 2);
                }
                if (target.getRelative(BlockFace.WEST).getType().isSolid()) {
                    target.setData((byte) 1);
                }
            }
        }
    }
}