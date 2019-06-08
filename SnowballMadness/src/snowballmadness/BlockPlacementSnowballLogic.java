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

        if (toPlace.name().endsWith("STEP") ||
                toPlace.name().endsWith("SLAB") ||
                toPlace.name().endsWith("SLAB2")) {
            if (loc.getBlock().getType() == Material.AIR && loc.getY() > 2) {
                loc.setY(loc.getY() - 1);
            }
            
            Block target = loc.getBlock();
            target.setType(toPlace);
            target.setData((byte) durability);
            target = loc.getBlock();
            target.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).setType(toPlace);
            target.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).setData((byte) durability);
            target.getRelative(BlockFace.NORTH).setType(toPlace);
            target.getRelative(BlockFace.NORTH).setData((byte) durability);
            target.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).setType(toPlace);
            target.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).setData((byte) durability);
            
            target.getRelative(BlockFace.EAST).setType(toPlace);
            target.getRelative(BlockFace.EAST).setData((byte) durability);
            target.setType(toPlace);
            target.setData((byte) durability);
            target.getRelative(BlockFace.WEST).setType(toPlace);
            target.getRelative(BlockFace.WEST).setData((byte) durability);
            
            target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).setType(toPlace);
            target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).setData((byte) durability);
            target.getRelative(BlockFace.SOUTH).setType(toPlace);
            target.getRelative(BlockFace.SOUTH).setData((byte) durability);
            target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).setType(toPlace);
            target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).setData((byte) durability);
            //neat little 3x3 for building floors and bridges and roads, under one's feet
            
        } else {

            if (loc.getBlock().getType() == Material.AIR && loc.getY() > 1) {
                loc.setY(loc.getY() - 1);
            }

            if (loc.getBlock().getType() != Material.AIR) {
                loc.setY(loc.getY() + 1);
            }

            Block target = loc.getBlock();
            if (target.getType() == Material.AIR) {
                target.setType(toPlace);
                target.setData((byte) durability);
            }
        }
    }
}
