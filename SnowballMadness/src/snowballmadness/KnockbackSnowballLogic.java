package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This creates a knockback snowball logic given the material; we sometimes use a subclass for special effects.
 *
 * @param weaponUsed The item being used with the snowball.
 * @return The new logic.
 */
public class KnockbackSnowballLogic extends LingeringSnowballLogic<Entity> {

    private final Material weaponUsed;
    private Vector bounce;
    private double weapon;

    public KnockbackSnowballLogic(Material weaponUsed) {
        this.weaponUsed = Preconditions.checkNotNull(weaponUsed);
        //we won't need a 'hit' method as we are overriding damage
        switch (weaponUsed) {
            case BONE:
                weapon = 0.62;
                break;
            case FENCE:
                weapon = 0.65;
                break;
            case COBBLE_WALL:
                weapon = 0.68;
                break;
            case NETHER_FENCE:
                weapon = 0.7;
                break;
            default: //stick
                weapon = 0.6;
        }
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {

        weapon = weapon + ((Math.sqrt(Math.sqrt(info.power))-1)*0.23);
        //low power somewhat enhances stuff, mega power takes nether fence to silly levels
        bounce = target.getVelocity().clone();
        bounce.add(target.getLocation().toVector());
        bounce.subtract(snowball.getLocation().toVector());
        bounce.setY(Math.abs(bounce.getY())*0.15);
        bounce.normalize();

        beginLinger(info, 1, 8, target);
        return super.damage(snowball, info, target, proposedDamage);
    }

    @Override
    protected boolean linger(SnowballInfo info, int counter, Entity target) {
        bounce.multiply(weapon); //should be progressively diminishing this
        target.setVelocity(target.getVelocity().add(bounce));
        target.setFallDistance(0);
        return true;

    }
}
