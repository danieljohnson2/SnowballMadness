/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

/**
 * This logic drops an item at the point of impact.
 *
 * @author DanJ
 */
public class ItemDropSnowballLogic extends SnowballLogic {

    private final ItemStack stack;

    public ItemDropSnowballLogic(Material toDrop, int amount) {
        this(new ItemStack(toDrop, amount));
    }

    public ItemDropSnowballLogic(ItemStack stack) {
        this.stack = Preconditions.checkNotNull(stack);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        snowball.getWorld().dropItem(snowball.getLocation(),
                stack.clone());
    }
}
