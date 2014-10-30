/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

/**
 *
 * @author DanJ
 */
public abstract class DurabilityDrainSnowballLogic extends SnowballLogic {

    private final Material drainedMaterial;
    private final InventorySlice inventory;

    public DurabilityDrainSnowballLogic(InventorySlice inventory) {
        this.inventory = Preconditions.checkNotNull(inventory);
        drainedMaterial = inventory.getBottomItem().getType();
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        ItemStack stack = inventory.getBottomItem();

        if (stack != null && stack.getType() == drainedMaterial) {
            short dur = stack.getDurability();
            dur += getDurabilityDrain();

            Bukkit.getLogger().info(String.format("Durability: %d", dur));

            if (dur > stack.getType().getMaxDurability()) {
                inventory.set(0, null);
            } else {
                stack = stack.clone();
                stack.setDurability(dur);
                inventory.set(0, stack);
            }
        }
    }
    
    protected int getDurabilityDrain() {
        return 16;
    }
}
