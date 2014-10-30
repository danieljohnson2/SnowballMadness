/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.collect.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

/**
 * This snowball just does damage like a sword. Boring, but useful.
 *
 * @author DanJ
 */
public class SwordSnowballLogic extends DurabilityDrainSnowballLogic {

    private final double damage;

    public SwordSnowballLogic(InventorySlice inventory) {
        super(inventory);
        this.damage = damages.get(inventory.getBottomItem().getType());
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        return damage;
    }
    private static final Map<Material, Double> damages = ImmutableMap.<Material, Double>builder().
            put(Material.WOOD_SWORD, 5.0).
            put(Material.STONE_SWORD, 6.0).
            put(Material.IRON_SWORD, 7.0).
            put(Material.GOLD_SWORD, 5.0).
            put(Material.DIAMOND_SWORD, 8.0).
            build();
}