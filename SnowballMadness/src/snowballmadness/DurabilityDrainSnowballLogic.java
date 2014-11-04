package snowballmadness;

import com.google.common.base.Preconditions;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is base class for snowballs that use up an item when fired.
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

        // we must delay the durability drain slightly; inventory updates
        // don't work during launch.

        new BukkitRunnable() {
            @Override
            public void run() {
                applyDurabilityLoss();
            }
        }.runTaskLater(info.plugin, 1);
    }

    private void applyDurabilityLoss() {
        ItemStack stack = inventory.getBottomItem();

        if (stack != null && stack.getType() == drainedMaterial) {
            short dur = stack.getDurability();
            dur += getDurabilityDrain();

            Bukkit.getLogger().info(String.format("Durability: %d", dur));

            if (dur > stack.getType().getMaxDurability()) {
                inventory.set(0, null);
            } else {
                stack.setDurability(dur);
            }
        }
    }

    /**
     * This returns the amount of durability to take off; the total that can be
     * drained is dependant on the item type, but we default to 16, which is
     * equivalent to 16 normal uses.
     *
     * @return The number of uses drained from the item being used up.
     */
    protected int getDurabilityDrain() {
        return 16;
    }
}
