package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;

/**
 * This logic plants a tree when you use a sapling
 *
 * @author christopherjohnson
 */
public class ArboristSnowballLogic extends SnowballLogic {

    private final Material baseItem;
    private final int variation;

    public ArboristSnowballLogic(ItemStack trigger) {
        this.baseItem = Preconditions.checkNotNull(trigger.getType());
        this.variation = Preconditions.checkNotNull(trigger.getDurability());
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
            if (baseItem == Material.SAPLING) {

                switch (variation) {
                    case 0:
                        if (info.power > 5.5) {
                            block.getWorld().generateTree(block.getLocation(), TreeType.BIG_TREE);
                        } else {
                            block.getWorld().generateTree(block.getLocation(), TreeType.TREE);
                        }
                        break;
                    case 1:
                        if (info.power > 5.5) {
                            block.getWorld().generateTree(block.getLocation(), TreeType.MEGA_REDWOOD);
                        } else {
                            block.getWorld().generateTree(block.getLocation(), TreeType.REDWOOD);
                        }
                        break;
                    case 2:
                        if (info.power > 5.5) {
                            block.getWorld().generateTree(block.getLocation(), TreeType.TALL_BIRCH);
                        } else {
                            block.getWorld().generateTree(block.getLocation(), TreeType.BIRCH);
                        }
                        break;
                    case 3:
                        if (info.power > 5.5) {
                            block.getWorld().generateTree(block.getLocation(), TreeType.JUNGLE);
                        } else {
                            block.getWorld().generateTree(block.getLocation(), TreeType.SMALL_JUNGLE);
                        }
                        break;
                    case 4:
                        block.getWorld().generateTree(block.getLocation(), TreeType.ACACIA);
                        break;
                    case 5:
                        block.getWorld().generateTree(block.getLocation(), TreeType.DARK_OAK);
                        break;
                        //note: power over 4.5 means level 21 and up
                        //power over 5.5 means level 31 and up
                }
                loc.setY(loc.getY() - 1);
                if (block.getY() > 1) {
                    block = loc.getBlock();
                    block.setType(Material.DIRT);
                }
            }
        }

    }
}
