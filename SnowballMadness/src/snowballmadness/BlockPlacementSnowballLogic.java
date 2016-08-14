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
public class BlockPlacementSnowballLogic extends SnowballLogic {

    private final Material toPlace;
    private final short durability;

    public BlockPlacementSnowballLogic(Material toPlace, short durability) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
        this.durability = Preconditions.checkNotNull(durability);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();

        if (loc.getBlock().getType() == Material.AIR && loc.getY() > 1) {
            loc.setY(loc.getY() - 1);
        }

        if (loc.getBlock().getType() != Material.AIR) {
            loc.setY(loc.getY() + 1);
        }

        Block target = loc.getBlock();
        if (target.getType() == Material.AIR) {
            target.setType(toPlace);
            target.setData((byte)durability);
        }
    }
}
