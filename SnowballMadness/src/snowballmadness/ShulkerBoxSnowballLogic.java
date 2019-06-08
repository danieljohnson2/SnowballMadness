package snowballmadness;

import com.google.common.base.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.*;

/**
 * This logic spawns a firework from various triggers such as flower types
 *
 * @author DanJ
 */
public class ShulkerBoxSnowballLogic extends SnowballLogic {

    private final Material baseItem;
    private final ItemStack item;

    public ShulkerBoxSnowballLogic(ItemStack trigger) {
        this.baseItem = Preconditions.checkNotNull(trigger.getType());
        this.item = Preconditions.checkNotNull(trigger);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        Location loc = snowball.getLocation().clone();

        BlockStateMeta sourcemeta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox sourcebox = (ShulkerBox) sourcemeta.getBlockState();
        Inventory sourceinventory = sourcebox.getInventory();
        //so we have the inventory from the box we're holding

        Block target = loc.getBlock();
        target.setType(baseItem);
        //and we make a world block a shulker box

        ShulkerBox targetbox = (ShulkerBox) target.getState();
        targetbox.getInventory().setContents(sourcebox.getInventory().getContents());
        BlockStateMeta targetmeta = (BlockStateMeta) targetbox.getInventory();
        //and this accesses the world box and maybe it's the blockstatemeta that made it work?
        targetbox.update();
        //and now we can snowball up filled shulker boxes. woot!
    }
}
