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

        if (toPlace.name().endsWith("STEP")
                || toPlace.name().endsWith("SLAB")
                || toPlace.name().endsWith("SLAB2")) {
            if (loc.getBlock().getType() == Material.AIR && loc.getY() > 2) {
                loc.setY(loc.getY() - 1);
            }

            Block target = loc.getBlock();
            target.setType(toPlace);
            target.setData((byte) durability);

            Block scanOut = loc.getBlock();

            scanOut = target.getRelative(BlockFace.NORTH);
            if (scanOut.getType() == Material.AIR) {
                scanOut.setType(toPlace);
                scanOut.setData((byte) durability);
                scanOut = target.getRelative(BlockFace.NORTH_EAST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                }
                scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                    scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_EAST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                    }
                    scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                        scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_EAST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_EAST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                    }
                }
            }

            scanOut = target.getRelative(BlockFace.EAST);
            if (scanOut.getType() == Material.AIR) {
                scanOut.setType(toPlace);
                scanOut.setData((byte) durability);
                scanOut = target.getRelative(BlockFace.SOUTH_EAST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                }
                scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                    scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH_EAST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                    }
                    scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                        scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH_EAST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH_EAST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                    }
                }
            }

            scanOut = target.getRelative(BlockFace.SOUTH);
            if (scanOut.getType() == Material.AIR) {
                scanOut.setType(toPlace);
                scanOut.setData((byte) durability);
                scanOut = target.getRelative(BlockFace.SOUTH_WEST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                }
                scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                    scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH_WEST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                    }
                    scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                        scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH_WEST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH_WEST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                    }
                }
            }

            scanOut = target.getRelative(BlockFace.WEST);
            if (scanOut.getType() == Material.AIR) {
                scanOut.setType(toPlace);
                scanOut.setData((byte) durability);
                scanOut = target.getRelative(BlockFace.NORTH_WEST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                }
                scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
                if (scanOut.getType() == Material.AIR) {
                    scanOut.setType(toPlace);
                    scanOut.setData((byte) durability);
                    scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH_WEST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                    }
                    scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
                    if (scanOut.getType() == Material.AIR) {
                        scanOut.setType(toPlace);
                        scanOut.setData((byte) durability);
                        scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH_WEST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                        scanOut = target.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH_WEST);
                        if (scanOut.getType() == Material.AIR) {
                            scanOut.setType(toPlace);
                            scanOut.setData((byte) durability);
                        }
                    }
                }
            }

            //neat little grid for building floors and bridges and roads, under one's feet
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
