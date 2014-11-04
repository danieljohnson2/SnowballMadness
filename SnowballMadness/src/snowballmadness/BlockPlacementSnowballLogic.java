package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic places on top of the one you hit with the snowball, replacing only
 * air with it.
 *
 * @author DanJ
 */
public class BlockPlacementSnowballLogic extends SnowballLogic {

    private final Material toPlace;

    public BlockPlacementSnowballLogic(Material toPlace) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();
        Block block = loc.getBlock();

        if (block.getType() == Material.AIR && block.getY() > 1) {
            loc.setY(loc.getY() - 1);
            block = loc.getBlock();
        }

        if (block.getType() != Material.AIR) {
            loc.setY(loc.getY() + 1);
            block = loc.getBlock();
        }

        if (block.getType() == Material.AIR) {
            block.setType(toPlace);
        }
    }
}
