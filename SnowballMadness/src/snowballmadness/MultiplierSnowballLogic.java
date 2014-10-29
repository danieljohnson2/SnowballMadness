/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This snowball logic spawns a cluster of additional snowballs at impact, and
 * these may have a second logic attached to them (and that can be a multiplier
 * too, for total chaos!)
 *
 * @author DanJ
 */
public class MultiplierSnowballLogic extends SnowballLogic {

    private final int numberOfSnowballs;
    private final InventorySlice inventory;

    public MultiplierSnowballLogic(int numberOfSnowballs, InventorySlice inventory) {
        this.numberOfSnowballs = numberOfSnowballs;
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        World world = snowball.getWorld();
        LivingEntity shooter = info.shooter;
        Location source = snowball.getLocation().clone();
        source.setY(source.getY() + 0.25);

        for (int i = 0; i < numberOfSnowballs; ++i) {
            Snowball sb = world.spawn(source, Snowball.class);

            Vector vector = Vector.getRandom();
            vector.setX(vector.getX() - 0.5);
            vector.setZ(vector.getZ() - 0.5);
            vector.setY(0.25);
            vector = vector.multiply(new Vector(info.amplification, 1.0, info.amplification));

            sb.setVelocity(vector);

            performLaunch(inventory, sb, info);
        }
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%d) %s",
                super.toString(),
                numberOfSnowballs,
                createLogic(inventory));
    }
}
