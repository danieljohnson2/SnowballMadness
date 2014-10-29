/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

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
    private final SnowballLogic nextLogic;

    public MultiplierSnowballLogic(int numberOfSnowballs, SnowballLogic nextLogic) {
        this.numberOfSnowballs = numberOfSnowballs;
        this.nextLogic = nextLogic; // can be null for plain snowballs
    }

    @Override
    public void hit() {
        super.hit();

        World world = getWorld();
        LivingEntity shooter = getShooter();
        Location source = getSnowball().getLocation().clone();
        source.setY(source.getY() + 0.25);

        for (int i = 0; i < numberOfSnowballs; ++i) {
            Snowball sb = world.spawn(source, Snowball.class);

            Vector vector = Vector.getRandom();
            vector.setX(vector.getX() - 0.5);
            vector.setZ(vector.getZ() - 0.5);
            vector.setY(0.5);

            sb.setVelocity(vector);

            performLaunch(nextLogic, sb, shooter);
        }
    }
}
