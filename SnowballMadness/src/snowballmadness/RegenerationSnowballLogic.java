/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

/**
 * This logic regenerates any chunk it lands in, and also heals players it hits.
 * As a bonus gimme, if you are using a dirt block to create this, it will
 * convert that block to grass.
 *
 * @author DanJ
 */
public class RegenerationSnowballLogic extends SnowballLogic {

    private final InventorySlice inventory;

    public RegenerationSnowballLogic(InventorySlice inventory) {
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.setHealth(living.getMaxHealth());
        }

        return 0;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation();
        Chunk chunk = loc.getBlock().getChunk();
        chunk.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());

        ItemStack bottom = inventory.getBottomItem();

        if (bottom != null && bottom.getType() == Material.DIRT) {
            bottom.setType(Material.GRASS);
        }
    }
}
